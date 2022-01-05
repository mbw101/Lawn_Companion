package com.mbw101.lawn_companion.database

import android.content.Context
import com.mbw101.lawn_companion.utils.UtilFunctions
import javax.inject.Inject
import javax.inject.Singleton

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-29
 */
@Singleton
class CutEntryRepository @Inject constructor(private val cutEntryDAO: CutEntryDAO) {
    // handles calling the DAO methods
    fun getCuts() = cutEntryDAO.getAllCuts()
    fun getSortedCuts() = cutEntryDAO.getAllCutsSorted()
    suspend fun getLastCut() = cutEntryDAO.getLastCut()
    suspend fun getLastCutSync() = cutEntryDAO.getLastCutAsync()
    suspend fun getCutsByMonth(monthNum: Int) = cutEntryDAO.findByMonthNum(monthNum)
    suspend fun getCutsByYearSorted(year: Int) = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(year)
    suspend fun getLastEntryFromSpecificYear(year: Int) = cutEntryDAO.getLastEntryFromSpecificYear(year)
    suspend fun addCut(cutEntry: CutEntry) = cutEntryDAO.insertAll(cutEntry)
    suspend fun deleteCuts(vararg cuts: CutEntry) = cutEntryDAO.deleteCuts(*cuts)
    suspend fun deleteAllCuts() = cutEntryDAO.deleteAll()
    suspend fun getYearDropdownArray() = cutEntryDAO.getYearDropdownArray()

    suspend fun hasANewYearOccurredSinceLastCut(): Boolean {
        val currentYearCuts = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(UtilFunctions.getCurrentYear())
        val lastYearCuts = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(UtilFunctions.getCurrentYear() - 1)

        if (currentYearCuts.isEmpty() && lastYearCuts.isNotEmpty()) {
            return true
        }
        return false
    }
}

fun setupCutEntryRepository(context: Context): CutEntryRepository {
    val dao = AppDatabaseBuilder.getInstance(context).cutEntryDao()
    return CutEntryRepository(dao)
}