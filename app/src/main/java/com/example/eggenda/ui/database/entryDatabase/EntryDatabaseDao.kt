package com.example.eggenda.ui.database.entryDatabase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDatabaseDao {

    // Quest Operations
    @Insert
    suspend fun insertQuest(quest: QuestEntry)

    @Query("SELECT * FROM quest_table")
    fun getAllQuests(): Flow<List<QuestEntry>>

    @Query("SELECT * FROM quest_table WHERE quest_title = :questTitle LIMIT 1") // Use correct column name
    suspend fun getQuestByTitle(questTitle: String): QuestEntry

    @Query("DELETE FROM quest_table WHERE quest_title = :questTitle") // Use correct column name
    suspend fun deleteQuest(questTitle: String)

    // Task Operations
    @Insert
    suspend fun insertTask(task: TaskEntry)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<TaskEntry>> // Returns all tasks in the database as a Flow

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks() // Deletes all tasks in the database

    @Query("DELETE FROM task_table WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long) // Deletes a specific task by ID

    @Query("SELECT COUNT(id) FROM task_table")
    suspend fun getTaskCount(): Int // Returns the count of all tasks in the database

    @Query("SELECT * FROM task_table WHERE quest_title = :questTitle") // Use correct column name
    fun getTasksByQuest(questTitle: String): Flow<List<TaskEntry>> // Returns tasks associated with a specific quest

    @Update
    suspend fun updateTask(task: TaskEntry) // Updates a specific task

    @Update
    suspend fun updateTasks(tasks: List<TaskEntry>) // Updates a list of tasks
}
