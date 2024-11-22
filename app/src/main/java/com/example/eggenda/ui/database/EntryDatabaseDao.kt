package com.example.eggenda.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
}