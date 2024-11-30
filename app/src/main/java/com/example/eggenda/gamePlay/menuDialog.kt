package com.example.eggenda.gamePlay

import android.content.Context
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

class menuDialog : DialogFragment() {

    interface MenuDialogListener {
        fun onRestartGame()
        fun onQuitGame()
    }

    private var listener: MenuDialogListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the activity implements the interface
        listener = context as? MenuDialogListener
            ?: throw RuntimeException("$context must implement MenuDialogListener")
    }

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
            listener?.onRestartGame()
            dismiss()

        }

        //quit game
        quitBtn.setOnClickListener {
            listener?.onQuitGame()
            dismiss()
        }


        return view
    }

    companion object {

        fun newInstance(): menuDialog {
            val fragment = menuDialog()
            val args = Bundle().apply {}
            fragment.arguments = args
            return fragment
        }

    }

    interface RestartDialogListener {
        fun onRestartConfirmed()
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