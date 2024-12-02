package com.example.eggenda.gameMonsterChoose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eggenda.gamePlay.dict
import kotlinx.coroutines.launch

class monsterChooseViewModel : ViewModel(){
    private val _chosenStageID = MutableLiveData<Int>(0)
    private val _amount = MutableLiveData(3)

    val chosenStageID: LiveData<Int> get() = _chosenStageID

    fun getChosenStageID():Int{
        return _chosenStageID.value!!
    }

    fun updateChosenStageID(newStageId: Int) {
        _chosenStageID.value = newStageId
    }

    fun subtractChosenStageID(){
        _chosenStageID.value = _chosenStageID.value?.minus(1)
    }

    fun addChosenStageID(){
        _chosenStageID.value = _chosenStageID.value?.plus(1)
    }

    fun getAmount(): Int{
        return _amount.value!!
    }

    fun updateAmount(newAmount: Int){
        _amount.value = newAmount
    }



}