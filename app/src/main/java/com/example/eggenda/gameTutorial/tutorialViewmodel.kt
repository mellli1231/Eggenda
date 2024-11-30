package com.example.eggenda.gameTutorial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class tutorialViewmodel: ViewModel() {
    private val _chosenTutID = MutableLiveData<Int>(0)

    val chosenTutID: LiveData<Int> get() = _chosenTutID

    fun getChosenTutID():Int{
        return _chosenTutID.value!!
    }

    fun subtractChosenTutID(){
        _chosenTutID.value = _chosenTutID.value?.minus(1)
    }

    fun addChosenTutID(){
        _chosenTutID.value = _chosenTutID.value?.plus(1)
    }

}