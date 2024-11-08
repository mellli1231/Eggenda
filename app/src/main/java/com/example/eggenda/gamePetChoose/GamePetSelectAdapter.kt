package com.example.eggenda.gamePetChoose

import com.example.eggenda.R

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SelectAdapter(private var itemCount: Int, private var selectedImages: MutableList<Int?>)
    : RecyclerView.Adapter<SelectAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_item_frame, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = itemCount

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
//       selectedImages[position].let { imageId ->
//           selectedImages[position]?.let {
//               holder.imageView.setImageResource(it) }
//       } ?: run{
//           holder.imageView.setImageResource(R.drawable.rectangle_blocks)
//       }

        if (selectedImages[position] != null) {
            holder.imageView.setImageResource(selectedImages[position]!!)
        } else {
            holder.imageView.setImageResource(R.drawable.game_set_colur)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

    }

    fun updateImageAt(position: Int, imageId: Int?) {
        selectedImages[position] = imageId // 更新指定位置的圖片
        notifyItemChanged(position) // 通知更新
    }

    fun updateImages(newImages: MutableList<Int?>) {
        selectedImages = newImages.toMutableList()
        notifyDataSetChanged()
    }

    //when remove a photo, go to front one
//    fun removeImage (imageId: Int?){
//        val index = selectedImages.indexOf(imageId)
//        if(index != -1){
//
//            for(i in index until selectedImages.size - 1){
//                selectedImages[i] = selectedImages[i+1]
//            }
//            selectedImages[selectedImages.size-1] = null
//            // Notify that an item was removed
//            notifyItemRemoved(index)
//            notifyItemRangeChanged(index, selectedImages.size-index)
//            // Log the current state of the list
//            Log.d("SelectAdapter", "Image removed: $imageId, New list: ${selectedImages.joinToString()}")
//        }
//
//        Log.d("SelectAdapter", "Image removed: $imageId, New list: ${selectedImages.joinToString()}")
//    }

}