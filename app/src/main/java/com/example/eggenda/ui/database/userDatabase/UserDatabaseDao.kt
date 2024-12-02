package com.example.eggenda.ui.database.userDatabase


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDatabaseDao {
    @Insert
    suspend fun insertUser(user: User)


    @Query("DELETE FROM user_table")
    suspend fun deleteAll()


    @Query("UPDATE user_table SET points = points + :addedPoints WHERE id = :id")
    suspend fun updatePoints(id: String, addedPoints: Int)


    @Query("UPDATE user_table SET username = :newUsername WHERE id = :id")
    suspend fun updateUsername(newUsername: String, id: String)


    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>


    @Query("SELECT * FROM user_table WHERE username = :username")
    fun getUser(username: String): User?
}
