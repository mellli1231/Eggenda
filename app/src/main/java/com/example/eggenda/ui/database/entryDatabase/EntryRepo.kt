package com.example.eggenda.ui.database.entryDatabase

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class EntryRepo(private val entryDatabaseDao: EntryDatabaseDao) {

    // Tasks
    val allTasks: Flow<List<TaskEntry>> = entryDatabaseDao.getAllTasks()

    fun insert(task: TaskEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.insertTask(task)
        }
    }

    fun delete(taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.deleteTask(taskId)
        }
    }

    fun deleteAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.deleteAllTasks()
        }
    }

    fun getTaskCount(): Int = runBlocking {
        val count = async { entryDatabaseDao.getTaskCount() }
        count.start()
        count.await()
    }

    fun getTasksByQuest(questTitle: String): Flow<List<TaskEntry>> {
        return entryDatabaseDao.getTasksByQuestFlow(questTitle)
    }

    fun updateTask(task: TaskEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.updateTask(task)
        }
    }

    fun updateTasks(tasks: List<TaskEntry>) {
        CoroutineScope(Dispatchers.IO).launch {
            entryDatabaseDao.updateTasks(tasks)
        }
    }

    // Quests
//    val allQuests: Flow<List<QuestEntry>> = entryDatabaseDao.getAllQuests()

//    fun insertQuest(quest: QuestEntry) {
//        CoroutineScope(Dispatchers.IO).launch {
//            entryDatabaseDao.insertQuest(quest)
//        }
//    }

//    fun deleteQuest(questTitle: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            entryDatabaseDao.deleteQuest(questTitle)
//        }
//    }

//    suspend fun getQuestByTitle(questTitle: String): QuestEntry = withContext(Dispatchers.IO) {
//        entryDatabaseDao.getQuestByTitle(questTitle)
//    }
}
