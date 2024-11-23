package com.example.eggenda.ui.database.userDatabase

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class UserViewModel(private val repository: UserRepository): ViewModel() {
    val allUsersLiveData: LiveData<List<User>> = repository.allUsers.asLiveData()

    suspend fun insert(user: User): Long {
        return repository.insert(user)
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }

    fun getUser(user: String): User? {
        return repository.getUser(user)
    }

    fun updatePoints(username: String, points: Int) {
        repository.updatePoints(username, points)
    }

    fun updateUsername(newUsername: String, oldUsername: String) {
        repository.updateUsername(newUsername, oldUsername)
    }

    suspend fun userExists(username: String): Boolean {
        return repository.userExists(username)
    }
}

class UserViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java))
            return UserViewModel(repository) as T
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}