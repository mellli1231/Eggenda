package com.example.eggenda.gamePetChoose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gameMonsterChoose.GameMonsterDialogFragment
import com.example.eggenda.gamePlay.gameActivity
import com.example.eggenda.gamePlay.petInfo2
import com.example.eggenda.gamePlay.stageInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamePetChooseMainActivity : AppCompatActivity(){
    private lateinit var petsViewModel : GamePetChooseViewModel
    private lateinit var petsAdapter: GamePetChooseAdapter
    private lateinit var selectPetsAdapter: SelectAdapter
    private lateinit var characterSelectedList : RecyclerView
    private lateinit var characterRecyclerView : RecyclerView
    private lateinit var startButton: Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var allPetsArrayID : IntArray
    private lateinit var stageInfo : stageInfo
    private lateinit var petInfo : petInfo2

    //load info from other activities
    private var selectedStage : Int = -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game_character_choose)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //initalize classes
        sharedPreferenceManager = SharedPreferenceManager(this)
        petInfo = petInfo2()
        stageInfo = stageInfo()

        selectedStage = sharedPreferenceManager.getStageChoose()


        //to set the game should have maximum how many characters
        var deckSize = stageInfo.StageInfoMap(selectedStage)!!.deckSize

        //the mutuable list that can save the list of the pets ,that can send to the game part
        val selectedPetID =  MutableList<Int?>(deckSize){ null }

        //initialize view model
        val factory = GamePetChooseViewModel.GamePetChooseViewModelFactory(sharedPreferenceManager)
        petsViewModel = ViewModelProvider(this, factory).get(GamePetChooseViewModel::class.java)

        //initialize pets array that has in the code in int array
        //here should take it form pet info class
        allPetsArrayID = intArrayOf(0,1,2,3,4)

        //initialize start button
        startButton = findViewById(R.id.fight_start)
        updateStartButtonState(false, selectedPetID)   //update the start button to gray


        //set the monster class
        val monsterConstraint = findViewById<ConstraintLayout>(R.id.monster_constraint_layout)
        val monsterName = findViewById<TextView>(R.id.game_pet_choose_monseter)
        val monsterPhoto = findViewById<ImageView>(R.id.game_pet_choose_monster_image)
        monsterName.text = stageInfo.StageInfoMap(selectedStage)!!.name
        monsterPhoto.setImageResource(stageInfo.StageInfoMap(selectedStage)!!.bossImageId)

        //set a long click to check monster info
        monsterConstraint.setOnLongClickListener{
            showMonsterDetailDialog(selectedStage)
            true
        }


        //set the character 3 in a row
        characterRecyclerView = findViewById(R.id.game_characterchoose_recyclerView)
        characterRecyclerView.layoutManager = GridLayoutManager(this, 5)

        //set adapter for showing pets
        petsAdapter = GamePetChooseAdapter(
            allPetsArrayID,
            selectedPetID,
            sharedPreferenceManager,
            this,
            {petId -> onImageSelected(petId, selectedPetID) },
            {petId -> onImageDeselected(petId, selectedPetID) },
            {petId -> showPetDetailDialog(petId)})
        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter


        //set the selected image view with customized amount
        characterSelectedList = findViewById(R.id.game_character_selectedList)
