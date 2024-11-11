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
                           private val onImageDeselected: (Int) -> Unit
) : RecyclerView.Adapter<GamePetChooseAdapter.ViewHolder>(){

    //gson list about the status of the pets
    private val ownedPetsTemp = sharedPreferenceManager.getPetOwnership()   //ArrayList <Int>
    private val ownedPets : IntArray = ownedPetsTemp.toIntArray()           //change it to Int Array

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
        val petInfo = petInfo2()
        val petImageId = petInfo.getPetInfoById(filteredPetsList[position])?.imageId
        petImageId?.let { holder.bind(it) }

////        update the selection state and display the label index into the selected image
//        holder.imageView.isSelected = selectedImages.contains(filteredPetsList[position])
//
//        Log.d("Choosing Adpater", "position: $position")

//        //setting the number pads label
//        val labelIndex = selectedImages.indexOf(position)
//
//        Log.d("Choosing Adpater", "label: index: $labelIndex")


//        //do not let the pets be choosable if the pets are not unlocked
//        if(position < ownedPets.size && !ownedPets[position]){
//            holder.grey_layer.visibility = View.VISIBLE
//            holder.imageView.isEnabled = false      //to let the photo be unclikable
//        }
//        else {
//            holder.grey_layer.visibility = View.GONE            //to let the photo be normal
//            holder.imageView.isEnabled = true       //to let the photo be clikable
//
//        }

        //if the pet here is not yet getable, then just let it out of the list, otherwise let it stays at the list
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
//        val grey_layer: View = itemView.findViewById(R.id.gray_layer)
//        val textViewLabel : TextView = itemView.findViewById(R.id.textViewLabel)

        fun bind(photoResId: Int) {
            imageView.setImageResource(photoResId) // Set the image resource
            // Set click listener on the item
            itemView.setOnClickListener {

//               if(imageView.isEnabled){     //check if the photo is enabled
                if (selectedImages.contains(photoResId)) {
                    onImageDeselected(photoResId)

                } else {
                    onImageSelected(photoResId)

                }
//               }
            }
        }
    }



    fun updateImages(newImages: IntArray) {
        characterList = newImages
        filteredPetsList = characterList.filterIndexed { index, _ ->
            index < ownedPets.size && ownedPets[index] == 1
        }
        notifyDataSetChanged()
    }

}