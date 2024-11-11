package com.example.eggenda.gamePetChoose

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gamePlay.petInfo
import com.example.eggenda.gamePlay.petInfo2

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class GamePetChooseAdapter(private var characterList: IntArray,
                           private val selectedImages: MutableList<Int?>,
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

    //    override fun getItemCount(): Int = filteredPetsList.size
    override fun getItemCount(): Int {return filteredPetsList.size}


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("GamePetChooseAdapter", "Binding position: $position")

        Log.d("Choose Adapter" , "what da fuck $filteredPetsList[position]")
//        holder.bind(filteredPetsList[position])
        val petInfo = petInfo2()
        val petImageId = petInfo.getPetInfoById(filteredPetsList[position])?.imageId
            temp = petInfo.getPetInfoById(filteredPetsList[position])?.id!!
//        Log.d("Choose Adapter" , "what da fuck $filteredPetsList[position]")
        petImageId?.let { holder.bind(it) }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val petInfo  =  petInfo2()

        fun bind(photoResId: Int) {
//            val photoResId = petInfo.getPetInfoById(petInfoID)?.imageId

            if (photoResId != null) { imageView.setImageResource(photoResId) } // Set the image resource

            // Set click listener on the item
            itemView.setOnClickListener {

                //unselect it if the photos has already being chosen
                if (selectedImages.contains(photoResId)) {
                    if (photoResId != null) { onImageDeselected(photoResId) }

                } //select if the image has not been selected
                else {
                    if (photoResId != null) { onImageSelected(photoResId) }

                }
            } //end of onclicklistener

            //hello ashpfiohj qpe

            //trigger the long-click logic for showing details
            itemView.setOnLongClickListener {
                val petInfoID = temp
                onLongClick(petInfoID)
                true
            }
        } //end of bind funciton

//        fun bind(petInfoID: Int) {
//            val photoResId = petInfo.getPetInfoById(petInfoID)?.imageId
//
//            if (photoResId != null) { imageView.setImageResource(photoResId) } // Set the image resource
//
//            // Set click listener on the item
//            itemView.setOnClickListener {
//
//                //unselect it if the photos has already being chosen
//                if (selectedImages.contains(photoResId)) {
//                    if (photoResId != null) { onImageDeselected(photoResId) }
//
//                } //select if the image has not been selected
//                else {
//                    if (photoResId != null) { onImageSelected(photoResId) }
//
//                }
//            } //end of onclicklistener
//
//            //hello ashpfiohj qpe
//
//            //trigger the long-click logic for showing details
//            itemView.setOnLongClickListener {
//                onLongClick(petInfoID)
//                true
//            }
//        } //end of bind funciton
    }



    fun updateImages(newImages: IntArray) {
        characterList = newImages
        filteredPetsList = characterList.filterIndexed { index, _ ->
            index < ownedPets.size && ownedPets[index] == 1
        }
        notifyDataSetChanged()
    }

}