package com.example.eggenda.ui.database.userDatabase


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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

    fun updatePoints(username: String, points: Int) {
        CoroutineScope(IO).launch {
            userDatabaseDao.updatePoints(username, points)
        }
    }

    suspend fun updateUsername(newUsername: String, id: String) {
        userDatabaseDao.updateUsername(newUsername, id)
    }
}

