package com.example.eggenda.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.R
import com.example.eggenda.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentExperience = 0
    private val maxExperience = 100


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        // Initialize egg and progress views
        binding.eggImageView.setOnClickListener {
            if (currentExperience >= maxExperience) {
                // Show cracked egg
                binding.eggImageView.setImageResource(R.drawable.egg_cracked_blue)
                binding.experienceTextView.text = "Egg has hatched!"

                // Pause for 3 seconds, then show popup
                Handler(Looper.getMainLooper()).postDelayed({
                    showCongratsPopup()
                }, 2000)

            }
        }

        binding.gainXp.setOnClickListener {
            gainExperience(20)
        }
        return root
    }

    // Function for incrementing experience progress
    private fun updateProgress() {
        binding.progressBar.progress = currentExperience
        binding.experienceTextView.text = "Experience: $currentExperience/$maxExperience"
    }

    private fun gainExperience(amount: Int) {
        if (currentExperience < maxExperience) {
            currentExperience += amount
            if (currentExperience > maxExperience) {
                currentExperience = maxExperience
            }
            updateProgress()
        }
    }

    // Show a popup dialog with a congratulatory message and pet picture
    private fun showCongratsPopup() {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_hatch, null)

        // Create the dialog builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        // Set up the dialog
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            resetEggAndExperience()
            dialog.dismiss()
        }

        // Show the dialog
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    // Reset the egg and experience progress
    private fun resetEggAndExperience() {
        currentExperience = 0
        binding.progressBar.progress = currentExperience
        binding.experienceTextView.text = "Experience: $currentExperience/$maxExperience"
        binding.eggImageView.setImageResource(R.drawable.egg_uncracked_blue_white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}