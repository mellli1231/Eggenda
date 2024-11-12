package com.example.eggenda.gamePetChoose

import com.example.eggenda.R

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.gamePlay.petInfo2

class SelectAdapter(private var itemCount: Int,
                    private var selectedIDs: MutableList<Int?>,
                    private var petInfo : petInfo2)
    : RecyclerView.Adapter<SelectAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_item_frame, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = itemCount

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        if (selectedIDs[position] != null) {

            //get the photo id by function
            petInfo = petInfo2()
            val imageSetting = petInfo.getPetInfoById(selectedIDs[position]!!)?.imageId
            holder.imageView.setImageResource(imageSetting!!)

        } else {
            holder.imageView.setImageResource(R.drawable.game_set_colur)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    fun updatePetsAt(position: Int, petId: Int?) {
        selectedIDs[position] = petId // renew specific place's id
        notifyItemChanged(position) // inform updated
    }

    fun updatePets(newPets: MutableList<Int?>) {
        selectedIDs = newPets.toMutableList()
        notifyDataSetChanged()
    }

}