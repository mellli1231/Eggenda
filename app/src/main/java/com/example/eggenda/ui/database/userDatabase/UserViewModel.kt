package com.example.eggenda.ui.database.userDatabase


import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class UserViewModel(private val repository: UserRepository): ViewModel() {
    val allUsersLiveData: LiveData<List<User>> = repository.allUsers.asLiveData()

    suspend fun insert(user: User) {
        repository.insert(user)
    }

    fun deleteAll(){
        val entryList = allUsersLiveData.value
        if (!entryList.isNullOrEmpty())
            repository.deleteAll()
    }

    fun getUser(user: String): User? {
        return repository.getUser(user)
    }

    fun updatePoints(username: String, points: Int) {
        repository.updatePoints(username, points)
    }

    suspend fun updateUsername(newUsername: String, id: String) {
        repository.updateUsername(newUsername, id)
    }
}

class UserViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java))
            return UserViewModel(repository) as T
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}
