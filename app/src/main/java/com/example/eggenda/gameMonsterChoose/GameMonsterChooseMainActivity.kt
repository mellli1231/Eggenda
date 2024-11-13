package com.example.eggenda.gameMonsterChoose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.GamePetChooseMainActivity
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.stageInfo

class GameMonsterChooseMainActivity : AppCompatActivity () {

    private lateinit var button: Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var stageInfo: stageInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_monster_choose)

        sharedPreferenceManager = SharedPreferenceManager(this)
        stageInfo = stageInfo()

        val amount = 5
        sharedPreferenceManager.savePetsAmount(amount)

        val monstersName = stageInfo.StageInfoMap(0)!!.name

        val retrievedList = sharedPreferenceManager.getPetOwnership()
        val intArrayhihi : IntArray = retrievedList.toIntArray()
        Log.d("Retrieved List", "IntArray: ${intArrayhihi.joinToString(",")}")

        button = findViewById(R.id.game_monster_go)

        button.setOnClickListener {
            val intent = Intent(this, GamePetChooseMainActivity::class.java)
            startActivity(intent)
        }



    }


}