package com.example.eggenda.gamePetChoose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.gamePlay.unuse.petInfo

class GamePetChooseViewModel(private val maxAmountPets: SharedPreferenceManager) : ViewModel(){

    //getting Int array from pets Info
    private val _allPets  = MutableLiveData<IntArray>()
    val allPets : LiveData<IntArray> get() = _allPets

    //IntArray that to track the character appearance
    private val _isOwned = MutableLiveData<IntArray>()
    val isOwned : LiveData<IntArray> get() = _isOwned

    private val _selectedPets = MutableLiveData<MutableList<Int?>>(mutableListOf())
    val selectedPets: LiveData<MutableList<Int?>> get() = _selectedPets

    private val _currentSelectedPet = MutableLiveData<Int?>() // Null means no pet is selected
    val currentSelectedPet: LiveData<Int?> = _currentSelectedPet

    fun selectPet(petId: Int?) {
        _currentSelectedPet.value = petId
    }

    fun clearSelection() {
        _currentSelectedPet.value = null
    }



    fun updateList(newList: MutableList<Int?>){
        // Update the existing MutableList and notify observers
        _selectedPets.value = newList.toMutableList() // Create a new list to notify observers
    }

    fun isSelectionComplete(): Boolean {
        val getAmount = maxAmountPets.getPetsAmount()
        Log.d("View Model", "${getAmount}")
        return _selectedPets.value?.count { it != null } == getAmount
    }

    class GamePetChooseViewModelFactory(private val maxAmountPets: SharedPreferenceManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GamePetChooseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GamePetChooseViewModel(maxAmountPets) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}