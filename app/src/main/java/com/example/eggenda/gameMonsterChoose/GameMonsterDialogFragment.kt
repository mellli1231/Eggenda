package com.example.eggenda.gameMonsterChoose

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.eggenda.R
import com.example.eggenda.gamePlay.dict
import com.example.eggenda.gamePlay.stageInfo
import org.w3c.dom.Text

class GameMonsterDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.game_monster_dialog, container, false)
        val stageInfo = stageInfo()
        val getID = arguments?.getInt(ARG_STAGE_ID)
        val getStage = getID?.let { stageInfo.StageInfoMap(it) }

        val rootLayout : View = view.findViewById(R.id.monster_dialog_root_layout)

        dialog?.window?.setBackgroundDrawableResource(R.color.white)

        if(getStage != null){
            view.findViewById<TextView>(R.id.monster_card_petName).text = getStage.name
            view.findViewById<TextView>(R.id.monster_card_petId).text = String.format("S%02d", getStage.id)
            view.findViewById<ImageView>(R.id.monster_card_pet_image).setImageResource(getStage.bossImageId)
            view.findViewById<TextView>(R.id.monster_dialog_skillDescription).text = getStage.description
            view.findViewById<TextView>(R.id.monster_dialog_accept_elements_description).text  = dict.ELEMENT_STRING[getStage.acceptElement]
            view.findViewById<TextView>(R.id.monster_dialog_dmg_amt).text = getStage.damageRequirement.toString() + " hp"
            view.findViewById<TextView>(R.id.monster_dialog_fight_obj).text = dict.STAGE_STRING[getStage.objectiveType]
            view.findViewById<TextView>(R.id.monster_dialog_max_turn_amt).text = getStage.maxTurn.toString() + " turns"
            view.findViewById<TextView>(R.id.monster_dialog_deck_size_amt).text = getStage.deckSize.toString() + " pets"
        }

        return view

    }


    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stageInfo = stageInfo()
        val getId = arguments?.getInt(ARG_STAGE_ID)
        val getStage = getId?.let { stageInfo.StageInfoMap(it) }
    }

    companion object{
        private const val ARG_STAGE_ID = "stage_id"

        fun newInstance(stageId : Int) : GameMonsterDialogFragment{
            val fragment = GameMonsterDialogFragment()
            val args = Bundle().apply{putInt(ARG_STAGE_ID, stageId)}
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