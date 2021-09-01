package com.mbw101.lawn_companion.notifications

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.DatabaseBuilder
import com.mbw101.lawn_companion.utils.Constants


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-10
 */

// used for getting the latest cut from DAO for our broadcast receiver
class DatabaseWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    var context = appContext

    companion object {
        const val EMPTY_MESSAGE = "emptyMessage"
        const val FULL_MESSAGE = "fullMessage"
        const val HAS_CUT_KEY = "hasCutEntry"
        const val MONTH_KEY = "monthKey"
        const val DAY_KEY = "monthKey"

        // static method that runs our doWork method
        fun run() : LiveData<WorkInfo> {
            val work = OneTimeWorkRequestBuilder<DatabaseWorker>().build()
            WorkManager.getInstance().enqueue(work)

            return WorkManager.getInstance().getWorkInfoByIdLiveData(work.id)
        }
    }

    override suspend fun doWork(): Result {
        // check the database to get last cut
        val repository = CutEntryRepository(DatabaseBuilder.getInstance(context).cutEntryDao())
        var returnDataBuilder: Data.Builder = Data.Builder()
        val observer: Observer<CutEntry> = Observer { lastCut ->
            Log.d(Constants.TAG, "Cut Entry = $lastCut")

            // TODO: Fix last cut entry being null

            // TODO: Figure out how to communicate that lastCut is null in the Data

            // build Data object based on this last cut
            returnDataBuilder = Data.Builder()
                .putInt(MONTH_KEY, lastCut?.month_number ?: 0)
                .putInt(DAY_KEY, lastCut?.day_number  ?: 0)
                .putString(HAS_CUT_KEY,
                    if (lastCut == null) {
                        EMPTY_MESSAGE
                    }
                    else {
                        FULL_MESSAGE
                    }
                )
        }
//        val lastCut: CutEntry? =
        val liveData: LiveData<CutEntry> = repository.getLastCut()
        liveData.observeForever(observer)
        liveData.removeObserver(observer)

        return Result.success(returnDataBuilder.build())

//        if (lastCut == null) { // we know they haven't added a cut, so don't send notification
//            return Result.success();
//        }
//
//        var cutDate = Calendar.getInstance()
//        cutDate.set(Calendar.MONTH, lastCut.month_num + 1)
//        cutDate.set(Calendar.DAY_OF_MONTH, lastCut.day_number)
//        val daysSince = UtilFunctions.getNumDaysSince(cutDate)
//
//        if (daysSince >= 7) {
//            // start alarm manager
//            val intent = Intent(applicationContext, AlarmReceiver::class.java)
//            applicationContext.sendBroadcast(intent)
//
//            // TODO: Get the weather data and factor that into the notification condition
//
//        }
//
//        return Result.success()
    }

    // calls the alarm manager when the last cut via the DAO
    // has surpassed the 7 day limit and in the future, accessing the weather data
}