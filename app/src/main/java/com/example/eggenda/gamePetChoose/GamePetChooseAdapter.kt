package com.example.eggenda.gamePetChoose

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gamePlay.dict
import com.example.eggenda.gamePlay.petInfo2


class GamePetChooseAdapter(private var characterList: IntArray,
                           private val selectedPetsID: MutableList<Int?>,
                           private val sharedPreferenceManager: SharedPreferenceManager,
                           private val context: Context,
                           private val onImageSelected: (Int) -> Unit,
                           private val onImageDeselected: (Int) -> Unit,
                           private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<GamePetChooseAdapter.ViewHolder>(){

    //gson list about the status of the pets
    private val ownedPetsTemp = sharedPreferenceManager.getPetOwnership()   //ArrayList <Int>
    private val ownedPets : IntArray = ownedPetsTemp.toIntArray()           //change it to Int Array

    private var temp : Int = 0

    //filter out the unlocked character list
    private var filteredPetsList = characterList.filterIndexed { index, _->
        index < ownedPets.size && ownedPets[index] == 1
    } .also { Log.d("FilteredPets", "filteredPetsList size: ${it.size}") }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_item_frame, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {return filteredPetsList.size}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("GamePetChooseAdapter", "Binding position: $position")
        val petInfo = petInfo2()
        val petId = petInfo.getPetInfoById(filteredPetsList[position])?.id!!
        petId?.let { holder.bind(it) }
        sharedPreferenceManager.saveFilteredPetsAmount(filteredPetsList.size)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val petInfo  =  petInfo2()

        fun bind(petID: Int) {
            val petDetails = petInfo.getPetInfoById(petID)
            imageView.setImageResource(petInfo.getPetInfoById(petID)?.imageId!!)     // Set the image resource

            // Set the pet image
            petDetails?.let {
                imageView.setImageResource(it.imageId)

                // Change the item's background color based on pet properties
                val backgroundColor = when (it.element) { // Assuming `type` is a property
                    dict.ELEMENT_FIRE -> context.getColor(R.color.fire)
                    dict.ELEMENT_WATER -> context.getColor(R.color.water)
                    dict.ELEMENT_FOREST-> context.getColor(R.color.forest)
                    else -> context.getColor(R.color.silver_rare)
                }
                itemView.setBackgroundColor(backgroundColor) // Apply background color
            }

            // Set click listener on the item
            itemView.setOnClickListener {
                //unselect it if the photos has already being chosen
                if (selectedPetsID.contains(petID)) { onImageDeselected(petID)      //return pet info id
                } else { onImageSelected(petID) }                                   //select if the image has not been selected
            }

            //trigger the long-click logic for showing details
            itemView.setOnLongClickListener {
                onLongClick(petID)
                true
            }
        } //end of bind funciton
    }


    fun updatePetsChoose(newImages: IntArray) {
        characterList = newImages
        filteredPetsList = characterList.filterIndexed { index, _ ->
            index < ownedPets.size && ownedPets[index] == 1
        }
        notifyDataSetChanged()
    }

}