package com.example.eggenda.gameMonsterChoose

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.GamePetChooseMainActivity
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.stageInfo

class GameMonsterChooseMainActivity : AppCompatActivity () {

    private lateinit var button: Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var stageInfo: stageInfo


    private lateinit var viewModel: monsterChooseViewModel
    private var stageId: Int = 0

    private lateinit var stageTitle:TextView
    private lateinit var bossTitle:TextView
    private lateinit var backBtn:ImageView
    private lateinit var nextBtn:ImageView
    private lateinit var bossImage:ImageView
    private lateinit var bossDefeatCover:ImageView  //add cover to boss image if the boss is defeated
    private lateinit var stageDoneArrayList: ArrayList<Int>
    private var amount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.game_monster_choose)
        sharedPreferenceManager = SharedPreferenceManager(this)
        stageInfo = stageInfo()
        viewModel = monsterChooseViewModel()

        stageTitle = findViewById(R.id.stage_Title)
        bossTitle = findViewById(R.id.boss_title)
        backBtn = findViewById(R.id.back_button)
        nextBtn = findViewById(R.id.next_button)
        bossImage = findViewById(R.id.bossView)
        bossDefeatCover = findViewById(R.id.bossDefeatCover)


        //handle stageDone
        stageDoneArrayList = sharedPreferenceManager.getStageDone()
        if(stageDoneArrayList.size != stageInfo.stageTotalNum){
            stageDoneArrayList = getNewStageDone(stageDoneArrayList, stageInfo.stageTotalNum)
            sharedPreferenceManager.saveStageDone(stageDoneArrayList)
        }


        viewModel.chosenStageID.observe(this, Observer { newStageId->
            if(newStageId == 0 ){
                backBtn.isInvisible = true
//                backBtn.isActivated = false
            }
            else{
                backBtn.isInvisible = false
            }
            if(newStageId  == stageInfo.stageTotalNum - 1){
                nextBtn.isInvisible = true
            }
            else{
                nextBtn.isInvisible = false
            }
            val selectedStage = stageInfo.StageInfoMap(newStageId)!!
            val stageStr = "Stage "+(newStageId + 1).toString()+":"
            val bossStr = selectedStage.name
            if(stageDoneArrayList[newStageId] == 1){
                bossDefeatCover.isInvisible = false
            }
            else{
                bossDefeatCover.isInvisible = true
            }
            stageTitle.text = stageStr
            bossTitle.text = bossStr
            bossImage.setImageResource(selectedStage.bossImageId)
            viewModel.updateAmount(selectedStage.deckSize)
//            viewModel.updateChosenStageID(0)
        })

        backBtn.setOnClickListener{
            viewModel.subtractChosenStageID()
        }
        nextBtn.setOnClickListener{
            viewModel.addChosenStageID()
        }
//        val amount = 5
//        sharedPreferenceManager.savePetsAmount(amount)
//
//        val retrievedList = sharedPreferenceManager.getPetOwnership()
//        val intArrayhihi : IntArray = retrievedList.toIntArray()
//        Log.d("Retrieved List", "IntArray: ${intArrayhihi.joinToString(",")}")

        button = findViewById(R.id.game_monster_go)

        button.setOnClickListener {

            //for pet amount
            sharedPreferenceManager.savePetsAmount(viewModel.getAmount())
            val retrievedList = sharedPreferenceManager.getPetOwnership()
            val intArrayhihi : IntArray = retrievedList.toIntArray()
            Log.d("Retrieved List", "IntArray: ${intArrayhihi.joinToString(",")}")

            //for stage id
            Log.d("stageID", "chosen: ${viewModel.getChosenStageID()}")
            sharedPreferenceManager.saveStageChoose(viewModel.getChosenStageID())

            Log.d("stageID", "saved: ${sharedPreferenceManager.getStageChoose()}")
            val intent = Intent(this, GamePetChooseMainActivity::class.java)
            finish()
            startActivity(intent)
        }

    }

    private fun getNewStageDone(oldStageDone: ArrayList<Int>, stageCount: Int):ArrayList<Int>{
        val ret = ArrayList<Int>()
        for(i in 1..stageCount){
            ret.add(0)
        }
        var index = 0
        while(index < oldStageDone.size && index < stageCount){
            ret[index] = oldStageDone[index]
            index ++
        }
        return ret
    }




}