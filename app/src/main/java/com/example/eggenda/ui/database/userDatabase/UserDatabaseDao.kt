package com.example.eggenda.ui.database.userDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDatabaseDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Query("UPDATE user_table SET points = points + :addedPoints WHERE username = :username")
    suspend fun updatePoints(username: String, addedPoints: Int)

    @Query("UPDATE user_table SET username = :newUsername WHERE username = :oldUsername")
    suspend fun updateUsername(newUsername: String, oldUsername: String)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user_table WHERE username = :username")
    fun getUser(username: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM user_table WHERE username = :username)")
    suspend fun userExists(username: String): Boolean
}