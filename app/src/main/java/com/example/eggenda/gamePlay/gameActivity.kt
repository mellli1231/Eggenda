package com.example.eggenda.gamePlay

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class gameActivity : AppCompatActivity() {
    private var customDialog: AlertDialog? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val viewModel: gameViewModel by viewModels()
    private  var selectedStage:Int = 0
    private lateinit var initBoard: IntArray
    private var boardRow = -1
    private var boardCol = -1
    private var boardSize = -1
    private var deckSize = 5
    private lateinit var stageInfo:stageInfo


    private lateinit var chosenPetId:IntArray

    private lateinit var petInfo: petInfo

    private lateinit var turnView: TextView
    private lateinit var bossView: ImageView
    private lateinit var hpFractionView: TextView
    private lateinit var hpBarView: ImageView
    private var hpBarLength:Int = 0

    private lateinit var boardRecyclerView: RecyclerView
    private lateinit var boardAdapter: boardAdapter

    private lateinit var unitRecyclerView: RecyclerView
    private lateinit var deckAdapter: deckAdapter


    private var selectedPetOrder: Int = -1
    private var newSelectedPetOrder: Int = -1

    private var petClicked: Int = 0

    private var selectedGird: Int = -1
    private var boardClicked: Int = 0

    private lateinit var resumeBtn: Button
    private lateinit var checkBtn: Button
    private lateinit var damageReportView: TextView
    private lateinit var fireCountView: TextView
    private lateinit var waterCountView: TextView
    private lateinit var forestCountView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        selectedStage = 0

        chosenPetId = intArrayOf(0,1,2,3,4)
        boardRow = 3
        boardCol = 5
        boardSize = boardRow * boardCol

        initBoard = IntArray(boardSize) { dict.noPet }

        petInfo = petInfo()
        stageInfo = stageInfo()

        turnView = findViewById(R.id.turnView)
        bossView = findViewById(R.id.bossView)
        hpFractionView = findViewById(R.id.hp_fraction)
        hpBarView = findViewById(R.id.hp_bar)
        hpBarView.post{
            hpBarLength = hpBarView.layoutParams.width
            Log.d("hp", "len${hpBarView.layoutParams.width}")
        }

        boardRecyclerView = findViewById(R.id.boardRecyclerView)
        boardRecyclerView.layoutManager = GridLayoutManager(this, boardCol) // 5 columns
        boardAdapter = boardAdapter(boardSize) { boardPosition ->
            Log.d("boardUI","deck clicked: ${boardPosition}")
            Log.d("boardUI","petClicked: ${petClicked}")

            Log.d("forceReturn", "boardClicked")
            Log.d("forceReturn", "viewModel.forceReturn.value: ${viewModel.forceReturn.value} ")
            if(selectedPetOrder != -1 && viewModel.allowPick.value == true){
                boardClicked = 1
                selectedGird = boardPosition
            }
//            else if (viewModel.forceReturn.value == true){
//                boardClicked = 1
//                selectedGird = boardPosition
//            }
        }
        boardRecyclerView.adapter = boardAdapter


        unitRecyclerView = findViewById(R.id.unitRecyclerView)
        unitRecyclerView.layoutManager = GridLayoutManager(this, 5) // 5 columns
        deckAdapter = deckAdapter(5) { position ->
            Log.d("deckUI","deck clicked: ${position}")
//            Log.d("deckUI","viewModel.allowPick.value == dict.ALLOW: ${viewModel.allowPick.value == dict.ALLOW}")
//            Log.d("deckUI","viewModel.deckStatus.value?.get(position)!!>= 0: ${viewModel.deckStatus.value?.get(position)!! >= 0}")
            if(viewModel.allowPick.value == true && viewModel.deckStatus.value?.get(position)!! >= dict.hasPet){
                newSelectedPetOrder = position
                petClicked = 1
                Log.d("deckUI","inside IF")
            }

        }

        unitRecyclerView.adapter = deckAdapter


        viewModel.deckStatus.observe(this, Observer{newDeckStatus->
            for (i in 0..deckSize-1){
                Log.d("deckStatus", "value: ${viewModel.deckStatus.value?.get(i)!! == dict.hasPet}")
                if(viewModel.deckStatus.value?.get(i)!! == dict.hasPet){
                    Log.d("deckStatus", "in if")
                    showPetOnDeck(i)
//                    returnPet(i,0,0)
                }
                else{
                    invisPetOnDeck(i)
                }

            }
        })

        viewModel.boardStatus.observe(this, Observer{newboardStatus->
            for (i in 0..boardSize-1){
                Log.d("boardStatus", "value: ${viewModel.boardStatus.value?.get(i)!! == dict.noPet}")
                if(viewModel.boardStatus.value?.get(i)!! >= 0){
                    Log.d("deckStatus", "in if")
                    showPetOnBoard(i)
//                    returnPet(i,0,0)
                }
                else if (viewModel.boardStatus.value?.get(i)!! == dict.noPet){
                    invisPetOnBoard(i)
                }

            }
        })


        viewModel.damageDealt.observe(this, Observer { newDamageDealt->
//            for(i in 0..2){
//                when(i){
//                    0 -> fireCountView.text = newDamageDealt[i].toString()
//                    1 -> waterCountView.text = newDamageDealt[i].toString()
//                    2 ->forestCountView.text = newDamageDealt[i].toString()
//                }
//            }\
            val stage = stageInfo.StageInfoMap[selectedStage]!!
            val maxHp = stage.damage
            val currentHp = viewModel.getCurrentBossHp()
            Log.d("hp", "max hp: ${maxHp}")
            Log.d("hp", "currentHp: ${currentHp}")

            if(stage.objectiveType  == dict.STAGE_OBJECTIVE_BEST){

                var newCurrentHp = maxHp

                for(i in 0..2){
                    newCurrentHp -= viewModel.getDamageDealt()[i]
                }
                Log.d("hp", "newCurrentHp: ${newCurrentHp}")
                if( newCurrentHp <0){
                    newCurrentHp = 0
                }
                val diff = currentHp - newCurrentHp
                val time = 500

                if(diff >0){
                    val layoutParams = hpBarView.layoutParams
                    Log.d("hp", "hpBarLength: $hpBarLength")

                    for (i in 0..diff){
                        val newHpBarLen = (((currentHp.toDouble()-i.toDouble())/maxHp.toDouble()) * hpBarLength.toDouble()).toInt()
                        Log.d("hp", "new hpBarLength: ${newHpBarLen}")
                        hpBarView.layoutParams.width = newHpBarLen
                        hpBarView.layoutParams = layoutParams
//                    delay(100)
                    }
                    viewModel.updateCurrentBossHp(newCurrentHp)

                }
                val display = newCurrentHp.toString()+"/"+maxHp.toString()
                hpFractionView.text = display

            }
            viewModel.turn.observe(this,Observer{newTurn->
                val display = "Turn "+newTurn.toString()
                turnView.text = display
            })

        })

        viewModel.init(stageInfo.StageInfoMap[selectedStage]!!.damage, dict.ALLOW, chosenPetId, initPetStatus(chosenPetId), initBoard, gameObjBuilder())

        gameStart()
    }

    private fun gameObjBuilder():String{
        val stage = stageInfo.StageInfoMap[selectedStage]!!
        val stageType = stage.objectiveType
        val maxTurn = stage.turn
        val targetDamage = stage.damage
        var obj = "Deal "
        if(stageType == dict.STAGE_OBJECTIVE_EXACT){
            obj +="exact "
        }
        obj += targetDamage.toString()+ " damages before "+maxTurn.toString()+"th turn!"
        return obj
    }
    private fun showPetOnDeck(petOrder: Int){
//        val pet= viewModel.petStatus.value?.get(petOrder)
//        val imageId = inventory.getViewInfoById(chosenPetId[petOrder])?.imageId
        val imageId = petInfo.getPetInfoById(chosenPetId[petOrder])?.imageId

//        Log.d("returnPet","returnPet triggered")
        unitRecyclerView.post {
            val targetDeck= unitRecyclerView.findViewHolderForAdapterPosition(petOrder) as? deckAdapter.ViewHolder
            targetDeck?.imageView?.isInvisible = false
            targetDeck?.imageView?.setImageResource(imageId!!)
        }
    }

    private fun invisPetOnDeck(petOrder:Int){
//        Log.d("returnPet","returnPet triggered")
        unitRecyclerView.post {
            val targetDeck= unitRecyclerView.findViewHolderForAdapterPosition(petOrder) as? deckAdapter.ViewHolder
            targetDeck?.imageView?.isInvisible = true
//            targetDeck?.imageView?.setImageResource(imageId!!)
        }
    }

    private fun showPetOnBoard(boardIndex: Int){
        val board = viewModel.getBoardStatus()
//        val pet= viewModel.petStatus.value?.get(board[boardIndex])

        val petStatus = viewModel.getPetStatus()
        val petOrder = board[boardIndex]
        val pet = viewModel.getPetStatus()[petOrder]
//        val imageId = inventory.getViewInfoById(pet?.unitId!!)?.imageId
        val imageId = petInfo.getPetInfoById(pet?.unitId!!)?.imageId
//        Log.d("returnPet","returnPet triggered")
        boardRecyclerView.post {
            val targetboard= boardRecyclerView.findViewHolderForAdapterPosition(boardIndex) as? boardAdapter.ViewHolder
            targetboard?.imageView?.isInvisible = false
            targetboard?.countView?.isInvisible = false
            targetboard?.elementFrame?.isInvisible = false
            targetboard?.countFrame?.isInvisible = false

            targetboard?.imageView?.setImageResource(imageId!!)

            val count = petInfo.getPetCount(petStatus,petOrder)
            if(count >=0){
                targetboard?.countView?.text = count.toString()
            }
            else{
                targetboard?.countView?.text = "∞"
            }

            val element = petInfo.getPetInfoById(pet?.unitId!!)?.element
            if(element == dict.ELEMENT_FIRE){
                targetboard?.countFrame?.setImageResource(R.drawable.game_count_frame_fire)
                targetboard?.elementFrame?.setImageResource(R.drawable.game_element_frame_fire)
            }
            else if(element == dict.ELEMENT_WATER){
                targetboard?.countFrame?.setImageResource(R.drawable.game_count_frame_water)
                targetboard?.elementFrame?.setImageResource(R.drawable.game_element_frame_water)
            }
            else if(element == dict.ELEMENT_FOREST){
                targetboard?.countFrame?.setImageResource(R.drawable.game_count_frame_forest)
                targetboard?.elementFrame?.setImageResource(R.drawable.game_element_frame_forest)
            }
        }
    }

    private fun invisPetOnBoard(boardIndex: Int){
        boardRecyclerView.post {
            val targetboard= boardRecyclerView.findViewHolderForAdapterPosition(boardIndex) as? boardAdapter.ViewHolder
            targetboard?.imageView?.isInvisible = true
            targetboard?.countView?.isInvisible = true
            targetboard?.elementFrame?.isInvisible = true
            targetboard?.countFrame?.isInvisible = true
        }
    }

    private fun gameStart() {
        coroutineScope.launch {
            var allBoard = 0
            var damageMatch = 0

            while (allBoard == 0 && damageMatch ==0) {
                viewModel.updateTurn()
                updateStayNum()
                handleDamageDealt(true)
                dismissIfDialogShowing()
                viewModel.updateAllowPick(true)
                setStartPos()
                handlePutPet()
                viewModel.updateAllowPick(false)

                handleBounceVictim(selectedGird)
//                waitForButtonClick(resumeBtn)
//                Log.d("gameStart", "cat damage: ${inventory.getViewInfoById(1)?.damage}")
                handleDamageDealt(false)
                dismissIfDialogShowing()

                allBoard = checkAllOnBoard()
                damageMatch = checkDamage()


//                forceReturn()

            }
            when(damageMatch){
                0 -> viewModel.updateGameMessage("Objective failed: no pet to fight!")
                1 -> viewModel.updateGameMessage("Objective done!")
                2 -> viewModel.updateGameMessage("Objective failed: Turn exceed!")
                3 -> viewModel.updateGameMessage("Objective failed: Dealt too much damage!")
            }

        }
    }

    private fun checkAllOnBoard():Int{
        val petStatus = viewModel.getPetStatus()
        for(i in 0..deckSize-1){
            if(petStatus[i]!!.location == dict.onDECK){
                return 0
            }
        }
        return -1
    }

    private fun checkDamage():Int{
        val stage = stageInfo.StageInfoMap[selectedStage]!!
        val objType = stageInfo.StageInfoMap[selectedStage]!!.objectiveType
        val currentTurn = viewModel.turn.value!!
        val requiredTurn = stage.turn
        val currentBossHp = viewModel.getCurrentBossHp()

        if(currentTurn <= requiredTurn){
            if(currentBossHp == 0){
                if(objType == dict.STAGE_OBJECTIVE_BEST){ //win
                    return 1
                }
                else{
                    var allDamage = 0
                    val damageHistory = viewModel.getDamageDealt()
                    for(i in 0..2){
                        allDamage += damageHistory[i]
                    }

                    if(allDamage == stage.damage){ //win
                        return 1
                    }
                    else if(allDamage > stage.damage){ //too much
                        return 3
                    }
                }
            }

        }
        else{   //turn exceed
            return 2
        }
        return 0
    }


    private suspend fun updateStayNum(){
        val petStatus = viewModel.getPetStatus()
        for (i in 0..deckSize-1){
            val pet = petStatus[i]!!
            if(pet.location == dict.onBoard){
                pet.stayNum += 1
            }
            else if(pet.location == dict.onDECK){
                pet.stayNum = 0
                pet.bounceNum = 0
            }
        }
        viewModel.updatePetStatus(petStatus)
    }

    private suspend fun waitForButtonClick(button: Button) {
        suspendCancellableCoroutine<Unit> { continuation ->
            button.setOnClickListener {
                // Resume the coroutine when the button is clicked
                continuation.resume(Unit)
//                button.setOnClickListener(null) // Clear the listener after use
            }
        }
    }


    private suspend fun handleDamageDealt(concernStay: Boolean){
        val damageHistory = viewModel.getDamageDealt()
        val petStatus = viewModel.getPetStatus()
        val chosenPet = viewModel.getChosenPet()
        val reportList = mutableListOf<String>()

        var damageHisChanged = false
        var petStatusChanged = false
        //hi

        for(i in 0..deckSize-1){
            val damage = petInfo.getPetDamage(petStatus,i)
            val damageType = petInfo.getPetInfoById(petStatus[i]!!.unitId)!!.type
            if(concernStay  && damageType != dict.ATK_TYPE_STAY ){
                continue
            }
            else if(!concernStay && damageType == dict.ATK_TYPE_STAY ){
                continue
            }

            if (damage > 0){
//                viewModel.updateShowDamageDialog(1)
                damageHisChanged= true
                val petID = petStatus[i]?.unitId!!
                val pet = petInfo.getPetInfoById(petID)!!
                val atkElement = pet.element
                val atkType = pet.type
                damageHistory[atkElement] +=damage

                if (atkType == dict.ATK_TYPE_BOUNCE && petStatus[i]?.location == dict.onBoard){
                    petStatus[i]?.bounceNum =0
                    petStatusChanged = true
                }

                val damageReport = pet.name+" dealt "+damage+" "+dict.ELEMENT_STRING[atkElement]+ " damage!!"
//                for(j in 0..damageReport.length){
//                    viewModel.updateDamageReport(damageReport.take(j))
//                    delay(100)
//                }
//                delay(500)
//                viewModel.updateDamageReport("")
                reportList.add(damageReport)


            }
        }

        if(damageHisChanged){
            viewModel.updateDamageDealt(damageHistory)
            showReportDialog(reportList)
            viewModel.refreshBoard()
        }

        if(petStatusChanged){
            viewModel.updatePetStatus(petStatus)
        }

    }

    fun dismissIfDialogShowing() {
        if (customDialog?.isShowing == true) {
            customDialog?.dismiss()
            customDialog = null // Clear the reference
        }
    }

    private suspend fun setStartPos(){
        val petStatus = viewModel.getPetStatus()
        for(order in 0..deckSize-1){
//            val pet = viewModel.petStatus.value?.get(order)
            val pet = petStatus[order]
            if (pet != null) {
                pet.startPos = pet.endPos
            }
        }
        viewModel.updatePetStatus(petStatus)
    }

    private suspend fun handlePutPet(){

        var putSuccess = 0
        selectedPetOrder = -1
        selectedGird = -1
        while (putSuccess == 0) {

            if (petClicked == 1 && newSelectedPetOrder != -1) {
                selectedPetOrder = toggleFrame(newSelectedPetOrder)
                newSelectedPetOrder = -1
                petClicked = 1
            }

            if (boardClicked == 1 && selectedPetOrder != -1 && viewModel.boardStatus.value?.get(selectedGird) == dict.noPet) {
                // Update board status
//                viewModel.updateBoardStatus(selectedGird, selectedPetOrder)
                val boardStatus = viewModel.getBoardStatus()
                boardStatus[selectedGird] = selectedPetOrder
                viewModel.updateBoardStatus(boardStatus)


                //update deck status
                val deckStatus = viewModel.getDeckStatus()
                deckStatus[selectedPetOrder] = dict.noPet
                viewModel.updateDeckStatus(deckStatus)

                val petStatus = viewModel.getPetStatus()
                val petGo = petStatus[selectedPetOrder]!!
                petGo.endPos = selectedGird
                petGo.stayNum = 0
                petGo.location = dict.onBoard
                viewModel.updatePetStatus(petStatus)

//                val testPet = viewModel.getPetStatus()[selectedPetOrder]!!
//                Log.d("handlePetPut", "testPet location: ${testPet.endPos}")

                // Exit the loop after updating
                putSuccess = 1
                selectedPetOrder = toggleFrame(selectedPetOrder)
                boardClicked = 0
            }

            delay(100) // Suspend the coroutine for 100 milliseconds
        }
        Log.d("Coroutine", "Coroutine end")
    }

    private suspend fun handleBounceVictim(petPut: Int){

        val victimPos = makeVictimPos(petPut)
        val victimPosBack = makeVictimBackPos(victimPos)
        val boardStatus = viewModel.getBoardStatus()
        val petStatus = viewModel.getPetStatus()
        Log.d("handleBounceVictim", "petPut: ${petPut}")
        for (t in 0..7) {
            Log.d("handleBounceVictim", "Loop: ${t}: victimPos: ${victimPos[t]}")
            if (victimPos[t] in 0..boardSize - 1) {
                val victimOrder = boardStatus[victimPos[t]]
//                Log.d("handleBounceVictim", "Loop: ${t}: victimOrder: ${victimOrder}")
                if (victimOrder >= 0) {   //it has a pet
                    val victimPet = petStatus[victimOrder]
//                        Log.d("handleBounceVictim", "victimID: ${victimPet?.unitId}")
                    //the support of the victim pet is on the board

                    Log.d("handleBounceVictim", "victimPos[t]: ${victimPos[t]}")
                    Log.d("handleBounceVictim", "victimPosBack[t]: ${victimPosBack[t]}")


                    for(k in 0..7){
                        Log.d("handleBounceVictim", "victimPos: ${victimPos[k]}")
                    }

                    for(k in 0..7){
                        Log.d("handleBounceVictim", "victimPosBack: ${victimPosBack[k]}")
                    }
                    if(victimPosBack[t]in 0..boardSize - 1){
                        val victimBackOrder = boardStatus[victimPosBack[t]]


                        if(victimBackOrder == dict.noPet){
//                            Log.d("handleBounceVictim", "victimBackOrder == dict.noPet: ${victimBackOrder == dict.noPet}")
                            //update pet status
                            victimPet!!.endPos = victimPosBack[t]
                            victimPet.bounceNum +=1
                            viewModel.updatePetStatus(petStatus)

                            //update board status
                            boardStatus[victimPos[t]] = dict.noPet
                            boardStatus[victimPosBack[t]] = victimOrder
                            viewModel.updateBoardStatus(boardStatus)
                        }
                    }
                    else{   //victim return to deck
                        victimPet!!.endPos = dict.outsideBoard
                        victimPet.bounceNum +=1
                        victimPet.location = dict.onDECK

                        //update deckStatus
                        val deckStatus = viewModel.getDeckStatus()
                        deckStatus[victimOrder] = dict.hasPet
                        viewModel.updateDeckStatus(deckStatus)

                        //update board status
                        boardStatus[victimPos[t]] = dict.noPet
                        viewModel.updateBoardStatus(boardStatus)
                    }
                }
            }
        }
    }

    private fun makeVictimPos(petPut: Int):IntArray{
//        val victimPos = IntArray(8){petPut}
//        val dir = intArrayOf(-6, -5, -4, -1, +1, +4, +5, +6)

        val ret = intArrayOf(petPut-6, petPut-5, petPut-4,
            petPut-1, petPut+1,
            petPut+4, petPut+5, petPut+6)

        //upmost row
        if(petPut / boardCol == 0){
            for(i in 0..2){
                ret[i] = dict.outsideBoard
            }
        }
        //lowest row
        if(petPut / boardCol == boardRow -1){
            for(i in 5..7){
                ret[i] = dict.outsideBoard
            }
        }

        //left most col
        if(petPut % boardCol == 0){
            ret[0] = dict.outsideBoard
            ret[3] = dict.outsideBoard
            ret[5] = dict.outsideBoard
        }

        //rightmost col
        if((petPut+1) % boardCol == 0){
            ret[2] = dict.outsideBoard
            ret[4] = dict.outsideBoard
            ret[7] = dict.outsideBoard
        }

        return ret

    }

    private fun makeVictimBackPos(victimPos: IntArray):IntArray{
        val dir = intArrayOf(-6, -5, -4, -1, +1, +4, +5, +6)
        val victimBackPos = victimPos.copyOf()
        for (i in 0..7){
            if(victimBackPos[i] == dict.outsideBoard){
                continue
            }
            when(i){
                0 -> if(victimBackPos[i] / boardCol == 0 || victimBackPos[i] % boardCol == 0) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]
                1 -> if(victimBackPos[i] / boardCol == 0 ) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[1]
                2 -> if(victimBackPos[i] / boardCol == 0 || (victimBackPos[i] + 1) % boardCol == 0) victimBackPos[i] = -4 else victimBackPos[i] += dir[i]

                3 -> if(victimBackPos[i] % boardCol == 0) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]
                4 -> if((victimBackPos[i] + 1) % boardCol == 0) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]

                5 -> if(victimBackPos[i] / boardCol == boardRow -1 || victimBackPos[i] % boardCol == 0) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]
                6 -> if(victimBackPos[i] / boardCol == boardRow -1) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]
                7 -> if(victimBackPos[i] / boardCol == boardRow -1 || (victimBackPos[i] + 1) % boardCol == 0) victimBackPos[i] = dict.outsideBoard else victimBackPos[i] += dir[i]
            }
        }

        return victimBackPos
    }

    private fun initPetStatus(chosenPetId: IntArray): Array<petStatus?>{
        val unitsOnBoard: Array<petStatus?> = arrayOfNulls<petStatus?>(deckSize)
        for (i in 0..deckSize-1) {
            unitsOnBoard[i] = petStatus().apply {
                unitId = chosenPetId[i]
                location = dict.onDECK
                startPos = dict.outsideBoard
                endPos = dict.outsideBoard
            }
        }
        return unitsOnBoard
    }

