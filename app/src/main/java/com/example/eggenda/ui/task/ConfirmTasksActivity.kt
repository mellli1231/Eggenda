package com.example.eggenda.ui.task

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.databinding.FragmentHomeBinding
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.services.NotifyService
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ConfirmTasksActivity : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private val taskAdapter by lazy { ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) }
    private lateinit var questTitleField: EditText
    private lateinit var dueDateField: TextView
    private var selectedDate: String = ""
    private var id: String=""

    private var currentExperience = 0
    private val maxExperience = 100
    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var udatabase: UserDatabase
    private lateinit var udatabaseDao: UserDatabaseDao
    private lateinit var repository: UserRepository
    private lateinit var FBdatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var isNewQuest: Boolean = true

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_tasks)
        sharedPreferenceManager = SharedPreferenceManager(this)
        id = UserPref.getId(this).toString()
        FBdatabase = FirebaseDatabase.getInstance()
        myRef = FBdatabase.reference.child("users")
        udatabase = UserDatabase.getInstance(this)
        udatabaseDao = udatabase.userDatabaseDao
        repository = UserRepository(udatabaseDao)

        isNewQuest = intent.getBooleanExtra("isNewQuest", true)
        val receivedQuestTitle = intent.getStringExtra("quest_title")
        val receivedDeadline = intent.getStringExtra("quest_deadline")
        taskListView = findViewById(R.id.task_list)
        taskListView.adapter = taskAdapter

        questTitleField = findViewById(R.id.quest_title)
        dueDateField = findViewById(R.id.nq_date)

        val acceptButton: Button = findViewById(R.id.accept_quest)
        val declineButton: Button = findViewById(R.id.decline_quest)
        val deleteButton: Button = findViewById(R.id.delete_quest)

        // Check if the activity was opened from the quest board
        if (isNewQuest) {
            // Clear fields for a new quest
            questTitleField.setText("")
            dueDateField.text = "Select Date"
            questTitleField.isEnabled = true // Editable for new quests
            dueDateField.isEnabled = true // Editable for new quests
            clearTaskList() // Reset task list for new quests
        } else {
            // Populate fields for an existing quest
            if (!receivedQuestTitle.isNullOrEmpty()) {
                questTitleField.setText(receivedQuestTitle)
                questTitleField.isEnabled = false // Non-editable for existing quests
            }
            if (!receivedDeadline.isNullOrEmpty()) {
                dueDateField.text = receivedDeadline
                dueDateField.isEnabled = false // Non-editable deadline
            }

            // Update button labels for existing quests
            acceptButton.text = "Confirm"
            declineButton.text = "Cancel"
            deleteButton.visibility = View.VISIBLE // Show delete button
        }

        // Date Picker for Due Date
        findViewById<ImageView>(R.id.nq_calendar).setOnClickListener {
            if (dueDateField.isEnabled) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dueDateField.text = selectedDate
                }, year, month, day).show()
            }
        }

