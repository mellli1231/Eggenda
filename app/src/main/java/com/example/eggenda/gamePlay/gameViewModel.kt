package com.example.eggenda.gamePlay
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class gameViewModel : ViewModel(){
    private val _turn = MutableLiveData<Int>()
    private val _allowPick = MutableLiveData<Boolean>()
    private val _forceReturn = MutableLiveData<Boolean>()
    private val _chosenPet = MutableLiveData<IntArray>()
    private val _petStatus = MutableLiveData<Array<petStatus?>>()


    private val _deckStatus = MutableLiveData<IntArray>()
    private val _boardStatus = MutableLiveData<IntArray>()
    private val _damageDealt = MutableLiveData<IntArray>()

    private val _damageReport = MutableLiveData<String>()

    private val _currentBossHp = MutableLiveData<Int>()

    private val _gameMessage = MutableLiveData<String>()




    fun init(newCurrentBossHp: Int, initAllowPick:Int, initChosenPet: IntArray, newPetStatus: Array<petStatus?>, initBoard:IntArray, gameObj:String){
        initTurn()
        initDamageDealt()
        updateCurrentBossHp(newCurrentBossHp)
        updateAllowPick(true)
        updateForceReturn(false)
        updateChosenPet(initChosenPet)
        updatePetStatus(newPetStatus)
        updateDeckStatus(IntArray(5){dict.hasPet})
        updateBoardStatus(initBoard)
        updateGameMessage(gameObj)
    }

    val turn: LiveData<Int> get() = _turn
    fun initTurn(){
        viewModelScope.launch {
            _turn.value = 0
        }
    }
    fun updateTurn() {
        viewModelScope.launch {
            _turn.value = _turn.value?.plus(1)
        }
    }

    val gameMessage: LiveData<String> get() = _gameMessage

    fun updateGameMessage(newGameMessage:String) {
        viewModelScope.launch {
            _gameMessage.value = newGameMessage
        }
    }

    val allowPick: LiveData<Boolean> get() = _allowPick
    fun updateAllowPick(newValue: Boolean) {

        viewModelScope.launch {
            _allowPick.value = newValue
        }
    }

    val forceReturn: LiveData<Boolean> get() = _forceReturn
    fun updateForceReturn(newValue: Boolean) {

        viewModelScope.launch {
            _forceReturn.value = newValue
        }
    }

    val damageDealt: LiveData<IntArray> get() = _damageDealt

    fun initDamageDealt(){
        viewModelScope.launch {
            _damageDealt.value = IntArray(3){0}
        }
    }

    fun getDamageDealt():IntArray{
        return _damageDealt.value!!
    }

    fun updateDamageDealt(newDamageDealt: IntArray){
        viewModelScope.launch {
            _damageDealt.value = newDamageDealt.copyOf()
        }
    }

    val damageReport: LiveData<String> get() = _damageReport

    fun initDamageReport(){
        viewModelScope.launch {
            _damageReport.value = ""
        }
    }

    fun getDamageReport():String{
        return _damageReport.value!!
    }

    fun updateDamageReport(newDamageReport: String){
        viewModelScope.launch {
            _damageReport.value = newDamageReport
        }
    }

    val currentBossHp: LiveData<Int> get() = _currentBossHp

    fun getCurrentBossHp():Int{
        return _currentBossHp.value!!
    }

    fun updateCurrentBossHp(newCurrentBossHp: Int) {
        viewModelScope.launch {
            _currentBossHp.value = newCurrentBossHp
        }
    }


    val chosenPet: LiveData<IntArray> get() = _chosenPet
    fun updateChosenPet(newChosenPet: IntArray) {
        viewModelScope.launch {
            _chosenPet.value = newChosenPet
        }
    }
    fun getChosenPet():IntArray{
        return _chosenPet.value!!
    }

    private val petStatus: LiveData<Array<petStatus?>> get() = _petStatus


    fun getPetStatus():Array<petStatus?>{
        return _petStatus.value!!
    }

    fun updatePetStatus(newPetStatus: Array<petStatus?>){
        viewModelScope.launch {
            _petStatus.value = newPetStatus.copyOf()
        }
    }

    val deckStatus: LiveData<IntArray> get() = _deckStatus
    fun updateDeckStatus(newDeckStatus:IntArray){

        viewModelScope.launch {
            _deckStatus.value = newDeckStatus.copyOf()
        }
//        triggerDeckUpdate()
    }

    fun getDeckStatus():IntArray{
        return _deckStatus.value!!
    }

//    fun updateDeckStatus(pos: Int, newVal: Int) {
//        viewModelScope.launch {
//            _deckStatus.value?.let {
//                it[pos] = newVal // Update the value in the array
//                _boardStatus.postValue(it) // Post the updated array to LiveData
//            }
//        }
//    }

    fun triggerDeckUpdate(){
        viewModelScope.launch {
            _deckStatus.value = _deckStatus.value?.copyOf()
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

    fun refreshBoard(){
        viewModelScope.launch {
            _boardStatus.value = _boardStatus.value!!.copyOf()
        }
    }

}