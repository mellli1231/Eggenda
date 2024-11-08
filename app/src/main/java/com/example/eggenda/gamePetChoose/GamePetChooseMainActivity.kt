package com.example.eggenda.gamePetChoose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gamePlay.gameActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamePetChooseMainActivity : ComponentActivity(){
    private lateinit var petsViewModel : GamePetChooseViewModel
    private lateinit var petsAdapter: GamePetChooseAdapter
    private lateinit var selectPetsAdapter: SelectAdapter
    private lateinit var characterSelectedList : RecyclerView
    private lateinit var characterRecyclerView : RecyclerView
    private lateinit var startButton: Button
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    //to set the game should have maximum how many characters
    //use shared preference
    private var maxSelectableImage = 5


    //the mutuable list that can save the list of the photots,that can send to the game part
    private val selectedPetPhoto =  MutableList<Int?>(maxSelectableImage){ null }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_character_choose)

        //set and save the shared preference of max amount of pets can play
        // should be at previous page to do
        val maxAmountPetsSP = getSharedPreferences("Max_pets_allow", MODE_PRIVATE)
        // max Selectable Image = maxAmountPetsSp
        val editor = maxAmountPetsSP.edit()
        editor.putInt("max_key", maxSelectableImage)
        editor.apply()

        //it should be set on main page not here
        val ownedPetsStatus = listOf(true, true, true, true, true)
        val ownedPetsStatusSp = getSharedPreferences("Pets_status", MODE_PRIVATE)
        val editor1 = ownedPetsStatusSp.edit()
        editor1.putString("owned_pets_key", Gson().toJson(ownedPetsStatus))
        editor1.apply()

        val sharedPreferences = getSharedPreferences("Pets_status", Context.MODE_PRIVATE)
        val ownedPetsJson = sharedPreferences.getString("owned_pets_key", "[]")
        Log.d("OwnedPetsJson", ownedPetsJson ?: "null")

        // confirm JSON can be used correctly
        try {
            val ownedPets: List<Boolean> = Gson().fromJson(ownedPetsJson, object : TypeToken<List<Boolean>>() {}.type) ?: listOf()
            Log.d("OwnedPets", ownedPets.toString())
        } catch (e: Exception) {
            Log.e("GsonError", "Failed to parse owned pets", e)
        }

        //initalize view model
        val factory = GamePetChooseViewModel.GamePetChooseViewModelFactory(maxAmountPetsSP)
        petsViewModel = ViewModelProvider(this, factory).get(GamePetChooseViewModel::class.java)
//        petsViewModel = ViewModelProvider(this).get(GamePetChooseViewModel::class.java)


        //initialize start button
        startButton = findViewById(R.id.fight_start)
        //update the start button to gray
        updateStartButtonState(false)


        //set the character 3 in a row
        characterRecyclerView = findViewById(R.id.game_characterchoose_recyclerView)
        characterRecyclerView.layoutManager = GridLayoutManager(this, 5)

        //set adapter for showing pets
        petsAdapter = GamePetChooseAdapter(petsViewModel.allPets.value ?: emptyList(), selectedPetPhoto,this, { imageId ->
            onImageSelected(imageId) }, {imageId ->  onImageDeselected(imageId) })
        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter


        //set the selected image view with customized amount
        characterSelectedList = findViewById(R.id.game_character_selectedList)
        characterSelectedList.layoutManager = GridLayoutManager(this, maxSelectableImage)

        //set the adapter for selecting pets
        selectPetsAdapter = SelectAdapter(maxSelectableImage, selectedPetPhoto)
        characterSelectedList.adapter = selectPetsAdapter

        //observe the changes of the pets photo
        petsViewModel.allPets.observe(this, Observer { photos ->

            Log.d("MainActivity", "allPets updated: $photos")
            petsAdapter.updateImages(photos)
        })

        petsViewModel.selectedPets.observe(this, Observer { photos ->
            selectPetsAdapter.updateImages(photos)
            Log.d("Main Activity", "updated")
            updateStartButtonState(petsViewModel.isSelectionComplete())
        })

        petsViewModel.updateList(selectedPetPhoto)

    }

    //make updates of selected photos with selectedAdapter
    private fun onImageSelected(imageId: Int) {
        val emptyIndex = selectedPetPhoto.indexOfFirst { it == null}

        //if there is space
        if(emptyIndex != -1){
            selectedPetPhoto[emptyIndex] = imageId //renew the photo
            selectPetsAdapter.updateImageAt(emptyIndex, imageId)

            // Update the ViewModel with the current selection
            petsViewModel.updateList(selectedPetPhoto)

        }
        updateStartButtonState(petsViewModel.isSelectionComplete())
        petsAdapter.notifyDataSetChanged() // 更新所有圖片的 RecyclerView
        selectPetsAdapter.notifyDataSetChanged() // 更新已選擇圖片的 RecyclerView
    }

    //make deletetion and change of list with selected Adapter
    private fun onImageDeselected(imageId: Int) {
        // find the chosen place of the photo
        val index = selectedPetPhoto.indexOf(imageId)
        if (index != -1) { // if photo found

            selectedPetPhoto[index] = null
//            selectPetsAdapter.removeImage(imageId)

            // clear the chosen photo
            selectedPetPhoto[index] = null

            // move photo
            for (i in index until selectedPetPhoto.size - 1) {
                selectedPetPhoto[i] = selectedPetPhoto[i + 1] // 將下一個圖片移到當前位置
            }
            selectedPetPhoto[selectedPetPhoto.size - 1] = null // 清空最後一個位置

            // Update the ViewModel with the current selection
            petsViewModel.updateList(selectedPetPhoto)

            Log.d("MainActivity", "Image deselected: $imageId, Current selection: ${selectedPetPhoto.joinToString()}")
//
        }

        updateStartButtonState(petsViewModel.isSelectionComplete())

        Log.d("MainActivity", "Image removed: $imageId, New list: ${selectedPetPhoto.joinToString()}")

        petsAdapter.notifyDataSetChanged()
        selectPetsAdapter.notifyDataSetChanged()
    }

    //update the color state of the start button
    private fun updateStartButtonState(isEnable:Boolean){
        if(isEnable){
            //validate start button and let it go to the next page
            startButton.isEnabled = true
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.enable_color))
            startButton.invalidate()

            startButton.setOnClickListener{
                //save the list to shared preference
                sharedPreferenceManager = SharedPreferenceManager(this)     //initalize mananger
                sharedPreferenceManager.savePetsList(selectedPetPhoto)

                //go to game start activity
                val intent = Intent(this,gameActivity::class.java )
                startActivity(intent)
            }


        }
        else {
            startButton.isEnabled = false
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled_color))
            startButton.invalidate()

        }
    }

}