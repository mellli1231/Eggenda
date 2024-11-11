package com.example.eggenda.gamePetChoose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gamePlay.gameActivity
import com.example.eggenda.gamePlay.petInfo2
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
    private lateinit var petInfo : petInfo2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_character_choose)

        //initalize classes
        sharedPreferenceManager = SharedPreferenceManager(this)
        petInfo = petInfo2()

        //to set the game should have maximum how many characters
//        var maxSelectableImage = sharedPreferenceManager.getPetsAmountInt()

        var maxSelectableImage = sharedPreferenceManager. getPetsAmount()


        //get the pets ID with int array
        val petsTotalAmount : Int = petInfo.getTotalPetAmount()

        //the mutuable list that can save the list of the photots,that can send to the game part
        val selectedPetPhoto =  MutableList<Int?>(maxSelectableImage){ null }

        //initialize view model
        val factory = GamePetChooseViewModel.GamePetChooseViewModelFactory(sharedPreferenceManager)
        petsViewModel = ViewModelProvider(this, factory).get(GamePetChooseViewModel::class.java)

        //initialize pets array that has in the code in int array
        allPetsArrayID = IntArray(petsTotalAmount) {it + 1}

        //initialize start button
        startButton = findViewById(R.id.fight_start)
        updateStartButtonState(false, selectedPetPhoto)   //update the start button to gray

        //set the character 3 in a row
        characterRecyclerView = findViewById(R.id.game_characterchoose_recyclerView)
        characterRecyclerView.layoutManager = GridLayoutManager(this, 3)

        //set adapter for showing pets
        petsAdapter = GamePetChooseAdapter(
            allPetsArrayID,
            selectedPetPhoto,
            sharedPreferenceManager,
            this,
            { imageId -> onImageSelected(imageId, selectedPetPhoto) },
            {imageId ->  onImageDeselected(imageId, selectedPetPhoto) },
            {petId -> showPetDetailDialog(petId)})
        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter


        //set the selected image view with customized amount
        characterSelectedList = findViewById(R.id.game_character_selectedList)
        val spanCount = if(maxSelectableImage > 0) maxSelectableImage else 3
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
            updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetPhoto)
        })

        petsViewModel.updateList(selectedPetPhoto)

    }

    //make updates of selected photos with selectedAdapter
    private fun onImageSelected(imageId: Int, selectedPetPhoto :MutableList <Int?> ) {
        val emptyIndex = selectedPetPhoto.indexOfFirst { it == null}
        //if there is space
        if(emptyIndex != -1){
            selectedPetPhoto[emptyIndex] = imageId //renew the photo
            selectPetsAdapter.updateImageAt(emptyIndex, imageId)

            // Update the ViewModel with the current selection
            petsViewModel.updateList(selectedPetPhoto)

        }
        updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetPhoto)
        petsAdapter.notifyDataSetChanged() // 更新所有圖片的 RecyclerView
        selectPetsAdapter.notifyDataSetChanged() // 更新已選擇圖片的 RecyclerView
    }

    //make deletetion and change of list with selected Adapter
    private fun onImageDeselected(imageId: Int, selectedPetPhoto: MutableList<Int?>) {
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

        updateStartButtonState(petsViewModel.isSelectionComplete(), selectedPetPhoto)

        Log.d("MainActivity", "Image removed: $imageId, New list: ${selectedPetPhoto.joinToString()}")

        petsAdapter.notifyDataSetChanged()
        selectPetsAdapter.notifyDataSetChanged()
    }

    //update the color state of the start button
    private fun updateStartButtonState(isEnable:Boolean, selectedPetPhoto: MutableList<Int?>){
        if(isEnable){
            //validate start button and let it go to the next page
            startButton.isEnabled = true
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.enable_color))
            startButton.invalidate()

            startButton.setOnClickListener{
                //save the list to shared preference
                sharedPreferenceManager = SharedPreferenceManager(this)     //initalize mananger
                val petIntArray : IntArray = selectedPetPhoto.filterNotNull().toIntArray()
                sharedPreferenceManager.savePetsList(petIntArray)

                //go to game start activity
                val intent = Intent(this,gameActivity::class.java )
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

    //set the show dialog funciton
    private fun showPetDetailDialog(petId:Int){

        val dialog = PetChooseDialogFragment.newInstance(petId)
        dialog.show(supportFragmentManager, "PetChooseDialog")

    }

}