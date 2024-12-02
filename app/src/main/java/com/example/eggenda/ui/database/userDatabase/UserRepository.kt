package com.example.eggenda.ui.database.userDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository (private val userDatabaseDao: UserDatabaseDao){
    val allUsers: Flow<List<User>> = userDatabaseDao.getAllUsers()


    suspend fun insert(user: User){
        userDatabaseDao.insertUser(user)
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            userDatabaseDao.deleteAll()
        }
    }

    fun getUser(username: String): User? {
        return userDatabaseDao.getUser(username)
    }

    fun updatePoints(id: String, points: Int) {
        CoroutineScope(IO).launch {
            userDatabaseDao.updatePoints(id, points)
        }
    }

    suspend fun updateUsername(newUsername: String, id: String) {
        userDatabaseDao.updateUsername(newUsername, id)
    }
}

