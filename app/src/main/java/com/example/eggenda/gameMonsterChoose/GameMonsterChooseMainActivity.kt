package com.example.eggenda.gameMonsterChoose

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.GamePetChooseMainActivity
import com.example.eggenda.gamePetChoose.SharedPreferenceManager

class GameMonsterChooseMainActivity : AppCompatActivity () {

    private lateinit var button: Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_monster_choose)

        sharedPreferenceManager = SharedPreferenceManager(this)

        val amount = 3
        sharedPreferenceManager.savePetsAmount(amount)

        button = findViewById(R.id.game_monster_go)

        button.setOnClickListener {
            val intent = Intent(this, GamePetChooseMainActivity::class.java)
            startActivity(intent)
        }



    }


}