//        val spanCount = if(maxSelectableImage > 0) maxSelectableImage else 3
        characterSelectedList.layoutManager = GridLayoutManager(this, deckSize)

        //set the adapter for selecting pets
        selectPetsAdapter = SelectAdapter(deckSize, selectedPetID, petInfo)
        characterSelectedList.adapter = selectPetsAdapter

        //observe the changes of the pets photo
        petsViewModel.allPets.observe(this, Observer { photos ->
            Log.d("MainActivity", "allPets updated: $photos")
            petsAdapter.updatePetsChoose(photos)
        })


        // Observe currently selected pet
        petsViewModel.currentSelectedPet.observe(this) { petId ->
            //set the chosen image
            val petChosenImage = findViewById<ImageView>(R.id.game_pet_choose_image)

            if (petId == null) {
                // No pet selected, return to the initalized format
                petChosenImage.setImageResource(R.drawable.game_choose_nth_3)
                petChosenImage.setOnLongClickListener(null)
            } else {
                // Pet selected, display the image
                petChosenImage.setImageResource(petInfo.getPetInfoById(petId)?.imageId ?: R.layout.gallery_pet_items_frame)
                petChosenImage.visibility = View.VISIBLE

                //set a long click so it can show the info of the pet
                petChosenImage.setOnLongClickListener{
                    showPetDetailDialog(petId)
                    true
                }
            }
        }

        petsViewModel.selectedPets.observe(this, Observer { photos ->
            selectPetsAdapter.updatePets(photos)
            Log.d("Main Activity", "updated")
            updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetID)
        })

        petsViewModel.updateList(selectedPetID)

    }

    //make updates of selected pets with selectedAdapter
    private fun onImageSelected(petId: Int, selectedPetID :MutableList <Int?> ) {
        val emptyIndex = selectedPetID.indexOfFirst { it == null}
        val petSelected = petInfo.getPetInfoById(petId)?.id     //get the petId by function

        //if there is space
        if(emptyIndex != -1){
            selectedPetID[emptyIndex] = petSelected //renew the photo
            selectPetsAdapter.updatePetsAt(emptyIndex, petSelected)

            // Update the ViewModel with the current selection
            petsViewModel.updateList(selectedPetID)
            petsViewModel.selectPet(petId)  //notify View Model of the current selection
        }
        updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetID)
        petsAdapter.notifyDataSetChanged() // update all photo's RecyclerView
        selectPetsAdapter.notifyDataSetChanged() // update selected photo RecyclerView
    }

    //make deletetion and change of list with selected Adapter
    private fun onImageDeselected(petId: Int, selectedPetID: MutableList<Int?>) {
        val index = selectedPetID.indexOf(petId)         // find the chosen place of the photo

        if (index != -1) { // if photo found
            selectedPetID[index] = null // clear the chosen photo

            // move photo
            for (i in index until selectedPetID.size - 1) {
                selectedPetID[i] = selectedPetID[i + 1] // the next photo move the the current position
            }
            selectedPetID[selectedPetID.size - 1] = null // clear the last position
            petsViewModel.updateList(selectedPetID)      // Update the ViewModel with the current selection
            petsViewModel.clearSelection()              // Clear the chosen image on top

        }

        updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetID)
        petsAdapter.notifyDataSetChanged()
        selectPetsAdapter.notifyDataSetChanged()
    }

    //update the color state of the start button
    private fun updateStartButtonState(isEnable:Boolean, selectedPetArray: MutableList<Int?>){
        if(isEnable){
            //validate start button and let it go to the next page
            startButton.isEnabled = true
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.enable_color))
            startButton.invalidate()

            startButton.setOnClickListener{
                //save the list to shared preference
                sharedPreferenceManager = SharedPreferenceManager(this)     //initalize mananger
                val petIntArray : IntArray = selectedPetArray.filterNotNull().toIntArray()
                sharedPreferenceManager.savePetsList(petIntArray)

                //go to game start activity
                val intent = Intent(this,gameActivity::class.java )
                finish()
                startActivity(intent)
            }

        }
        else {
            // change it into non-selectable status
            startButton.isEnabled = false
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled_color))
            startButton.invalidate()

        }
    }

    //set the show pet dialog function
    private fun showPetDetailDialog(petId:Int){
        val dialog = PetChooseDialogFragment.newInstance(petId)
        dialog.show(supportFragmentManager, "PetChooseDialog")

    }

    //set the show monster dialog function
    private fun showMonsterDetailDialog(stageId : Int){
        val dialog = GameMonsterDialogFragment.newInstance(stageId)
        dialog.show(supportFragmentManager, "MonsterChooseDialog")

    }

}