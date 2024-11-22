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

//    class GalleryViewModelFactory(private val maxAmountPets: SharedPreferenceManager) : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
//                @Suppress("UNCHECKED_CAST")
//                return GamePetChooseViewModel(maxAmountPets) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }

}