package com.example.eggenda.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.gamePetChoose.GamePetChooseViewModel
import com.example.eggenda.gamePetChoose.SharedPreferenceManager

class GalleryViewModel : ViewModel() {
    //getting Int array from pets Info
    private val _allPets  = MutableLiveData<IntArray>()
    val allPets : LiveData<IntArray> get() = _allPets

    private val _currentSelectedPet = MutableLiveData<Int?>() // Null means no pet is selected
    val currentSelectedPet: LiveData<Int?> = _currentSelectedPet

    private val _allMonster  = MutableLiveData<IntArray>()
    val allMonster : LiveData<IntArray> get() = _allMonster

    private val _currentSelectedMonster = MutableLiveData<Int?>() // Null means no pet is selected
    val currentSelectedMonster: LiveData<Int?> = _currentSelectedMonster

    fun selectPet(petId: Int?) {
        _currentSelectedPet.value = petId
    }

    fun selectMonster(monsterId: Int?) {
        _currentSelectedMonster.value = monsterId
    }

    fun clearSelectionPets() {
        _currentSelectedPet.value = null
    }

    fun clearSelectionMonster() {
        _currentSelectedMonster.value = null
    }

}