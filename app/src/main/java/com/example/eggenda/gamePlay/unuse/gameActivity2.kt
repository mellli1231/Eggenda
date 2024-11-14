//package com.example.eggenda.gamePlay
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.eggenda.R
//
//class gameActivity2 : AppCompatActivity() {
//
//    //load the info from hardcoded dataset
//    private lateinit var stageInfo:stageInfo
//    private lateinit var petInfo: petInfo
//
//    private val viewModel: gameViewModel by viewModels()
//
//    //load info from other activities
//    private  var selectedStage:Int = 0
//    private lateinit var chosenPetId:IntArray
//    private var boardRow = -1
//    private var boardCol = -1
//    private var boardSize = -1
//    private var deckSize = 5
//
//
//    private lateinit var initBoard: IntArray
//
//    //UI component
//    private lateinit var turnView: TextView
//    private lateinit var bossView: ImageView
//    private lateinit var hpFractionView: TextView
//    private lateinit var hpBarView: ImageView
//    private var hpBarLength:Int = 0
//
//    private lateinit var boardRecyclerView: RecyclerView
//    private lateinit var boardAdapter: boardAdapter
//
//    private lateinit var unitRecyclerView: RecyclerView
//    private lateinit var deckAdapter: deckAdapter
//
//    //variables for UI
//    private var selectedPetOrder: Int = -1
//    private var newSelectedPetOrder: Int = -1
//
//    private var petClicked: Int = 0
//
//    private var selectedGird: Int = -1
//    private var boardClicked: Int = 0
//
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        enableEdgeToEdge()
//        setContentView(R.layout.activity_game)
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
////            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
////            insets
////        }
//
//        //load the info from hardcoded dataset
//        stageInfo = stageInfo()
//        petInfo = petInfo()
//
//
//        selectedStage = 0
//        chosenPetId = intArrayOf(5,1,2,3,4)
//        boardRow = 3
//        boardCol = 5
//        boardSize = boardRow * boardCol
//
//        initBoard = IntArray(boardSize) { dict.noPet }
//
//        //boss info
//        turnView = findViewById(R.id.turnView)
//        bossView = findViewById(R.id.bossView)
//        hpFractionView = findViewById(R.id.hp_fraction)
//        hpBarView = findViewById(R.id.hp_bar)
//        hpBarView.post{
//            hpBarLength = hpBarView.layoutParams.width
//            Log.d("hp", "len${hpBarView.layoutParams.width}")
//        }
//
//        //board UI
//        boardRecyclerView = findViewById(R.id.boardRecyclerView)
//        boardRecyclerView.layoutManager = GridLayoutManager(this, boardCol) // 5 columns
////        boardAdapter = boardAdapter(boardSize) { boardPosition ->
////            Log.d("boardUI","deck clicked: ${boardPosition}")
////            Log.d("boardUI","petClicked: ${petClicked}")
////
////            Log.d("forceReturn", "boardClicked")
////            Log.d("forceReturn", "viewModel.forceReturn.value: ${viewModel.forceReturn.value} ")
////            if(selectedPetOrder != -1 && viewModel.allowPick.value == true){
////                boardClicked = 1
////                selectedGird = boardPosition
////            }
////        }
////        boardRecyclerView.adapter = boardAdapter
//
//        //deck UI
//        unitRecyclerView = findViewById(R.id.unitRecyclerView)
//        unitRecyclerView.layoutManager = GridLayoutManager(this, 5) // 5 columns
//        deckAdapter = deckAdapter(5) { position ->
//            Log.d("deckUI","deck clicked: ${position}")
//            if(viewModel.allowPick.value == true && viewModel.deckStatus.value?.get(position)!! >= dict.hasPet){
//                newSelectedPetOrder = position
//                petClicked = 1
//                Log.d("deckUI","inside IF")
//            }
//
//        }
//        unitRecyclerView.adapter = deckAdapter
//
//        viewModel.init(stageInfo.StageInfoMap[selectedStage]!!.damage, dict.ALLOW, chosenPetId, initPetStatus(chosenPetId), initBoard, gameObjBuilder())
//
//    }
//
//    private fun initPetStatus(chosenPetId: IntArray): Array<petStatus?>{
//        val unitsOnBoard: Array<petStatus?> = arrayOfNulls<petStatus?>(deckSize)
//        for (i in 0..deckSize-1) {
//            unitsOnBoard[i] = petStatus().apply {
//                unitId = chosenPetId[i]
//                location = dict.onDECK
//                startPos = dict.outsideBoard
//                endPos = dict.outsideBoard
//            }
//        }
//        return unitsOnBoard.copyOf()
//    }
//
//    private fun gameObjBuilder():String{
//        val stage = stageInfo.StageInfoMap[selectedStage]!!
//        val stageType = stage.objectiveType
//        val maxTurn = stage.turn
//        val targetDamage = stage.damage
//        var obj = "Deal "
//        if(stageType == dict.STAGE_OBJECTIVE_EXACT){
//            obj +="exact "
//        }
//        obj += targetDamage.toString()+ " damages before "+maxTurn.toString()+"th turn!"
//        return obj
//    }
//}