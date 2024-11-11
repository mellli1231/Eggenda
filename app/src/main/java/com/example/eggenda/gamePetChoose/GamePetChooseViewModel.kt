package com.example.eggenda.gamePetChoose

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.gamePlay.petInfo

class GamePetChooseViewModel(private val maxAmountPets: SharedPreferenceManager) : ViewModel(){

    //getting Int array from pets Info
    private val _allPets  = MutableLiveData<IntArray>()
    val allPets : LiveData<IntArray> get() = _allPets

    //IntArray that to track the character appearance
    private val _isOwned = MutableLiveData<IntArray>()
    val isOwned : LiveData<IntArray> get() = _isOwned

    private val _selectedPets = MutableLiveData<MutableList<Int?>>(mutableListOf())
    val selectedPets: LiveData<MutableList<Int?>> get() = _selectedPets

    //

    private val inventory = petInfo()

//    init{
//        loadPetsImages()
//    }

    //function to UPDATE the character appearance
//    fun updateAppearance (id: Int, hasOwned : Int){
//        _isOwned.value?.let { array ->
//            val updatedArray = array.copyOf()
//            updatedArray[id] = if(hasOwned == 1) 1 else 0
//            _isOwned.value = updatedArray
//        }
//    }

//    //function that to get the owned pets only
//    fun getAppearedPets() : List<Int>{
//        val pets = _allPets.value?: return emptyList()
//        val isOwned = _isOwned.value?: return emptyList()
//        return pets.filterIndexed { index, _-> isOwned.getOrNull(index) == 1 }
//    }

    //    private fun loadPetsImages(){
//        val ids = petInfo.getAllPetImageIds()
//        _allPets.value = ids
//    }
//
    fun updateList(newList: MutableList<Int?>){
        // Update the existing MutableList and notify observers
        _selectedPets.value = newList.toMutableList() // Create a new list to notify observers
    }

//    private fun getAmount(): Int {
//        return maxAmountPets.getInt("max_key", 0)
//    }

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