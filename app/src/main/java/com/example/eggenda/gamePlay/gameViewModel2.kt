package com.example.eggenda.gamePlay
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class gameViewModel2 : ViewModel(){
    //
    private val _gameRunState = MutableLiveData<Int>(-2)
    private val _turn = MutableLiveData<Int>()
    private val _currentBossHp = MutableLiveData<Int>()
    val _petStatus = MutableLiveData<Array<petStatus?>>()
    private val _boardStatus = MutableLiveData<IntArray>()
    private val _deckStatus = MutableLiveData<IntArray>()
    private val _damageDealt = MutableLiveData<IntArray>()



    private val _damageReport = MutableLiveData<String>()

    private val _gameMessage = MutableLiveData<String>()
    private val _forceReturn = MutableLiveData<Boolean>()


    private val gameStart: LiveData<Int> get() = _gameRunState

    fun getGameRunState():Int{
        return _gameRunState.value!!
    }

    fun updateGameRunState(newRun: Int) {
        viewModelScope.launch {
            _gameRunState.value = newRun
        }
    }

    private val turn: LiveData<Int> get() = _turn

    fun getTurn():Int{
        return _turn.value!!
    }

    fun updateTurn(newTurn: Int) {
        viewModelScope.launch {
            _turn.value = newTurn
        }
    }


    private val currentBossHp: LiveData<Int> get() = _currentBossHp

    fun getCurrentBossHp():Int{
        return _currentBossHp.value!!
    }

    fun updateCurrentBossHp(newCurrentBossHp: Int) {
        viewModelScope.launch {
            _currentBossHp.value = newCurrentBossHp
        }
    }

    val gameMessage: LiveData<String> get() = _gameMessage
    fun updateGameMessage(newGameMessage:String) {
        viewModelScope.launch {
            _gameMessage.value = newGameMessage
        }
    }

    val forceReturn: LiveData<Boolean> get() = _forceReturn
    fun updateForceReturn(newValue: Boolean) {

        viewModelScope.launch {
            _forceReturn.value = newValue
        }
    }

    val damageDealt: LiveData<IntArray> get() = _damageDealt

    fun getDamageDealt():IntArray{
        return _damageDealt.value!!
    }

    fun updateDamageDealt(newDamageDealt: IntArray){
        viewModelScope.launch {
            _damageDealt.value = newDamageDealt.copyOf()
        }
    }

    val damageReport: LiveData<String> get() = _damageReport

    fun getDamageReport():String{
        return _damageReport.value!!
    }

    fun updateDamageReport(newDamageReport: String){
        viewModelScope.launch {
            _damageReport.value = newDamageReport
        }
    }



    private val petStatus: LiveData<Array<petStatus?>> get() = _petStatus


    fun getPetStatus():Array<petStatus?>{
        return _petStatus.value!!.copyOf()
    }

    fun updatePetStatus(newPetStatus: Array<petStatus?>){
        viewModelScope.launch {
            _petStatus.value = newPetStatus.copyOf()
        }
    }

    val deckStatus: LiveData<IntArray> get() = _deckStatus

    fun getDeckStatus():IntArray{
        return _deckStatus.value!!
    }
    fun updateDeckStatus(newDeckStatus:IntArray){

        viewModelScope.launch {
            _deckStatus.value = newDeckStatus.copyOf()
        }
    }


    val boardStatus: LiveData<IntArray> get() = _boardStatus
    fun getBoardStatus():IntArray{
        return _boardStatus.value!!
    }

    fun updateBoardStatus(newBoardStatus:IntArray) {
        viewModelScope.launch {
            _boardStatus.value = newBoardStatus.copyOf()
        }
    }



}