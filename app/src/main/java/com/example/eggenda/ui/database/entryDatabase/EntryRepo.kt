package com.example.eggenda.ui.database.entryDatabase

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class EntryRepo(private val entryDatabaseDao: EntryDatabaseDao) {
    val allTasks: Flow<List<TaskEntry>> = entryDatabaseDao.getAllTasks()

    fun insert(task: TaskEntry) { // Insert a task into the database
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.insertTask(task)
        }
    }

    fun delete(taskId: Long) { // Delete a task from the database
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.deleteTask(taskId)
        }
    }

    fun deleteAllTasks() { // Delete all tasks from the database
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.deleteAllTasks()
        }
    }

    fun getTaskCount() = runBlocking { // Get the number of tasks in the database
        val count = async {
            entryDatabaseDao.getTaskCount()
        }
        count.start()
        count.await()
    }
}