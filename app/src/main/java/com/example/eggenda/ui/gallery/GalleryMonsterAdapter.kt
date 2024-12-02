package com.example.eggenda.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.petInfo2
import com.example.eggenda.gamePlay.stageInfo

class GalleryMonsterAdapter(private var stageList : IntArray,
                            private val sharedPreferenceManager: SharedPreferenceManager,
                            private val onMonsterClick: (Int) -> Unit,
                            private val onLongClick : (Int) -> Unit):
    RecyclerView.Adapter<GalleryMonsterAdapter.ViewHolder>() {

    private var stageDoneArrayList = sharedPreferenceManager.getStageDone()   //ArrayList <Int>     //change it to Int Array



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_pet_items_frame, parent, false)

        val stageInfo  =  stageInfo()
        if(stageDoneArrayList.size != stageInfo.stageTotalNum){
            stageDoneArrayList = getNewStageDone(stageDoneArrayList, stageInfo.stageTotalNum)
            sharedPreferenceManager.saveStageDone(stageDoneArrayList)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {return stageList.size }

    override fun onBindViewHolder(holder: GalleryMonsterAdapter.ViewHolder, position: Int) {
        Log.d("Gallery Monster Fragement", "Binding position: $position")
        val stageInfo = stageInfo()
        val monsterId = stageInfo.StageInfoMap(position)?.id!!
        monsterId?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val stageInfo  =  stageInfo()

        fun bind(monsterID: Int) {
            imageView.setImageResource(stageInfo.StageInfoMap(monsterID)?.bossImageId!!)     // Set the image resource

            // Set click listener on the item
            itemView.setOnClickListener { onMonsterClick(monsterID) }
            //trigger the long-click logic for showing details
            itemView.setOnLongClickListener {
                onLongClick(monsterID)
                true
            }

        } //end of bind funciton

    }

    private fun getNewStageDone(oldStageDone: ArrayList<Int>, stageCount: Int):ArrayList<Int>{
        val ret = ArrayList<Int>()
        for(i in 1..stageCount){
            ret.add(0)
        }
        var index = 0
        while(index < oldStageDone.size && index < stageCount){
            ret[index] = oldStageDone[index]
            index ++
        }
        return ret
    }

    fun updateMonsterChoose(newImages: IntArray) {
        stageList = newImages
        notifyDataSetChanged()
    }
}