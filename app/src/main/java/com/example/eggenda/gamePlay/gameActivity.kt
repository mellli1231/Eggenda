package com.example.eggenda.gamePlay

import android.graphics.Color
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
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class gameActivity : AppCompatActivity() {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    //load the info from hardcoded dataset
    private lateinit var stageInfo:stageInfo
//    private lateinit var petInfo: petInfo

    private lateinit var petInfo: petInfo2

    private val viewModel: gameViewModel2 by viewModels()

    //load info from other activities
    private  var selectedStage:Int = -1
    private lateinit var chosenPetId:IntArray
    private var boardRow = -1
    private var boardCol = -1
    private var boardSize = -1
    private var deckSize = -1


    private lateinit var initBoard: IntArray
    private lateinit var initDeck :IntArray

    //UI component
    private lateinit var turnView: TextView
    private lateinit var bossView: ImageView
    private lateinit var hpFractionView: TextView
    private lateinit var hpBarView: ImageView
    private var hpBarLength:Int = 0

    private lateinit var boardRecyclerView: RecyclerView
    private lateinit var boardAdapter: boardAdapter

    private lateinit var unitRecyclerView: RecyclerView
    private lateinit var deckAdapter: deckAdapter

    //variables for UI
    private var selectedPetOrder: Int = -1
    private var newSelectedPetOrder: Int = -1

    private var petClicked: Int = 0

    private var selectedGird: Int = -1
    private var boardClicked: Int = 0

    //buffer for game status
    private var turnBuffer: Int = 0
    private var currentBossHpBuffer: Int = 0
    private lateinit var petStatusBuffer:Array<petStatus?>
    private lateinit var boardStatusBuffer: IntArray
    private lateinit var deckStatusBuffer: IntArray
    private var allowPick:Boolean = false
    private lateinit var damageDealtBuffer: IntArray
    private var currentPlayerHpBuffer: Int =0


    //game visual purpose
    private var hitNum = 0
    private lateinit var bounceNumResetQueue: ArrayDeque<Int>
    private lateinit var stayNumPlusQueue: ArrayDeque<Int>

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
//    private val handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        //load the info from hardcoded dataset
        stageInfo = stageInfo()
        petInfo = petInfo2()
        sharedPreferenceManager = SharedPreferenceManager(this)


        selectedStage = 0
//        chosenPetId = intArrayOf(0,1,2,3,4)
        chosenPetId = sharedPreferenceManager.getPetsList()
        boardRow = 3
        boardCol = 5
        boardSize = boardRow * boardCol
        deckSize = 5

        //initialize the board and deck
        initBoard = IntArray(boardSize) { dict.noPet }
        initDeck = IntArray(deckSize) {dict.hasPet}

        //boss info
        turnView = findViewById(R.id.turnView)
        turnFractionVisualization(stageInfo.StageInfoMap(selectedStage)!!.maxTurn)

        bossView = findViewById(R.id.bossView)
        bossView.setImageResource(stageInfo.StageInfoMap(selectedStage)!!.bossImageId)

        hpFractionView = findViewById(R.id.hp_fraction)
        hpFractionVisualization(stageInfo.StageInfoMap(selectedStage)!!.damageRequirement)

        hpBarView = findViewById(R.id.hp_bar)
        hpBarView.post{
            hpBarLength = hpBarView.layoutParams.width
            Log.d("hp", "len${hpBarView.layoutParams.width}")
        }

        //board UI
        boardRecyclerView = findViewById(R.id.boardRecyclerView)
        boardRecyclerView.layoutManager = GridLayoutManager(this, boardCol) // 5 columns
        boardAdapter = boardAdapter(boardSize,
            onItemClick = {boardPosition ->
            Log.d("boardUI","short clicked clicked: ${boardPosition}")
            Log.d("boardUI","petClicked: ${petClicked}")

            Log.d("forceReturn", "boardClicked")
            Log.d("forceReturn", "viewModel.forceReturn.value: ${viewModel.forceReturn.value} ")

            if(selectedPetOrder != -1 && allowPick){
//            if(selectedPetOrder != -1 && viewModel.allowPick.value == true){
                boardClicked = 1
                selectedGird = boardPosition
            }

        },
            onItemLongClick = {boardPosition ->
                Log.d("boardUI","Long clicked: ${boardPosition}")
                Log.d("boardUI","petClicked: ${petClicked}")
                if(boardStatusBuffer[boardPosition]>=0 && allowPick){
                    showPetCard(chosenPetId[boardStatusBuffer[boardPosition]], boardStatusBuffer[boardPosition])
                }
            })


        boardRecyclerView.adapter = boardAdapter

        //deck UI
        unitRecyclerView = findViewById(R.id.unitRecyclerView)
        unitRecyclerView.layoutManager = GridLayoutManager(this, 5) // 5 columns
        deckAdapter = deckAdapter(5,
            onItemClick = {position ->
                Log.d("deckUI","deck clicked: ${position}")
                if(allowPick == true && deckStatusBuffer[position] >= dict.hasPet){
//            if(viewModel.allowPick.value == true && deckStatusBuffer[position] >= dict.hasPet){
                    newSelectedPetOrder = position
                    petClicked = 1
                    Log.d("deckUI","inside IF")
                }

            },
            onItemLongClick = {position ->
                if(allowPick && deckStatusBuffer[position] >= dict.hasPet){
                    showPetCard(chosenPetId[position], position)
                }
            })
        unitRecyclerView.adapter = deckAdapter

//        viewModel.init(stageInfo.StageInfoMap[selectedStage]!!.damage, dict.ALLOW, chosenPetId, initPetStatus(chosenPetId), initBoard, gameObjBuilder())

        //buffers initialization
        turnBuffer = 1
        currentBossHpBuffer = stageInfo.StageInfoMap(selectedStage)!!.damageRequirement
        currentPlayerHpBuffer = 100
        petStatusBuffer = initPetStatus(chosenPetId).copyOf()
        boardStatusBuffer = initBoard.copyOf()
        deckStatusBuffer = initDeck.copyOf()
        damageDealtBuffer = IntArray(3){0}

        //Plug init value to viewModel
        if(viewModel.getGameRunState() != dict.GAME_START){
            viewModel.updateTurn(turnBuffer)
            viewModel.updateCurrentBossHp(currentBossHpBuffer)
            viewModel.updateCurrentPlayerHp(currentPlayerHpBuffer)
            viewModel.updatePetStatus(petStatusBuffer)
            viewModel.updateBoardStatus(boardStatusBuffer)
            viewModel.updateDeckStatus(deckStatusBuffer)
            viewModel.updateDamageDealt(damageDealtBuffer)
        }

        if(stageInfo.StageInfoMap(selectedStage)!!.objectiveType !=dict.STAGE_OBJECTIVE_FIGHT){
            gameStart()
        }
        else{

        }


    }

    private fun gameStart() {

        coroutineScope.launch {
            var allPetOnBoard = 0
            var damageMatchRequired = 0

            bounceNumResetQueue = ArrayDeque()
            stayNumPlusQueue = ArrayDeque()

            deckVisualize()
            boardVisualize()
            viewModel.updateGameRunState(dict.GAME_START)
            while (allPetOnBoard == 0 && damageMatchRequired == 0) {
                turnBuffer = viewModel.getTurn()
                currentBossHpBuffer = viewModel.getCurrentBossHp()
                deckStatusBuffer = viewModel.getDeckStatus()
                boardStatusBuffer = viewModel.getBoardStatus()
                petStatusBuffer = viewModel.getPetStatus()
                damageDealtBuffer = viewModel.getDamageDealt()

                showTurnDialog(turnBuffer)
                turnFractionVisualization(turnBuffer)
                updateStayNum()

                deckVisualize()
                boardVisualize()

                allowPick = true

                handlePutPet(deckStatusBuffer, boardStatusBuffer, petStatusBuffer)
                allowPick = false
                handleBounceVictim(
                    selectedGird,
                    deckStatusBuffer,
                    boardStatusBuffer,
                    petStatusBuffer
                )

                deckVisualize()
                boardVisualize()

                val reportList = handleDamageDealt()

                if (reportList.size > 0) {
                    showReportDialog(reportList)
                }


                while (bounceNumResetQueue.size > 0) {
                    val petOrder = bounceNumResetQueue.removeFirst()
                    petStatusBuffer[petOrder]!!.bounceNum = 0
                }

                bossImageFlash(hitNum)
                hpBarVisualization(currentBossHpBuffer)

                deckVisualize()
                boardVisualize()

                allPetOnBoard = checkAllOnBoard()
                damageMatchRequired = checkDamage()
                Log.d("end info", "current turn ${turnBuffer}")
                Log.d("end info", "allPetOnBoard ${allPetOnBoard}")
                Log.d("end info", "damageMatchRequired ${damageMatchRequired}")
                turnBuffer += 1

                viewModel.updateTurn(turnBuffer)
                viewModel.updateCurrentBossHp(currentBossHpBuffer)
                viewModel.updatePetStatus(petStatusBuffer)
                viewModel.updateBoardStatus(boardStatusBuffer)
                viewModel.updateDeckStatus(deckStatusBuffer)
                viewModel.updateDamageDealt(damageDealtBuffer)
            }
            Log.d("end info", "outside While")
            viewModel.updateGameRunState(damageMatchRequired)
            showResultDialog(damageMatchRequired)
        }
    }

    private fun gameFightStart() {
        coroutineScope.launch {
            var allPetOnBoard = 0
            var damageMatchRequired = 0

            bounceNumResetQueue = ArrayDeque()
            stayNumPlusQueue = ArrayDeque()

            deckVisualize()
            boardVisualize()
            viewModel.updateGameRunState(dict.GAME_START)
            while (allPetOnBoard == 0 && damageMatchRequired == 0) {
                turnBuffer = viewModel.getTurn()
                currentBossHpBuffer = viewModel.getCurrentBossHp()
                currentPlayerHpBuffer = viewModel.getCurrentPlayerHp()
                deckStatusBuffer = viewModel.getDeckStatus()
                boardStatusBuffer = viewModel.getBoardStatus()
                petStatusBuffer = viewModel.getPetStatus()
                damageDealtBuffer = viewModel.getDamageDealt()

                showTurnDialog(turnBuffer)
                turnFractionVisualization(turnBuffer)
                updateStayNum()

                deckVisualize()
                boardVisualize()

                allowPick = true

                handlePutPet(deckStatusBuffer, boardStatusBuffer, petStatusBuffer)
                allowPick = false
                handleBounceVictim(
                    selectedGird,
                    deckStatusBuffer,
                    boardStatusBuffer,
                    petStatusBuffer
                )

                deckVisualize()
                boardVisualize()

                val reportList = handleDamageDealt()

                if (reportList.size > 0) {
                    showReportDialog(reportList)
                }


                while (bounceNumResetQueue.size > 0) {
                    val petOrder = bounceNumResetQueue.removeFirst()
                    petStatusBuffer[petOrder]!!.bounceNum = 0
                }

                bossImageFlash(hitNum)
                hpBarVisualization(currentBossHpBuffer)

                deckVisualize()
                boardVisualize()

                allPetOnBoard = checkAllOnBoard()
                damageMatchRequired = checkDamage()

                //boss action

                Log.d("end info", "current turn ${turnBuffer}")
                Log.d("end info", "allPetOnBoard ${allPetOnBoard}")
                Log.d("end info", "damageMatchRequired ${damageMatchRequired}")
                turnBuffer += 1

                viewModel.updateTurn(turnBuffer)
                viewModel.updateCurrentBossHp(currentBossHpBuffer)
                viewModel.updateCurrentPlayerHp(currentPlayerHpBuffer)
                viewModel.updatePetStatus(petStatusBuffer)
                viewModel.updateBoardStatus(boardStatusBuffer)
                viewModel.updateDeckStatus(deckStatusBuffer)
                viewModel.updateDamageDealt(damageDealtBuffer)
            }
            Log.d("end info", "outside While")
            viewModel.updateGameRunState(damageMatchRequired)
            showResultDialog(damageMatchRequired)
        }
    }

    //Clean
    private suspend fun showTurnDialog(turn:Int ){
        val dialogView = layoutInflater.inflate(R.layout.game_turn_dialog, null)

        // Create the dialog
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissing if needed
            .create()


        customDialog.show()
        val textView = dialogView.findViewById<TextView>(R.id.turnStart)

        val display = "Turn "+turn.toString()+" start!!"
        textView.text = display
        delay(2000)
        customDialog.dismiss()

    }

    //Dirty: modify petStatusBuffer
    private suspend fun updateStayNum(){
//        val petStatus = viewModel.getPetStatus()
        val petStatus = petStatusBuffer
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
    }

    //Dirty: modify deckStatusBuffer, boardStatusBuffer, petStatusBuffer
    private suspend fun handlePutPet(deckStatusBuffer:IntArray, boardStatusBuffer:IntArray, petStatusBuffer: Array<petStatus?>){
        var putSuccess = 0
        selectedPetOrder = -1
        selectedGird = -1
        while (putSuccess == 0) {
            if (petClicked == 1 && newSelectedPetOrder != -1) {
                selectedPetOrder = toggleFrame(newSelectedPetOrder)
                newSelectedPetOrder = -1
                petClicked = 1
            }
            if (boardClicked == 1 && selectedPetOrder != -1 && boardStatusBuffer[selectedGird] == dict.noPet) {
//            if (boardClicked == 1 && selectedPetOrder != -1 && viewModel.boardStatus.value?.get(selectedGird) == dict.noPet) {
                //update board status
                val boardStatus = boardStatusBuffer
                boardStatus[selectedGird] = selectedPetOrder

                Log.d("check", "selectedGird: ${selectedGird}")
                //update deck status
                val deckStatus = deckStatusBuffer
                deckStatus[selectedPetOrder] = dict.noPet

                val petStatus = petStatusBuffer
                val petGo = petStatus[selectedPetOrder]!!
                petGo.endPos = selectedGird
                petGo.stayNum = 0
                petGo.location = dict.onBoard

                // Exit the loop after updating
                putSuccess = 1
                selectedPetOrder = toggleFrame(selectedPetOrder)
                boardClicked = 0
            }

            delay(100) // Suspend the coroutine for 100 milliseconds
        }
        Log.d("Coroutine", "Coroutine end")
    }

    //dirty: modify global buffer
    private suspend fun handleBounceVictim(petPut: Int,deckStatusBuffer:IntArray, boardStatusBuffer:IntArray, petStatusBuffer: Array<petStatus?>){
        val victimPos = makeVictimPos(petPut)
        val victimPosBack = makeVictimBackPos(victimPos)
        val boardStatus = boardStatusBuffer
        val petStatus = petStatusBuffer
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
//                            viewModel.updatePetStatus(petStatus)

                            //update board status
                            boardStatus[victimPos[t]] = dict.noPet
                            boardStatus[victimPosBack[t]] = victimOrder
//                            viewModel.updateBoardStatus(boardStatus)
                        }
                    }
                    else{   //victim return to deck
                        victimPet!!.endPos = dict.outsideBoard
                        victimPet.bounceNum +=1
                        victimPet.location = dict.onDECK

                        //update deckStatus
//                        val deckStatus = viewModel.getDeckStatus()
                        val deckStatus = deckStatusBuffer
                        deckStatus[victimOrder] = dict.hasPet
//                        viewModel.updateDeckStatus(deckStatus)

                        //update board status
                        boardStatus[victimPos[t]] = dict.noPet
//                        viewModel.updateBoardStatus(boardStatus)
                    }
                }
            }
        }
    }

    //Clean: helper function of handleBounceVictim
    private fun makeVictimPos(petPut: Int):IntArray{
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

    //Clean: helper function of handleBounceVictim
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

    //Clean
    private suspend fun handleDamageDealt():MutableList<String>{
        val damageHistory = damageDealtBuffer
        val petStatus = petStatusBuffer
        val reportList = mutableListOf<String>()
        val acceptElement = stageInfo.StageInfoMap(selectedStage)!!.element
        hitNum = 0
        for(i in 0..deckSize-1){
            val damage = petInfo.getPetInfoById(chosenPetId[i])!!.dealDamage(petStatus,i)
            val petElement = petInfo.getPetInfoById(chosenPetId[i])!!.element
            Log.d("damage dealt", "pet element : ${petElement}")
            Log.d("damage dealt", "accept element: ${acceptElement}")
            if (damage > 0 &&  (acceptElement == dict.STAGE_ACCEPT_ALL_ELEMENT || acceptElement == petElement)){
                val petID = petStatus[i]?.unitId!!
                val pet = petInfo.getPetInfoById(petID)!!
                val atkElement = pet.element
                val atkType = pet.attackType
                damageHistory[atkElement] += damage

                if (atkType == dict.ATK_TYPE_BOUNCE && petStatus[i]?.location == dict.onBoard){
                    bounceNumResetQueue.add(i)
                }
                else if (atkType == dict.ATK_TYPE_STAY && petStatus[i]?.location == dict.onBoard){
                    stayNumPlusQueue.add(i)
                }

                val damageReport = pet.name+" dealt "+damage+" "+dict.ELEMENT_STRING[atkElement]+ " damage!!"
                reportList.add(damageReport)
                hitNum ++

            }
        }

        return reportList

    }

    //clean
    private suspend fun showReportDialog(reportList: MutableList<String>): Boolean {
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
//        dialogIsShowing = false
        customDialog.dismiss()
        return true
    }

    //clean
    private suspend fun bossImageFlash(hitNum: Int){
        for(i in 0..< hitNum){
            bossView.isInvisible = true
            delay(50)
            bossView.isInvisible = false
            delay(50)
        }
    }

    //dirty: affecting global buffer: currentBossHpBuffer
    private suspend fun hpBarVisualization(currentBossHpBuffer: Int){
        val stage = stageInfo.StageInfoMap(selectedStage)!!
        val maxHp = stage.damageRequirement
        val currentHp = currentBossHpBuffer
        Log.d("hp", "max hp: ${maxHp}")
        Log.d("hp", "currentHp: ${currentHp}")

        var newCurrentHp = maxHp

        for (i in 0..2) {
            newCurrentHp -= damageDealtBuffer[i]
        }
        Log.d("hp", "newCurrentHp: ${newCurrentHp}")
        if( newCurrentHp <0){
            newCurrentHp = 0
        }
        val diff = currentHp - newCurrentHp
        val totalTime = 300
        val interval = (totalTime.toDouble()/diff.toDouble()).toInt().toLong()

        if(diff >0){
            val layoutParams = hpBarView.layoutParams
            Log.d("hp", "hpBarLength: $hpBarLength")

            for (i in 0..diff){
                val newHpBarLen = (((currentHp.toDouble()-i.toDouble())/maxHp.toDouble()) * hpBarLength.toDouble()).toInt()
                Log.d("hp", "new hpBarLength: ${newHpBarLen}")
                hpBarView.layoutParams.width = newHpBarLen
                hpBarView.layoutParams = layoutParams
                delay(interval)
            }
            this@gameActivity.currentBossHpBuffer = newCurrentHp
//                viewModel.updateCurrentBossHp(newCurrentHp)

        }
//            val display = newCurrentHp.toString() + "/" + maxHp.toString()
//            hpFractionView.text = display
        hpFractionVisualization(newCurrentHp)
    }

    private fun turnFractionVisualization(currentTurn: Int){
        val display = "Turn "+currentTurn.toString() + "/" + stageInfo.StageInfoMap(selectedStage)!!.maxTurn
        turnView.text =display
    }

    private fun hpFractionVisualization(currentHp: Int){
        val display = currentHp.toString() + "/" + stageInfo.StageInfoMap(selectedStage)!!.damageRequirement.toString()
        hpFractionView.text = display
    }

    private fun checkAllOnBoard():Int{
//        val petStatus = viewModel.getPetStatus()
        val petStatus = petStatusBuffer
        for(i in 0..< deckSize){
            if(petStatus[i]!!.location == dict.onDECK){
                return 0
            }
        }
        return -1
    }

    private fun checkDamage():Int{
        val stage = stageInfo.StageInfoMap(selectedStage)!!
        val objType = stageInfo.StageInfoMap(selectedStage)!!.objectiveType
        val currentTurn = turnBuffer
        val requiredTurn = stage.maxTurn
        val currentBossHp = currentBossHpBuffer

        val below = -1
        val just = 0
        val over = 1

        var damageMatch = below    //-1 below, 0 just, 1 over
        var turnExceed = if (currentTurn >= requiredTurn) true else false

        if(objType == dict.STAGE_OBJECTIVE_BEST && currentBossHp == 0){
            damageMatch = 0
        }
        else if(objType == dict.STAGE_OBJECTIVE_EXACT && currentBossHp <= 0 ){
            var allDamage = 0
                    val damageHistory = damageDealtBuffer
                    for(i in 0..2){
                        allDamage += damageHistory[i]
                    }
                    if(allDamage == stage.damageRequirement){ //win
                        damageMatch = just
                    }
                    else if(allDamage > stage.damageRequirement){ //too much
                        damageMatch = over
                    }
        }

        if(turnExceed){
            when(damageMatch){
                below -> return dict.GAME_TURN_EXCEED
                just -> return dict.GAME_WON
                over -> return dict.GAME_DAMAGE_EXCEED

            }
        }
        else if(!turnExceed){
            when(damageMatch){
                below -> return 0
                just -> return dict.GAME_WON
                over -> return dict.GAME_DAMAGE_EXCEED

            }
        }
        return 0
    }

    private fun showResultDialog(gameResult: Int){
        val dialogView = layoutInflater.inflate(R.layout.game_reusult_dialog, null)

        // Create the dialog
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissing if needed
            .create()

//        val window = customDialog.window
//        if (window != null) {
//            val layoutParams = window.attributes
//            layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL // Position at bottom center
//            layoutParams.y = 200 // Adjust vertical offset
//            window.attributes = layoutParams
//        }


        // Show the dialog
        customDialog.show()
        val textView = dialogView.findViewById<TextView>(R.id.gameResult)
        when(gameResult){
                dict.GAME_NO_PET -> textView.text = "Objective failed:\nNo pet to fight!"
                dict.GAME_WON -> textView.text = "Objective done:\nYou Win!!"
                dict.GAME_TURN_EXCEED -> textView.text = "Objective failed:\nTurn exceed!"
                dict.GAME_DAMAGE_EXCEED -> textView.text = "Objective failed:\nDealt too much damage!"
        }

        val restartBtn = dialogView.findViewById<Button>(R.id.game_restart)
        restartBtn.setOnClickListener{
            viewModel.updateGameRunState(dict.GAME_NOT_START)
            customDialog.dismiss()
            recreate()
        }

        val goHomeBtn = dialogView.findViewById<Button>(R.id.game_toBossChoose)
        goHomeBtn.setOnClickListener {
            viewModel.updateGameRunState(dict.GAME_NOT_START)
            customDialog.dismiss()
            finish()
        }

//        dialogIsShowing = false
//        customDialog.dismiss()
//        return true
    }

    //Small helper functions
    private fun showPetCard(petId:Int, petOrder: Int){
        val dialogView = layoutInflater.inflate(R.layout.game_pet_detail_dialog, null)

        // Create the dialog
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true) // Prevent dismissing if needed
            .create()


        val pet = petInfo.getPetInfoById(petId)!!
        customDialog.show()
        val petImage = dialogView.findViewById<ImageView>(R.id.detail_petImage)
        petImage.setImageResource(pet.imageId)

        val petName = dialogView.findViewById<TextView>(R.id.detail_petName)
        petName.text = pet.name

        val petIdView = dialogView.findViewById<TextView>(R.id.detail_petId)
        petIdView.text = String.format("%03d", pet.id)
        when(pet.element){
            dict.ELEMENT_FIRE ->  petIdView.setTextColor(Color.RED)
            dict.ELEMENT_WATER -> petIdView.setTextColor(Color.BLUE)
            dict.ELEMENT_FOREST -> petIdView.setTextColor(Color.parseColor("#4CAF50"))
        }

        val skillName = dialogView.findViewById<TextView>(R.id.detail_petSkill)
        skillName.text = pet.skillName


        val elementDot = dialogView.findViewById<ImageView>(R.id.detail_element)
        when(pet.element){
            dict.ELEMENT_FIRE -> elementDot.setImageResource(R.drawable.game_element_frame_fire)
            dict.ELEMENT_WATER -> elementDot.setImageResource(R.drawable.game_element_frame_water)
            dict.ELEMENT_FOREST -> elementDot.setImageResource(R.drawable.game_element_frame_forest)
        }

        val description = dialogView.findViewById<TextView>(R.id.detail_skillCondition)
        description.text = pet.description


        val condition = dialogView.findViewById<TextView>(R.id.detail_nextReminder)
        condition.text = pet.condition(petStatusBuffer, petOrder)

        val nextDmg = dialogView.findViewById<TextView>(R.id.detail_nextDmgAmount)
        nextDmg.text = pet.nextDamage(petStatusBuffer, petOrder)

//        val textView = dialogView.findViewById<TextView>(R.id.card_pet_name)

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
        return unitsOnBoard.copyOf()
    }

    private fun gameObjBuilder():String{
        val stage = stageInfo.StageInfoMap(selectedStage)!!
        val stageType = stage.objectiveType
        val maxTurn = stage.maxTurn
        val targetDamage = stage.damageRequirement
        var obj = "Deal "
        if(stageType == dict.STAGE_OBJECTIVE_EXACT){
            obj +="exact "
        }
        obj += targetDamage.toString()+ " damages before "+maxTurn.toString()+"th turn!"
        return obj
    }

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

    private suspend fun deckVisualize(){
        for (i in 0..deckSize-1){
//            Log.d("deckStatus", "value: ${viewModel.deckStatus.value?.get(i)!! == dict.hasPet}")
            if(deckStatusBuffer[i]== dict.hasPet){
                Log.d("deckStatus", "in if")
                showPetOnDeck(i)
            }
            else{
                invisPetOnDeck(i)
            }

        }
    }

    private suspend fun boardVisualize(){
        for (i in 0..boardSize-1){
//            Log.d("boardStatus", "value: ${viewModel.boardStatus.value?.get(i)!! == dict.noPet}")
            if(boardStatusBuffer[i] >= 0){
                showPetOnBoard(i)
//                    returnPet(i,0,0)
            }
            else if (boardStatusBuffer[i] == dict.noPet){
                invisPetOnBoard(i)
            }
        }
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
//        val board = viewModel.getBoardStatus()
        val board = boardStatusBuffer
//        val pet= viewModel.petStatus.value?.get(board[boardIndex])

//        val petStatus = viewModel.getPetStatus()
        val petStatus = petStatusBuffer
        val petOrder = board[boardIndex]
//        val pet = viewModel.getPetStatus()[petOrder]
        val pet = petStatus[petOrder]
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

//            val count = petInfo.getPetCount(petStatus,petOrder)

            val count = petInfo.getPetInfoById(chosenPetId[petOrder])!!.attackCountdown(petStatus,petOrder)
            if(count >=0){
                targetboard?.countView?.text = count.toString()
            }
            else{
                targetboard?.countView?.text = "∞"
            }

            val element = petInfo.getPetInfoById(pet?.unitId!!)?.element
            if(element == dict.ELEMENT_FIRE){
//                targetboard?.nextDmgFrame
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
}