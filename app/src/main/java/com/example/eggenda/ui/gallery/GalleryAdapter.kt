package com.example.eggenda.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.ui.gallery.GalleryAdapter.ViewHolder
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.petInfo2

class GalleryAdapter(private var characterList : IntArray,
                     private val sharedPreferenceManager: SharedPreferenceManager,
                     private val onLongClick : (Int) -> Unit
):RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){

    private val ownedPetsTemp = sharedPreferenceManager.getPetOwnership()   //ArrayList <Int>
    private val ownedPets : IntArray = ownedPetsTemp.toIntArray()           //change it to Int Array

    private var temp : Int = 0

    //filter out the unlocked character list
//    private var filteredPetsList = characterList.filterIndexed { index, _->
//        index < ownedPets.size && ownedPets[index] == 1
//    } .also { Log.d("FilteredPets", "filteredPetsList size: ${it.size}") }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_pet_items_frame, parent, false)
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
        private val overlayView: View = itemView.findViewById(R.id.petOverlay)
        val petInfo  =  petInfo2()

        fun bind(petID: Int) {
            imageView.setImageResource(petInfo.getPetInfoById(petID)?.imageId!!)     // Set the image resource

            if(ownedPets[petID] == 0){
                //pets is unowned, show gray overlay and enable clicks
                overlayView.visibility = View.VISIBLE
                itemView.isClickable = false
                itemView.isEnabled = false

            }
            else {
                // Pet is owned, hide gray overlay and enable clicks
                overlayView.visibility = View.GONE
                itemView.isClickable = true
                itemView.isEnabled = true
            }
            //trigger the long-click logic for showing details
            itemView.setOnLongClickListener {
                val petInfoID = temp
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