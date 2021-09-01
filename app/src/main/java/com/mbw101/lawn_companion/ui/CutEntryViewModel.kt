package com.mbw101.lawn_companion.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.DatabaseBuilder
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
        val dao = DatabaseBuilder.getInstance(application).cutEntryDao()
        repository = CutEntryRepository(dao)
    }

    fun addEntry(entry: CutEntry) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.addCut(entry)
        }
    }

<<<<<<< HEAD
    fun getCuts() = repository.getCuts()
=======
>>>>>>> develop
    fun getSortedCuts() = repository.getSortedCuts()
    suspend fun deleteCuts(vararg cuts: CutEntry) = repository.deleteCuts(*cuts)
}