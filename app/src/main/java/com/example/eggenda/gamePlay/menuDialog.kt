package com.example.eggenda.gamePlay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.eggenda.MainActivity
import com.example.eggenda.R

class menuDialog(viewModel:gameViewModel2) : DialogFragment() {

    private val gameViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.game_menu_dialog, container, false)

        val resumeBtn : ConstraintLayout = view.findViewById(R.id.game_dialog_resume)
        val restartBtn : ConstraintLayout = view.findViewById(R.id.game_dialog_restart)
        val quitBtn : ConstraintLayout = view.findViewById(R.id.game_dialog_quit)

        val rootLayout : View = view.findViewById(R.id.menu_root_layout)

        dialog?.window?.setBackgroundDrawableResource(R.drawable.game_menu_dialog_bg)

        //resume the game
        resumeBtn.setOnClickListener { dismiss() }

        //restart the game
        restartBtn.setOnClickListener {
            //restart the game
            gameViewModel.updateGameRunState(dict.GAME_NOT_START)
            activity?.recreate()

        }

        //quit game
        quitBtn.setOnClickListener {
            gameViewModel.updateGameRunState(dict.GAME_NOT_START)
            val intent = Intent(requireContext(), MainActivity::class.java)
            dismiss()
            startActivity(intent)
        }




        return view
    }

    companion object {

        fun newInstance(viewModel:gameViewModel2): menuDialog {

            val fragment = menuDialog(viewModel)
            val args = Bundle().apply {}
            fragment.arguments = args
            return fragment
        }

    }



    override fun onResume() {
        super.onResume()

        //set the size of the dialog
        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.menu_width),
            resources.getDimensionPixelSize(R.dimen.menu_hight),

            )
    }

}