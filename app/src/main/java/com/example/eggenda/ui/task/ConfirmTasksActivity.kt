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
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        isNewQuest = intent.getBooleanExtra("isNewQuest", false)
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
//            Toast.makeText(this, "New Quest: $isNewQuest", Toast.LENGTH_LONG).show()
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
            loadTasks()
            loadProgress()
//            loadTasks(receivedQuestTitle)
//            Toast.makeText(this, "Quest: $receivedQuestTitle", Toast.LENGTH_LONG).show()

            // Update button labels for existing quests
            acceptButton.text = "Confirm"
            declineButton.text = "Cancel"
            deleteButton.visibility = View.VISIBLE // Show delete button
//            Toast.makeText(this, "New Quest(F): $isNewQuest", Toast.LENGTH_LONG).show()
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

        val addTaskImage: ImageView = findViewById(R.id.add_task)
        addTaskImage.setOnClickListener {
            val questTitle = questTitleField.text.toString().trim()
            if (questTitle.isEmpty()) {
                Toast.makeText(this, "Please specify a quest title", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("quest_title", questTitle)
                startActivity(intent)
            }
        }


        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        acceptButton.setOnClickListener {
            val questTitle = questTitleField.text.toString().trim()
            val dueDate = if (isNewQuest) selectedDate else receivedDeadline

            if (isNewQuest) {
                // Save new quest
                val newQuest = dueDate?.let { it1 -> TaskEntry(questTitle = questTitle, dueDate = it1) }
                CoroutineScope(Dispatchers.IO).launch {
                    if (newQuest != null) {
                        EntryDatabase.getInstance(applicationContext).entryDatabaseDao.insertTask(newQuest)
                    }
                }

                if (dueDate != null) {
                    if (questTitle.isEmpty() || dueDate.isEmpty()) {
                        Toast.makeText(this, "Quest Title/Deadline cannot be empty!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            } else {
                // Update existing quest tasks
                CoroutineScope(Dispatchers.IO).launch {
                    val tasks = EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getTasksByQuest(questTitle)
                    tasks.forEach { it.timerStarted = true }
                    EntryDatabase.getInstance(applicationContext).entryDatabaseDao.updateTasks(tasks)
                }

                lifecycleScope.launch {
                    val allTasksChecked = withContext(Dispatchers.IO) {
                        val tasks = EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getTasksByQuest(questTitle)
                        tasks.isNotEmpty() && tasks.all { it.isChecked }
                    }

                    if (allTasksChecked) {
                        // Mark quest as completed and update experience points
                        val expPoints = 50 // Example: 50 experience points per quest
                        withContext(Dispatchers.IO) {
                            // Delete the quest and its tasks
                            EntryDatabase.getInstance(applicationContext).entryDatabaseDao.deleteQuestAndTasks(questTitle)

                            // Update shared preferences
                            val sharedPreferences = getSharedPreferences("eggenda_prefs", MODE_PRIVATE)
                            val currentExperience = sharedPreferences.getInt("currentExperience", 0)
                            val newExperience = currentExperience + expPoints
                            sharedPreferences.edit().putInt("currentExperience", newExperience).apply()

                            // Broadcast the experience update
                            val intent = Intent("com.example.eggenda.EXPERIENCE_UPDATE")
                            intent.putExtra("new_experience", newExperience)
                            sendBroadcast(intent)
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ConfirmTasksActivity, "Quest Completed! + $expPoints exp.", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        val questTitle = questTitleField.text.toString().trim()
        if (questTitle.isNotEmpty()) {
            loadTasks(questTitle)
        }
    }

    private fun loadTasks() {   // Load all tasks
        lifecycleScope.launch {
            EntryDatabase.getInstance(applicationContext).entryDatabaseDao
                .getAllTasks().collectLatest { tasks ->
                val adapter = TaskAdapter(this@ConfirmTasksActivity, tasks)
                taskListView.adapter = adapter
            }
        }
    }

    private fun loadTasks(questTitle: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (questTitle != null) {
                val tasks = EntryDatabase.getInstance(applicationContext)
                    .entryDatabaseDao.getTasksByQuestFlow(questTitle)
                    .collectLatest { taskList ->
                    withContext(Dispatchers.Main) {
                        val adapter = TaskAdapter(this@ConfirmTasksActivity, taskList)
                        taskListView.adapter = adapter
                    }
                }
            }
        }
    }


    private fun clearTaskList() {
        val emptyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emptyList())
        taskListView.adapter = emptyAdapter
    }

    private fun loadProgress() {
        val sharedPreferences = this.getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)
        currentExperience = sharedPreferences.getInt("currentExperience", 0) // Default to 0 if not found
        // TODO: make this shared preference the same as whatever the tasks updates
    }
}