//        loadTasks(receivedQuestTitle)
        loadTasks()
        loadProgress()

        val addTaskImage: ImageView = findViewById(R.id.add_task)
        addTaskImage.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        acceptButton.setOnClickListener {
            val questTitle = questTitleField.text.toString().trim()
            val dueDate = selectedDate

            if (isNewQuest) {
                // Save new quest
                val newQuest = TaskEntry(questTitle = questTitle, dueDate = dueDate)
                CoroutineScope(Dispatchers.IO).launch {
                    EntryDatabase.getInstance(applicationContext).entryDatabaseDao.insertTask(newQuest)
                }

                if (questTitle.isEmpty() || dueDate.isEmpty()) {
                    Toast.makeText(this, "Quest Title/Deadline cannot be empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                // Update existing quest tasks
                CoroutineScope(Dispatchers.IO).launch {
                    val tasks = EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getTasksByQuest(questTitle)
                    tasks.forEach { it.timerStarted = true }
                    EntryDatabase.getInstance(applicationContext).entryDatabaseDao.updateTasks(tasks)
                }

                lifecycleScope.launch {
                    val questTitle = questTitleField.text.toString().trim()

                    if (questTitle.isEmpty()) {
                        Toast.makeText(this@ConfirmTasksActivity, "Quest Title cannot be empty!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val allTasksChecked = withContext(Dispatchers.IO) {
                        val tasks = EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getTasksByQuestList(questTitle)
                        tasks.isNotEmpty() && tasks.all { it.isChecked }
                    }

                    if (allTasksChecked) {
                        // Mark quest as completed
                        withContext(Dispatchers.IO) {
                            EntryDatabase.getInstance(applicationContext).entryDatabaseDao.deleteQuestAndTasks(questTitle)
                            EntryDatabase.getInstance(applicationContext).entryDatabaseDao.deleteAllTasksForQuest(questTitle)
                        }
                        withContext(Dispatchers.Main) {
                            gainExperience(27)
                            Toast.makeText(this@ConfirmTasksActivity, "Quest Completed!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }


            Toast.makeText(this, if (isNewQuest) "Quest added!" else "Quest updated!", Toast.LENGTH_SHORT).show()
            finish()
        }

        declineButton.setOnClickListener {
            Toast.makeText(this, if (isNewQuest) "Quest declined!" else "Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Delete Button Logic
        deleteButton.setOnClickListener {
            if (!receivedQuestTitle.isNullOrEmpty()) {
                // Confirm deletion with a dialog
                AlertDialog.Builder(this)
                    .setTitle("Delete Quest")
                    .setMessage("Are you sure you want to delete this quest?")
                    .setPositiveButton("Yes") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            // Delete the quest and its associated tasks
                            EntryDatabase.getInstance(applicationContext)
                                .entryDatabaseDao.deleteQuestAndTasks(receivedQuestTitle)
                        }

                        runOnUiThread {
                            Toast.makeText(this, "Quest deleted successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    private fun loadTasks() {
        lifecycleScope.launch {

            EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getAllTasks().collectLatest { tasks ->
                val adapter = TaskAdapter(this@ConfirmTasksActivity, tasks)
                taskListView.adapter = adapter
            }

        }
    }

    private fun loadTasks(questTitle: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val tasks = if (!questTitle.isNullOrEmpty()) {
                EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getTasksByQuest(questTitle)
            } else {
                emptyList()
            }

            withContext(Dispatchers.Main) {
                val adapter = TaskAdapter(this@ConfirmTasksActivity, tasks)
                taskListView.adapter = adapter
            }
        }
    }


    private fun clearTaskList() {
        val emptyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emptyList())
        taskListView.adapter = emptyAdapter
    }

    // Function for incrementing experience progress
//    @SuppressLint("SetTextI18n")
//    private fun updateProgress() {
//        binding.progressBar.progress = currentExperience
//        binding.circularProgress.progress = currentExperience
//        binding.experienceTextView.text = "Experience: $currentExperience/$maxExperience"
//    }

    private fun loadProgress() {
        val sharedPreferences = this.getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        currentExperience = sharedPreferences.getInt("currentExperience", 0) // Default to 0 if not found
        // TODO: make this shared preference the same as whatever the tasks updates
    }

    private fun saveExperienceProgress() {
        val sharedPreferences = this.getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("currentExperience", currentExperience).apply()
    }

    private fun gainExperience(amount: Int) {
        if (currentExperience < maxExperience) {
            currentExperience += amount
            if (currentExperience > maxExperience) {
                currentExperience = maxExperience
            }
            saveExperienceProgress()
//            updateProgress()

//            if (currentExperience == maxExperience) {
//                startHatchNotificationService()
//            }
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
    }

    // Helper function to start notification for ready-to-hatch egg
//    private fun startHatchNotificationService() {
//        val intent = Intent(this, NotifyService::class.java)
//        intent.putExtra("notification_type", "hatch")
//        this.startService(intent)
//    }
}
