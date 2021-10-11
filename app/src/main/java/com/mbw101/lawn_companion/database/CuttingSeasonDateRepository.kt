package com.mbw101.lawn_companion.database

import android.content.Context
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-10-10
 */
@Singleton
class CuttingSeasonDateRepository @Inject constructor(private val cuttingSeasonDateDao: CuttingSeasonDatesDao) {
    suspend fun getStartDate() = cuttingSeasonDateDao.getStartDate()
    suspend fun getEndDate() = cuttingSeasonDateDao.getEndDate()
    suspend fun insertStartDate(startDate: Calendar) = cuttingSeasonDateDao.insertStartDate(startDate)
    suspend fun insertEndDate(startDate: Calendar) = cuttingSeasonDateDao.insertEndDate(startDate)
}

fun setupCuttingSeasonDateRepository(context: Context): CuttingSeasonDateRepository {
    val dao = AppDatabaseBuilder.getInstance(context).cuttingSeasonDatesDao()
    return CuttingSeasonDateRepository(dao)
}