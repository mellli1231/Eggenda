package com.example.eggenda.ui.home

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
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
    private var mediaPlayer: MediaPlayer? = null
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

        // Never used
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        // xp
        loadProgress()
        updateProgress()

        // Initialize egg and progress views
        binding.eggImageView.setOnClickListener {
            if (currentExperience >= maxExperience) {
                // Show cracked egg
                binding.eggImageView.setImageResource(R.drawable.egg_cracked_blue)
                binding.experienceTextView.text = "Egg has hatched!"

                getNewPet()
                playSound(R.raw.sound_success)
                Handler(Looper.getMainLooper()).postDelayed({
                    resetEggAndExperience()
                    showCongratsPopup()
                }, 1500)
                triggerVibration(1500)

            } else {
                // Perform subtle haptic feedback if egg is not ready to hatch
                binding.eggImageView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        }

        // Button to test what happens when Xp is gained
        binding.gainXp.setOnClickListener {
            gainExperience(20)
        }
        return root
    }

    private fun getNewPet() {
        // TODO: implement
        // User gets a pet that they do not own, the shared preference should have all pets
        // one field in each pet should determine if user owns or not
        // Update the shared preference ownership field for a random unowned pet.
    }

    private fun triggerVibration(time: Long) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // For Android O and above
                it.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For older versions
                it.vibrate(time) // Vibrate for 200 milliseconds
            }
        }
    }

    private fun playSound(soundResId: Int) {
        // Release any existing MediaPlayer instance
        mediaPlayer?.release()

        // Initialize and play the new sound
        mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.start()

        // Release the media player when the sound finishes playing
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    private fun loadProgress() {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        currentExperience = sharedPreferences.getInt("currentExperience", 0) // Default to 0 if not found
        // TODO: make this shared preference the same as whatever the tasks updates
    }

    // Function for incrementing experience progress
    private fun updateProgress() {
        binding.progressBar.progress = currentExperience
        binding.circularProgress.progress = currentExperience
        binding.experienceTextView.text = "Experience: $currentExperience/$maxExperience"
    }

    // Eventually, we won't need this function because user experience increase based on tasks done
    private fun gainExperience(amount: Int) {
        if (currentExperience < maxExperience) {
            currentExperience += amount
            if (currentExperience > maxExperience) {
                currentExperience = maxExperience
            }
            saveExperienceProgress()
            updateProgress()
        }
    }

    // Eventually, we won't need this either
    private fun saveExperienceProgress() {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("currentExperience", currentExperience).apply()
    }

    // Reset the egg and experience progress
    // TODO: properly implement xp bar with shared preference xp/levels
    // also, maybe try to implement random egg colors/pictures
    private fun resetEggAndExperience() {
        currentExperience = 0
        saveExperienceProgress()
        binding.progressBar.progress = currentExperience
        binding.circularProgress.progress = currentExperience
        binding.experienceTextView.text = "Experience: $currentExperience/$maxExperience"
        binding.eggImageView.setImageResource(R.drawable.egg_uncracked_blue_white)
    }

    // Show a popup dialog with a congratulatory message and pet picture
    // TODO: get the randomly chosen pet, and get its image placed in the popup
    private fun showCongratsPopup() {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_hatch, null)

        // Create the dialog builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        // Set up the dialog
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}