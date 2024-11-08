package com.example.eggenda.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.example.eggenda.R
import com.example.eggenda.databinding.DialogHatchBinding
import com.example.eggenda.databinding.FragmentHomeBinding
import com.example.eggenda.gamePlay.gameActivity
import com.example.eggenda.ui.task.ConfirmTasksActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var mediaPlayer: MediaPlayer? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentExperience = 0
    private val maxExperience = 100

    private val PET_OWNERSHIP_KEY = "pet_ownership"
    private val DEFAULT_PET_OWNERSHIP = arrayOf(0, 0, 0, 0, 0) // 5 pets, all initially unowned


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val petOwnership = loadPetOwnership()

        // xp
        loadProgress()
        updateProgress()

        // Initialize egg and progress views
        binding.eggImageView.setOnClickListener {
            if (ownsAll()) {
                Toast.makeText(requireContext(), "You own all pets already!", Toast.LENGTH_SHORT).show()
                // Perform subtle haptic feedback if egg is not ready to hatch
                binding.eggImageView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            } else if (currentExperience >= maxExperience) {
                // Show cracked egg
                binding.eggImageView.setImageResource(R.drawable.egg_cracked_blue)
                binding.experienceTextView.text = "Egg has hatched!"

                // save the array of pets
                savePetOwnership(petOwnership)

                playSound(R.raw.sound_success)
                Handler(Looper.getMainLooper()).postDelayed({
                    resetEggAndExperience()
                    hatchEgg(petOwnership)
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

        val newQuestButton: Button = root.findViewById(R.id.new_quest)
        newQuestButton.setOnClickListener {
            val intent = Intent(requireContext(), ConfirmTasksActivity::class.java)
            startActivity(intent)
        }

        val gotoGameButton: Button = root.findViewById(R.id.game)
        gotoGameButton.setOnClickListener {
            val intent = Intent(requireContext(), gameActivity::class.java)
            startActivity(intent)
        }
        binding.goGame.setOnClickListener{
            val intent = Intent(requireContext(), gameActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    private fun loadPetOwnership(): Array<Int> {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(PET_OWNERSHIP_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<Array<Int>>() {}.type
            Gson().fromJson(json, type)
        } else {
            // Initialize with all pets unowned and save it to SharedPreferences
            savePetOwnership(DEFAULT_PET_OWNERSHIP)
            DEFAULT_PET_OWNERSHIP.clone() // Return a clone to avoid modifying the original
        }
    }

    private fun savePetOwnership(petOwnership: Array<Int>) {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(petOwnership)
        editor.putString(PET_OWNERSHIP_KEY, json)
        editor.apply()
    }

    private fun hatchEgg(petOwnership: Array<Int>) {
        val unownedPets = petOwnership.indices.filter { petOwnership[it] == 0 }
        if (unownedPets.isNotEmpty()) {
            // Choose a random unowned pet and mark it as owned
            val randomPet = unownedPets.random()
            petOwnership[randomPet] = 1

            // Save pets, and also provide popup with specific pet's photo
            savePetOwnership(petOwnership)
            showCongratsPopup(randomPet)
        }
    }

    // Function to check if user owns all pets
    private fun ownsAll(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("pet_ownership", null)

        if (json != null) {
            val type = object : TypeToken<Array<Int>>() {}.type
            val petOwnership: Array<Int> = Gson().fromJson(json, type)

            // Check if every pet in the array is owned (i.e., every value is 1)
            return petOwnership.all { it == 1 }
        }

        // If no ownership data exists, user does not own all pets
        return false
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
    @SuppressLint("SetTextI18n")
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
    @SuppressLint("SetTextI18n")
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
    private fun showCongratsPopup(petIndex: Int) {
        // Inflate the dialog layout
        val dialogBinding = DialogHatchBinding.inflate(LayoutInflater.from(requireContext()))

        // Set the pet image based on petIndex
        when (petIndex) {
            0 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_chubby_bunny_large)
            1 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_evil_water_large)
            2 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_flaming_skull_large)
            3 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_little_mothman_large)
            4 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_shy_raccoon_large)
        }

        // Create and display the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogBinding.root)
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            resetEggAndExperience() // Reset egg and experience after user acknowledges the dialog
            dialog.dismiss()
        }
        dialogBuilder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}