package com.example.eggenda.gamePlay

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.eggenda.R
import com.example.eggenda.gamePetChoose.PetChooseDialogFragment
import com.example.eggenda.gamePetChoose.PetChooseDialogFragment.Companion

class petDialog : DialogFragment(){

    private var petId: Int? = null
    private var petOrder: Int? = null
    private var petCondition : String ?= null
    private var petNextDmg : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.game_pet_play_dialog, container, false)

        val petInfo = petInfo2()
        val getId = arguments?.let {
            petId = it.getInt(ARG_PET_ID)
            petOrder = it.getInt(ARG_PET_ORDER)
            petCondition = it.getString(ARG_PET_STATUS)
            petNextDmg = it.getString(ARG_PET_NEXT_DMG)
        }
        val getpet = getId?.let { petInfo.getPetInfoById(petId!!) }


        val element_background = dict.ELEMENT_STRING[getpet?.element!!]
        val rarity = dict.RARETY_STRING[getpet?.rarity!!]
        val rootLayout : View = view.findViewById(R.id.game_dialog_root_layout)


        //rarity need to change
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
            view.findViewById<ImageView>(R.id.game_card_pet_image).setImageResource(getpet.imageId)
            view.findViewById<TextView>(R.id.game_card_petId).text = String.format("%03d", getpet.id)
            view.findViewById<TextView>(R.id.game_card_petName).text = getpet.name
            view.findViewById<TextView>(R.id.game_dialog_petSkill_name).text = getpet.skillName
            view.findViewById<TextView>(R.id.game_dialog_skillDescription).text = getpet.description
            view.findViewById<TextView>(R.id.game_dialog_next_reminder).text = petCondition
            view.findViewById<TextView>(R.id.game_dialog_nextDmgAmount).text = petNextDmg
        }

        return view
    }

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DialogFragment", "SecondDialogFragment created")
        val petInfo = petInfo2()
        val getId = arguments?.getInt(ARG_PET_ID)
        val getpet = getId?.let { petInfo.getPetInfoById(it) }
    }


    companion object{
        private const val ARG_PET_ID = "pet_id"
        private const val ARG_PET_ORDER = "pet_order"
        private const val ARG_PET_STATUS = "pet_status"
        private const val ARG_PET_NEXT_DMG = "pet_next_damage"

        fun newInstance(petId : Int, petOrder : Int, petStatus: String, petNextDmg : String) : petDialog {
            val fragment = petDialog()
            val args = Bundle().apply {
                putInt(ARG_PET_ID, petId)
                putInt(ARG_PET_ORDER, petOrder)
                putString(ARG_PET_STATUS, petStatus)
                putString(ARG_PET_NEXT_DMG, petNextDmg)}
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