//    private fun updateImage(position: Int, imageId: Int) {
//        val viewHolder = boardRecyclerView.findViewHolderForAdapterPosition(position) as? boardAdapter.ViewHolder
//        viewHolder?.imageView?.isInvisible = false
//        viewHolder?.imageView?.setImageResource(imageId)
//    }
//
//    private fun invisibleImage(position: Int) {
//        val viewHolder = boardRecyclerView.findViewHolderForAdapterPosition(position) as? boardAdapter.ViewHolder
//        viewHolder?.imageView?.isInvisible = true
//    }

    private fun toggleFrame(position: Int): Int {
        Log.d("toggle","")
        val targetFrame= unitRecyclerView.findViewHolderForAdapterPosition(position) as? deckAdapter.ViewHolder
        val selectedFrame = unitRecyclerView.findViewHolderForAdapterPosition(selectedPetOrder) as? deckAdapter.ViewHolder

        if(selectedPetOrder == position){
            targetFrame?.frameView?.setBackgroundResource(R.drawable.game_stroke_background)
//            selectedPetOrder = -1
            return -1
        }

        else if(selectedPetOrder == -1){
            targetFrame?.frameView?.setBackgroundResource(R.drawable.game_stroke_background_click)
//            selectedPetOrder = position
            return position
        }
        else{
            selectedFrame?.frameView?.setBackgroundResource(R.drawable.game_stroke_background)
            targetFrame?.frameView?.setBackgroundResource(R.drawable.game_stroke_background_click)
//            selectedPetOrder = position
            return position
        }
    }

    private suspend fun showReportDialog(reportList: MutableList<String>) {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.game_report_dialog, null)

        // Create the dialog
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissing if needed
            .create()

        val window = customDialog.window
        if (window != null) {
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL // Position at bottom center
            layoutParams.y = 200 // Adjust vertical offset
            window.attributes = layoutParams
        }


        // Show the dialog
        customDialog.show()

        // Optional: Set the message dynamically
        val textView = dialogView.findViewById<TextView>(R.id.dialogReportText)
        viewModel.damageReport.observe(this, Observer { newDamageReport->
            textView.text = newDamageReport

        })

        for(report in reportList){
            for(i in 0..report.length){
                viewModel.updateDamageReport(report.take(i))
                delay(100)
            }
            delay(500)
            viewModel.updateDamageReport("")
        }
        customDialog.dismiss()
    }
}