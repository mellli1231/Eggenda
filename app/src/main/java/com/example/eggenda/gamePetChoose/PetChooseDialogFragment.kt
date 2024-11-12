package com.example.eggenda.gamePetChoose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.eggenda.R
import com.example.eggenda.gamePlay.dict
import com.example.eggenda.gamePlay.petInfo2

class PetChooseDialogFragment  : DialogFragment (){

    //all the info about the pets
    private var petId : Int = 0
    private var petName: String? = null
    private var petImageId: Int = 0
    private var petElement: Int = 0
    private var petAttackType: Int = 0
    private var petDamange : Int = 0
    private var petCount : Int = 0
    private var petDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view =  inflater.inflate(R.layout.game_pet_choose_dialog, container, false)
        val view =  inflater.inflate(R.layout.game_card_layout, container, false)
        val petInfo = petInfo2()
        val getId = arguments?.getInt(ARG_PET_ID)
        val getpet = getId?.let { petInfo.getPetInfoById(it) }

        val element_background = dict.ELEMENT_STRING[getpet?.element!!]
        val rarity = dict.RARETY_STRING[getpet?.rarity!!]
        val rootLayout : View = view.findViewById(R.id.dialog_root_layout)

        when (rarity){
            "LEGENDARY" ->
                when(element_background){
                    "Fire" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_fire_gold)
                    "Water" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_water_gold)
                    "Forest" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_forest_gold)
                }
            "RARE" ->
                when(element_background){
                    "Fire" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_fire_silver)
                    "Water" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_water_silver)
                    "Forest" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_forest_silver)
                }

            "NORMAL" ->
                when(element_background){
                    "Fire" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_fire_gray)
                    "Water" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_water_gray)
                    "Forest" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_forest_gray)
                }


        }

        //set the data on views
        if (getpet != null) {
            view.findViewById<ImageView>(R.id.card_pet_image).setImageResource(getpet.imageId)
            view.findViewById<TextView>(R.id.card_petId).text = getpet.id.toString()
            view.findViewById<TextView>(R.id.card_petName).text = getpet.name
            view.findViewById<TextView>(R.id.dialog_skillCondition).text = getpet.description
            view.findViewById<TextView>(R.id.dialog_attackType).text = dict.ATK_STRING[getpet.attackType]
            view.findViewById<TextView>(R.id.dialog_count).text = getpet.count.toString()
        }

        return view

    }

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val petInfo = petInfo2()
        val getId = arguments?.getInt(ARG_PET_ID)
        val getpet = getId?.let { petInfo.getPetInfoById(it) }
    }

    companion object{
        private const val ARG_PET_ID = "pet_id"

        fun newInstance(petId : Int) : PetChooseDialogFragment{
            val fragment = PetChooseDialogFragment()
            val args = Bundle().apply {putInt(ARG_PET_ID, petId)}
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()

        //set the size of the dialog
        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.card_width),
            resources.getDimensionPixelSize(R.dimen.card_hight),

        )
    }


}