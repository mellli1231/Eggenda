package com.example.eggenda.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.databinding.DialogHatchBinding
import com.example.eggenda.databinding.FragmentHomeBinding
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.gameActivity
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.EntryDatabaseDao
import com.example.eggenda.ui.database.entryDatabase.EntryRepo
import com.example.eggenda.ui.database.entryDatabase.EntryViewModel
import com.example.eggenda.ui.database.entryDatabase.EntryViewModelFactory
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.example.eggenda.ui.database.userDatabase.UserViewModel
import com.example.eggenda.ui.database.userDatabase.UserViewModelFactory
import com.example.eggenda.ui.task.ConfirmTasksActivity
import com.example.eggenda.ui.task.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentExperience = 0
    private val maxExperience = 100

    private val PET_OWNERSHIP_KEY = "pet_ownership"
    private val DEFAULT_PET_OWNERSHIP = arrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0, 0)

    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDao
    private lateinit var repo: EntryRepo
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    private lateinit var udatabase: UserDatabase
    private lateinit var udatabaseDao: UserDatabaseDao
    private lateinit var userViewModel: UserViewModel
    private lateinit var repository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var FBdatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var id: String=""


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferenceManager = SharedPreferenceManager(requireContext())
        val root: View = binding.root
        val petOwnership = loadPetOwnership()
//        sharedPreferenceManager = SharedPreferenceManager(requireContext())klh

        //get username and id
        val user = UserPref.getUsername(requireContext())
        id = UserPref.getId(requireContext()).toString()
        println("user: ${user}, id: ${id}")

        //load database to retrieve gained points
        FBdatabase = FirebaseDatabase.getInstance()
        myRef = FBdatabase.reference.child("users")
        udatabase = UserDatabase.getInstance(requireContext())
        udatabaseDao = udatabase.userDatabaseDao
        repository = UserRepository(udatabaseDao)
        userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


        //load profile picture
        val sharedPreferences = requireContext().getSharedPreferences("user_${id}", Context.MODE_PRIVATE)
        binding.displayName.text=user //set username
        val profileImgPath = sharedPreferences.getString("profileImagePath", null)
        if (profileImgPath != null) { //if profile pic exists, set
            val ogFile = File(profileImgPath)
            if (ogFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(ogFile.absolutePath)
                binding.profilePic.setImageBitmap(bitmap)
            } else {
                binding.profilePic.setImageResource(R.drawable.defaultprofile)
            }
        } else {
            binding.profilePic.setImageResource(R.drawable.defaultprofile)
        }

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
                sharedPreferenceManager.savePetOwnership(petOwnership)

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

        val questListView: ListView = binding.questList

        // Load tasks into quest board ListView
        lifecycleScope.launch {
            EntryDatabase.getInstance(requireContext()).entryDatabaseDao.getAllTasks().collectLatest { tasks ->
                // val taskTitles = tasks.map { it.title }
                val adapter = TaskAdapter(requireContext(), tasks)
                questListView.adapter = adapter
            }
        }

        return root
    }

    private fun loadPetOwnership(): Array<Int> {
        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        //orig
//        val json = sharedPreferences.getString(PET_OWNERSHIP_KEY, null)
        val json = requireContext().getSharedPreferences("user_${UserPref.getId(requireContext())}", Context.MODE_PRIVATE).getString(PET_OWNERSHIP_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<Array<Int>>() {}.type
            Gson().fromJson(json, type)
        } else {
            // Initialize with all pets unowned and save it to SharedPreferences
            sharedPreferenceManager.savePetOwnership(DEFAULT_PET_OWNERSHIP)
            DEFAULT_PET_OWNERSHIP.clone() // Return a clone to avoid modifying the original
        }
    }


//    private fun savePetOwnership(petOwnership: Array<Int>) {
//        val sharedPreferences = requireContext().getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        val json = Gson().toJson(petOwnership)
//        editor.putString(PET_OWNERSHIP_KEY, json)
//        editor.apply()
//    }

    private fun hatchEgg(petOwnership: Array<Int>) {
        val unownedPets = petOwnership.indices.filter { petOwnership[it] == 0 }
        if (unownedPets.isNotEmpty()) {
            // Choose a random unowned pet and mark it as owned
            val randomPet = unownedPets.random()
            petOwnership[randomPet] = 1

            // Save pets, and also provide popup with specific pet's photo
            sharedPreferenceManager.savePetOwnership(petOwnership)
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

        CoroutineScope(Dispatchers.IO).launch {
            repository.updatePoints(id, amount) //update room database

            //update firebase database
            myRef.child(id).child("points").runTransaction(object: Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val curr = mutableData.getValue(Int::class.java)
                    if (curr != null) {
                        mutableData.value = curr + amount
                    }
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if(databaseError != null) {
                        println("Error updating points: ${databaseError.message}")
                    } else {
                        println("Points updated in firebase")
                    }
                }
            })
        }
        //adding to database just to test, temporary as well






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
            0 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_a_babyowlbear)
            1 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_b_ambushmouseviper)
            2 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_b_evilwater)
            3 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_animatednutcracker)
            4 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_deepseamerman)
            5 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_flamingskull)
            6 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_glutinousbunny)
            7 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_healingsprite)
            8 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_lilmothy)
            9 -> dialogBinding.petImageView.setImageResource(R.drawable.pet_c_shyraccoon)
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