package com.mbw101.lawn_companion.database

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */
@Singleton
class LawnLocationRepository @Inject constructor(private val lawnLocationDAO: LawnLocationDAO) {
    // handles calling the DAO methods
    fun getLocation() = lawnLocationDAO.getFirstLocation()
    suspend fun getNumEntries() = lawnLocationDAO.getNumEntries()
    suspend fun hasALocationSaved(): Boolean {
        if (lawnLocationDAO.getNumEntries() >= 1) return true
        return false
    }
    suspend fun addLocation(lawnLocation: LawnLocation) = lawnLocationDAO.insertAll(lawnLocation)
    suspend fun deleteCuts(vararg locations: LawnLocation) = lawnLocationDAO.deleteLocations(*locations)
    suspend fun deleteAllCuts() = lawnLocationDAO.deleteAll()
}

fun setupLawnLocationRepository(context: Context): LawnLocationRepository {
    val dao = AppDatabaseBuilder.getInstance(context).lawnLocationDao()
    return LawnLocationRepository(dao)
}