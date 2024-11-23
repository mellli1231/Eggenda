package com.example.eggenda.ui.database.userDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository (private val userDatabaseDao: UserDatabaseDao){
    val allUsers: Flow<List<User>> = userDatabaseDao.getAllUsers()

    suspend fun insert(user: User): Long {
        return userDatabaseDao.insertUser(user)
    }

    suspend fun deleteAll() {
        userDatabaseDao.deleteAll()
    }

    fun getUser(username: String): User? {
        return userDatabaseDao.getUser(username)
    }

    fun updatePoints(username: String, points: Int) {
        CoroutineScope(IO).launch {
            userDatabaseDao.updatePoints(username, points)
        }
    }

    suspend fun userExists(username: String): Boolean {
        return userDatabaseDao.userExists(username)
    }

    fun updateUsername(newUsername: String, oldUsername: String) {
        CoroutineScope(IO).launch {
            userDatabaseDao.updateUsername(newUsername, oldUsername)
        }
    }
}