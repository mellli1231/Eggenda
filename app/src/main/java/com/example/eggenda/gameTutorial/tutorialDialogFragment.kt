package com.example.eggenda.gameTutorial

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.eggenda.R

class tutorialDialogFragment : DialogFragment(){

    private var currentIndex = 0
    private var totalNum = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.tutorial_dialog_fragment, container, false)
        val tutorials = tutorials()
        val getId = arguments?.getInt(ARG_DIALOG_ID)
        val getDialog = getId?.let { tutorials.getTutorialById(it) }

        totalNum = getDialog!!.totalPages

        val forward_button : ImageView = view.findViewById(R.id.tut_dialog_next_button)
        val backward_button : ImageView = view.findViewById(R.id.tut_dialog_back_button)
        val tutorial_confirm_dialog_button : ConstraintLayout = view.findViewById(R.id.tutorial_confirm_dialog_button)

        val rootLayout : View = view.findViewById(R.id.tutorial_main)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.game_menu_dialog_bg)

        if(getDialog != null){
            backward_button.visibility = if (currentIndex == 0) View.INVISIBLE else View.VISIBLE
            forward_button.visibility = if (currentIndex == totalNum - 1) View.INVISIBLE else View.VISIBLE
            view.findViewById<TextView>(R.id.tut_dialogTitle).text = getDialog.coverDescription
            view.findViewById<ImageView>(R.id.tut_dialog_Image).setImageResource(getDialog.pagesImage[0])
            view.findViewById<TextView>(R.id.tut_dialog_Des).text = getDialog!!.description[0]
        }

        forward_button.setOnClickListener {
            currentIndex = (currentIndex + 1)
            backward_button.visibility = if (currentIndex == 0) View.INVISIBLE else View.VISIBLE
            forward_button.visibility = if (currentIndex == totalNum - 1) View.INVISIBLE else View.VISIBLE

            view.findViewById<ImageView>(R.id.tut_dialog_Image).setImageResource(getDialog!!.pagesImage[currentIndex])
            view.findViewById<TextView>(R.id.tut_dialog_Des).text = getDialog!!.description[currentIndex]
        }

        backward_button.setOnClickListener {
            currentIndex = (currentIndex - 1)
            backward_button.visibility = if (currentIndex == 0) View.INVISIBLE else View.VISIBLE
            forward_button.visibility = if (currentIndex == totalNum - 1) View.INVISIBLE else View.VISIBLE

            view.findViewById<ImageView>(R.id.tut_dialog_Image).setImageResource(getDialog!!.pagesImage[currentIndex])
            view.findViewById<TextView>(R.id.tut_dialog_Des).text = getDialog!!.description[currentIndex]

        }

        tutorial_confirm_dialog_button.setOnClickListener { dismiss() }



        return view


    }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tutorials = tutorials()
        val getId = arguments?.getInt(ARG_DIALOG_ID)
        val getTutorial = getId?.let { tutorials.getTutorialById(it) }
    }

    companion object{
        private const val ARG_DIALOG_ID = "dialog_id"

        fun newInstance(dialogId : Int) : tutorialDialogFragment {
            val fragment = tutorialDialogFragment()
            val args = Bundle().apply {putInt(ARG_DIALOG_ID, dialogId)}
            fragment.arguments = args
            return fragment
        }
    }

//    private fun updateContent(view : View) {
//        view.findViewById<TextView>(R.id.tut_dialogTitle).text = getDialog.coverDescription
//        view.findViewById<ImageView>(R.id.tut_dialog_Image).setImageResource(getDialog.pagesImage)
//
//    }

    override fun onResume() {
        super.onResume()

        //set the size of the dialog
        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.tutorial_width),
            resources.getDimensionPixelSize(R.dimen.tutorial_hight),

            )
    }
}