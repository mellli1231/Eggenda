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
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.GamePetChooseMainActivity
import com.example.eggenda.gamePetChoose.PetChooseDialogFragment
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.gameActivity
import com.example.eggenda.gamePlay.stageInfo
import org.w3c.dom.Text

class GameMonsterChooseMainActivity : AppCompatActivity () {

    private lateinit var button: Button
    private lateinit var infoButton : Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var stageInfo: stageInfo


    private lateinit var viewModel: monsterChooseViewModel
    private var stageId: Int = 0

    private lateinit var stageTitle:TextView
    private lateinit var bossTitle:TextView
    private lateinit var backBtn:ImageView
    private lateinit var nextBtn:ImageView
    private lateinit var bossImage:ImageView
    private lateinit var bossChallengeCover :ImageView  //add cover to boss image if the boss is not challengable at ths moment
    private lateinit var stageDoneArrayList: ArrayList<Int>
    private lateinit var monsterStatusIcon : ImageView
    private lateinit var monsterStatusText : TextView
    private lateinit var monsterWarningText : TextView
    private lateinit var ownedPetsList : ArrayList<Int> // to check if the pets are enough for play or not

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
        bossChallengeCover = findViewById(R.id.bossDefeatCover)
        monsterStatusIcon = findViewById(R.id.monster_status_icon)
        monsterStatusText = findViewById(R.id.monster_status_text)
        monsterWarningText = findViewById(R.id.monster_pets_warning)

        //handle stageDone
        stageDoneArrayList = sharedPreferenceManager.getStageDone()
        if(stageDoneArrayList.size != stageInfo.stageTotalNum){
            stageDoneArrayList = getNewStageDone(stageDoneArrayList, stageInfo.stageTotalNum)
            sharedPreferenceManager.saveStageDone(stageDoneArrayList)
        }

        //handle the owned pets size whenever the pets is updated or not
        ownedPetsList = sharedPreferenceManager.getPetOwnership()
         val ownedPets : IntArray = ownedPetsList.toIntArray()

        var filteredPetsList = ownedPetsList.filterIndexed { index, _->
            index < ownedPets.size && ownedPets[index] == 1
        } .also { Log.d("FilteredPets", "filteredPetsList size: ${it.size}") }


        //set the button

        button = findViewById(R.id.game_monster_go)

        viewModel.chosenStageID.observe(this, Observer { newStageId->
            if(newStageId == 0 ){
                backBtn.isInvisible = true
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

            //set the stage status bar
            if(stageDoneArrayList[newStageId] == 1){
                monsterStatusIcon.isVisible = true
                monsterStatusIcon.setImageResource(R.drawable.game_monster_tick)
                monsterStatusText.text = "Finished!"
//                bossChallengeCover.isInvisible = false

            }
            else{
                monsterStatusIcon.isVisible = false
                monsterStatusText.text = "You're ready to play!"
//                bossChallengeCover.isInvisible = true
            }

            //set the mask if the user does not get enough pets for that stage
            if(stageInfo.StageInfoMap(newStageId)!!.deckSize > filteredPetsList.size){
                bossChallengeCover.isVisible = true
                monsterWarningText.isVisible = true
                monsterWarningText.text = "Warning : \n The number of pets owned is insufficient to " +
                        "challenge the boss! \n Pets owned now: ${filteredPetsList.size} \n" + "" +
                        "Stage Required : ${stageInfo.StageInfoMap(newStageId)!!.deckSize}"
                monsterStatusIcon.isVisible = true
                monsterStatusIcon.setImageResource(R.drawable.game_monster_cross)
                monsterStatusText.text = "Pets not enough!"

                // change it into non-selectable status
                button.isVisible = false
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled_color))
                button.invalidate()


            } else {
                bossChallengeCover.isVisible = false
                monsterWarningText.isVisible = false
                buttonClickableStatus()

            }

            stageTitle.text = stageStr
            bossTitle.text = bossStr
            bossImage.setImageResource(selectedStage.bossImageId)
            viewModel.updateAmount(selectedStage.deckSize)

        })

        bossImage.setOnLongClickListener {
            val stageId = viewModel.chosenStageID.value ?: 0
            showMonsterDetailDialog(stageId)
            true
        }


        backBtn.setOnClickListener{
            viewModel.subtractChosenStageID()
        }
        nextBtn.setOnClickListener{
            viewModel.addChosenStageID()
        }

        infoButton = findViewById(R.id.game_monster_info_btn)

        infoButton.setOnClickListener {
            val stageId = viewModel.chosenStageID.value ?: 0
            showMonsterDetailDialog(stageId)
        }



    }

    private fun buttonClickableStatus(){

        button.isVisible = true
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.enable_color))
        button.invalidate()

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
            startActivity(intent)
            finish()
        }
    }

    //update the color state of the start button
    private fun updateStartButtonState(isEnable:Boolean, selectedPetArray: MutableList<Int?>){
        if(isEnable){
            //validate start button and let it go to the next page
            button.isEnabled = true
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.enable_color))
            button.invalidate()

            button.setOnClickListener {

                //for stage id
                Log.d("stageID", "chosen: ${viewModel.getChosenStageID()}")
                sharedPreferenceManager.saveStageChoose(viewModel.getChosenStageID())

                Log.d("stageID", "saved: ${sharedPreferenceManager.getStageChoose()}")
                val intent = Intent(this, GamePetChooseMainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        else {
            // change it into non-selectable status
            button.isEnabled = false
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled_color))
            button.invalidate()

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

    //set the show dialog funciton
    private fun showMonsterDetailDialog(stageId:Int){
        val dialog = GameMonsterDialogFragment.newInstance(stageId)
        dialog.show(supportFragmentManager, "MonsterChooseDialog")

    }




}