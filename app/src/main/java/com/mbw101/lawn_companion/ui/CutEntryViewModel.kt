package com.mbw101.lawn_companion.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-26
 */
class CutEntryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CutEntryRepository

    init {
        val dao = AppDatabaseBuilder.getInstance(application).cutEntryDao()
        repository = CutEntryRepository(dao)
    }

    fun addEntry(entry: CutEntry) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.addCut(entry)
        }
    }

    fun updateEntries(vararg entries: CutEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCuts(*entries)
        }
    }

    fun getSortedCuts() = repository.getSortedCuts()
    suspend fun getEntriesFromSpecificYearSorted(year: Int) = repository.getCutsByYearSorted(year)
    suspend fun deleteCutById(id: Int) = repository.deleteCutById(id)
    suspend fun hasCutEntry(entry: CutEntry) = repository.hasCutEntry(entry)
}