package com.example.eggenda.gameTutorial

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import com.example.eggenda.R

class tutorialActivity : AppCompatActivity() {


    private lateinit var tutInfo: tutorials
    private lateinit var viewmodel: tutorialViewmodel

    private lateinit var tutTitle: TextView
    private lateinit var tutImage: ImageView
    private lateinit var tutDes: TextView
    private lateinit var backBtn:ImageView
    private lateinit var nextBtn:ImageView
    private lateinit var dialogButton : ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_tutorial)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        tutInfo = tutorials()
        viewmodel = tutorialViewmodel()
        tutTitle = findViewById(R.id.tutTitle)
        tutImage = findViewById(R.id.tut_dialog_Image)
        backBtn = findViewById(R.id.tut_dialog_back_button)
        nextBtn = findViewById(R.id.tut_dialog_next_button)
        tutDes = findViewById(R.id.tut_dialog_Des)
        dialogButton = findViewById(R.id.tutorial_check_dialog_button)
        var chosenTutorialId = 0

        viewmodel.chosenTutID.observe(this, Observer { newTutId->
            if(newTutId == 0 ){
                backBtn.isInvisible = true
            }
            else{
                backBtn.isInvisible = false
            }
            if(newTutId  == tutInfo.tutTotalNum - 1){
                nextBtn.isInvisible = true
            }
            else{
                nextBtn.isInvisible = false
            }
            val selectedTut = tutInfo.getTutorialById(newTutId)
            chosenTutorialId = newTutId
            val tutTitleStr = "Tutorial "+(newTutId + 1).toString()
            val tutCoverImage = selectedTut?.coverImage
            val tutCoverDes = selectedTut?.coverDescription
            tutTitle.text = tutTitleStr
            if (tutCoverImage != null) {
                tutImage.setImageResource(tutCoverImage)
            }
            tutDes.text = tutCoverDes
        })

        backBtn.setOnClickListener{
            viewmodel.subtractChosenTutID()
        }
        nextBtn.setOnClickListener{
            viewmodel.addChosenTutID()
        }

        dialogButton.setOnClickListener {
            showTutorialDialog(chosenTutorialId)
        }


    }

    private fun showTutorialDialog(tutorialID : Int){
        val dialog = tutorialDialogFragment.newInstance(tutorialID)
        dialog.show(supportFragmentManager, "TutorialDialog")
    }
}