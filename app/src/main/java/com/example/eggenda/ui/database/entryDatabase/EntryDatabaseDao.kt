package com.example.eggenda.ui.database.entryDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDatabaseDao {
    @Insert
    suspend fun insertTask(task: TaskEntry)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<TaskEntry>>

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM task_table WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("SELECT COUNT(id) FROM task_table")
    suspend fun getTaskCount(): Int

    @Query("SELECT * FROM task_table WHERE quest_title != ''")
    fun getAllQuests(): Flow<List<TaskEntry>>

    @Query("SELECT * FROM task_table WHERE quest_title = :questTitle")
    fun getTasksByQuest(questTitle: String): List<TaskEntry>

    @Query("SELECT * FROM task_table WHERE quest_title = :questTitle")
    fun getTasksByQuestFlow(questTitle: String): Flow<List<TaskEntry>>

    @Query("SELECT * FROM task_table WHERE quest_title = :questTitle LIMIT 1")
    suspend fun getQuestByTitle(questTitle: String): TaskEntry

    @Query("DELETE FROM task_table WHERE quest_title = :questTitle")
    suspend fun deleteQuestAndTasks(questTitle: String)

    @Delete
    suspend fun deleteTask(task: TaskEntry)

    @Update
    suspend fun updateTasks(tasks: List<TaskEntry>)

    @Update
    suspend fun updateTask(task: TaskEntry)

    @Query("SELECT * FROM task_table WHERE quest_title = :questTitle")
    suspend fun getTasksByQuestList(questTitle: String): List<TaskEntry> // Retrieves all tasks for a given quest

    @Query("DELETE FROM task_table WHERE quest_title = :questTitle")
    suspend fun deleteAllTasksForQuest(questTitle: String) // Deletes all tasks for a given quest
}