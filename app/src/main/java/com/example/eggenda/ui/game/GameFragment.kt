package com.example.eggenda.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    private lateinit var gameStartButton: Button

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

//        val textView: TextView = binding.textGame
//        gameViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        //set the game button to the choose character acitivity
//        val gameStartButton: Button = view?.findViewById(R.id.game_start_frag_btn) ?:
        binding.gameStartFragBtn.setOnClickListener {
            //use intent to goto another activity
            val intent = Intent(requireContext(),GameMonsterChooseMainActivity::class.java )
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}