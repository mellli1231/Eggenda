package com.example.eggenda.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.ui.gallery.GalleryAdapter.ViewHolder
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.petInfo2

class GalleryAdapter(private var characterList : IntArray,
                     private val sharedPreferenceManager: SharedPreferenceManager,
                     private val onPetClick: (Int) -> Unit,
                     private val onLongClick : (Int) -> Unit
):RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){

    private val ownedPetsTemp = sharedPreferenceManager.getPetOwnership()   //ArrayList <Int>
    private var ownedPets : IntArray = ownedPetsTemp.toIntArray()           //change it to Int Array

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_pet_items_frame, parent, false)

        //check if the owned pets size is not as same as total size
        if(ownedPets.size < characterList.size){
            val intList =ownedPets.toMutableList()  // Convert to a mutable list
            intList.add(0)                          // Add an integer
            ownedPets = intList.toIntArray()
            val temp : Array<Int> = intList.toTypedArray()
            sharedPreferenceManager.savePetOwnership(temp)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {return characterList.size }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Gallery Fragment", "Binding position: $position")
        val petInfo = petInfo2()
        val petId = petInfo.getPetInfoById(characterList[position])?.id!!
        petId?.let { holder.bind(it) }
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
//        val textView : TextView = itemView.findViewById(R.id.)
        private val overlayView: View = itemView.findViewById(R.id.petOverlay)
        val petInfo  =  petInfo2()

        fun bind(petID: Int) {
            imageView.setImageResource(petInfo.getPetInfoById(petID)?.imageId!!)     // Set the image resource

            if(ownedPets[petID] == 0){
                // Pet is owned, hide gray overlay and enable clicks
                overlayView.alpha = 0.5f  // 50% transparency
                overlayView.visibility = View.VISIBLE

            }
            else {
                // Pet is owned, hide gray overlay and enable clicks
                overlayView.visibility = View.GONE
            }

            // Set click listener on the item
            itemView.setOnClickListener {
               onPetClick(petID)
            } //end of onclicklistener


            //trigger the long-click logic for showing details
            itemView.setOnLongClickListener {
                onLongClick(petID)
                true
            }
        } //end of bind funciton
    }


    fun updatePetsChoose(newImages: IntArray) {
        characterList = newImages
//        filteredPetsList = characterList.filterIndexed { index, _ ->
//            index < ownedPets.size && ownedPets[index] == 1
//        }
        notifyDataSetChanged()
    }


}