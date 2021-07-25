package com.mbw101.lawn_companion.database

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
    fun getLastCut() = cutEntryDAO.getLastCut()
    suspend fun getLastCutSync() = cutEntryDAO.getLastCutSync()
    fun getCutsByMonth(monthNum: Int) = cutEntryDAO.findByMonthNum(monthNum)
    suspend fun addCut(cutEntry: CutEntry) = cutEntryDAO.insertAll(cutEntry)
    suspend fun deleteCuts(vararg cuts: CutEntry) = cutEntryDAO.deleteCuts(*cuts)
}