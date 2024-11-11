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
import org.w3c.dom.Text

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
        val view =  inflater.inflate(R.layout.game_pet_choose_dialog, container, false)
        val petInfo = petInfo2()
        val getId = arguments?.getInt(ARG_PET_ID)
        val getpet = getId?.let { petInfo.getPetInfoById(it) }

        val element_background = dict.ELEMENT_STRING[getpet?.element!!]
        val rootLayout : View = view.findViewById(R.id.dialog_root_layout)

        when(element_background){
            "Fire" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_fire)
            "Water" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_water)
            "Forest" -> dialog?.window?.setBackgroundDrawableResource(R.drawable.game_dialog_forest)
//            "Water" -> rootLayout.setBackgroundResource(R.drawable.game_element_frame_water)
//            "Forest" -> rootLayout.setBackgroundResource(R.drawable.game_element_frame_forest)
        }

        //set the data on views
        if (getpet != null) {
            view.findViewById<ImageView>(R.id.choose_petImage).setImageResource(getpet.imageId)
            view.findViewById<TextView>(R.id.choose_petId).text = getpet.id.toString()
            view.findViewById<TextView>(R.id.choose_petName).text = getpet.name
            view.findViewById<TextView>(R.id.choose_skillCondition).text = getpet.description
            view.findViewById<TextView>(R.id.choose_attackType).text = dict.ATK_STRING[getpet.attackType]
            view.findViewById<TextView>(R.id.choose_count_num).text = getpet.count.toString()
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