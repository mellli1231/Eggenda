package com.example.eggenda.ui.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class EntryViewModel(private val repo: EntryRepo) : ViewModel() {
    val allEntriesLiveData: LiveData<List<TaskEntry>> = repo.allTasks.asLiveData()

    fun insert(entryInfo: TaskEntry) {
        repo.insert(entryInfo)
    }

    fun delete(id: Long){
        repo.delete(id)
    }

    fun deleteAll(){
        val entryList = allEntriesLiveData.value
        if (!entryList.isNullOrEmpty())
            repo.deleteAllTasks()
    }

    fun getSize() : Int{
        val entryList = allEntriesLiveData.value
        if (entryList != null) {
            return entryList.size
        }
        return 10
    }
}

class EntryViewModelFactory (private val repo: EntryRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if (modelClass.isAssignableFrom(EntryViewModel::class.java))
            return EntryViewModel(repo) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}