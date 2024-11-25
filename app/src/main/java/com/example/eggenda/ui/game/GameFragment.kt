package com.example.eggenda.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.R
import com.example.eggenda.databinding.FragmentGalleryBinding
import com.example.eggenda.databinding.FragmentGameBinding
import com.example.eggenda.gameMonsterChoose.GameMonsterChooseMainActivity
import com.example.eggenda.gamePetChoose.GamePetChooseMainActivity
import com.example.eggenda.ui.gallery.GalleryViewModel

class GameFragment : Fragment(R.layout.fragment_game) {

    private var _binding: FragmentGameBinding? = null
    private lateinit var gameInfo: Button
    private lateinit var gameItem : ConstraintLayout
    private lateinit var tutorialItem : ConstraintLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val gameViewModel =
            ViewModelProvider(this).get(GameViewModel::class.java)

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        gameItem = root.findViewById(R.id.game_frag_fight_conlay)
        tutorialItem = root.findViewById(R.id.game_frag_tutorial_conlay)

        //game item goes to game page
        gameItem.setOnClickListener {
            //use intent to goto another activity
            val intent = Intent(requireContext(),GameMonsterChooseMainActivity::class.java )
            startActivity(intent)
        }

        //tutorial item goes to tutorial class
        tutorialItem.setOnClickListener {
        }


        //About this game or it does not needed



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}