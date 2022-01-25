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
    suspend fun getLastCut() = cutEntryDAO.getMostRecentCut()
    suspend fun getLastCutSync() = cutEntryDAO.getMostRecentCutAsync()
    suspend fun getCutsByMonth(monthNum: Int) = cutEntryDAO.findByMonthNum(monthNum)
    suspend fun getCutsByYearSorted(year: Int) = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(year)
    suspend fun getLastEntryFromSpecificYear(year: Int) = cutEntryDAO.getLastEntryFromSpecificYear(year)
    suspend fun addCut(cutEntry: CutEntry) = cutEntryDAO.insertAll(cutEntry)
    suspend fun deleteCutById(id: Int) = cutEntryDAO.deleteCutById(id)
    suspend fun deleteAllCuts() = cutEntryDAO.deleteAll()
    suspend fun getYearDropdownArray() = cutEntryDAO.getYearDropdownArray()
    suspend fun updateCuts(vararg cutEntries: CutEntry) = cutEntryDAO.updateCut(*cutEntries)

    suspend fun hasANewYearOccurredSinceLastCut(): Boolean {
        val currentYearCuts = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(UtilFunctions.getCurrentYear())
        val lastYearCuts = cutEntryDAO.getEntriesFromSpecificYearSortedAsync(UtilFunctions.getCurrentYear() - 1)

        if (currentYearCuts.isEmpty() && lastYearCuts.isNotEmpty()) {
            return true
        }
        return false
    }

    suspend fun hasCutEntry(entry: CutEntry) = cutEntryDAO.hasExistingCut(entry)
}

fun setupCutEntryRepository(context: Context): CutEntryRepository {
    val dao = AppDatabaseBuilder.getInstance(context).cutEntryDao()
    return CutEntryRepository(dao)
}