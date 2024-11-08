package com.example.eggenda.gamePetChoose

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.gamePlay.petInfo

class GamePetChooseViewModel(private val maxAmountPets: SharedPreferences) : ViewModel(){

    private val _allPets  = MutableLiveData<List<Int>>()
    val allPets:LiveData<List<Int>> get() = _allPets

    private val _selectedPets = MutableLiveData<MutableList<Int?>>(mutableListOf())
    val selectedPets: LiveData<MutableList<Int?>> get() = _selectedPets

    private val inventory = petInfo()

    init{
        loadPetsImages()
    }

    private fun loadPetsImages(){
        val ids = inventory.getAllPetImageIds()
        _allPets.value = ids
    }

    fun updateList(newList: MutableList<Int?>){
        // Update the existing MutableList and notify observers
//        _selectedPets.value?.clear() // Clear existing values
//        _selectedPets.value?.addAll(newList) // Add the new list
//        _selectedPets.value = _selectedPets.value // This is to notify observers
        _selectedPets.value = newList.toMutableList() // Create a new list to notify observers
    }

    private fun getAmount(): Int {
        return maxAmountPets.getInt("max_key", 0)
    }

    fun isSelectionComplete(): Boolean {
        Log.d("View Model", "${getAmount()}")
        return _selectedPets.value?.count { it != null } == getAmount()
    }

    class GamePetChooseViewModelFactory(private val maxAmountPets: SharedPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GamePetChooseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GamePetChooseViewModel(maxAmountPets) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}