package com.example.eggenda.ui.task

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.QuestEntry
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ConfirmTasksActivity : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private lateinit var questTitleField: EditText
    private lateinit var dueDateField: TextView
    private var selectedDate: String = ""
    private var isNewQuest: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_tasks)

        isNewQuest = intent.getBooleanExtra("isNewQuest", false)
        val receivedQuestTitle = intent.getStringExtra("quest_title")
        val receivedDeadline = intent.getStringExtra("quest_deadline")

        taskListView = findViewById(R.id.task_list)
        questTitleField = findViewById(R.id.quest_title)
        dueDateField = findViewById(R.id.nq_date)

        val acceptButton: Button = findViewById(R.id.accept_quest)
        val deleteButton: Button = findViewById(R.id.delete_quest)
        val declineButton: Button = findViewById(R.id.decline_quest)

        if (isNewQuest) {
            questTitleField.setText("")
            dueDateField.text = "Select Date"
            questTitleField.isEnabled = true
            dueDateField.isEnabled = true
            clearTaskList()
        } else {
            if (!receivedQuestTitle.isNullOrEmpty()) {
                questTitleField.setText(receivedQuestTitle)
                questTitleField.isEnabled = false
            }
            if (!receivedDeadline.isNullOrEmpty()) {
                dueDateField.text = receivedDeadline
                dueDateField.isEnabled = false
            }
//            loadTasks(receivedQuestTitle)
            loadTasks()
        }

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
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        acceptButton.setOnClickListener {
            val questTitle = questTitleField.text.toString().trim()
            val deadline = if (isNewQuest) selectedDate else receivedDeadline

            if (questTitle.isEmpty() || deadline.isNullOrEmpty()) {
                Toast.makeText(this, "Quest Title/Deadline cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                // Ensure the quest exists
                if (isNewQuest) {
                    val newQuest = QuestEntry(questTitle = questTitle, dueDate = deadline)
                    EntryDatabase.getInstance(applicationContext).entryDatabaseDao.insertQuest(newQuest)
                }

                // Now insert tasks for this quest
                val task = TaskEntry(
                    title = "Sample Task", // Replace with your task title
                    questTitle = questTitle,
                    timeLimit = "00:30:00", // Replace with your time limit
                    details = "Sample Details", // Replace with task details
                    attachmentPath = "",
                    endTime = System.currentTimeMillis() + 1800000 // Example: 30 minutes from now
                )

                EntryDatabase.getInstance(applicationContext).entryDatabaseDao.insertTask(task)
            }

            runOnUiThread {
                Toast.makeText(this@ConfirmTasksActivity, if (isNewQuest) "Quest added!" else "Quest updated!", Toast.LENGTH_SHORT).show()
                finish()
            }


        }

        declineButton.setOnClickListener {
            Toast.makeText(this, if (isNewQuest) "Quest declined!" else "Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }

        deleteButton.setOnClickListener {
            val questTitle = receivedQuestTitle ?: return@setOnClickListener
            CoroutineScope(Dispatchers.IO).launch {
                EntryDatabase.getInstance(applicationContext).entryDatabaseDao.deleteQuest(questTitle)
                runOnUiThread {
                    Toast.makeText(this@ConfirmTasksActivity, "Quest deleted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun loadTasks(questTitle: String?) {
        if (questTitle.isNullOrEmpty()) {
            clearTaskList()
            return
        }

        lifecycleScope.launch {
            EntryDatabase.getInstance(applicationContext).entryDatabaseDao
                .getTasksByQuest(questTitle)
                .collectLatest { tasks ->
                    val adapter = TaskAdapter(this@ConfirmTasksActivity, tasks)
                    taskListView.adapter = adapter
                }
        }
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            EntryDatabase.getInstance(applicationContext).entryDatabaseDao.getAllTasks().collectLatest { tasks ->
                // val taskTitles = tasks.map(TaskEntry::title)
                val adapter = TaskAdapter(this@ConfirmTasksActivity, tasks)
                taskListView.adapter = adapter
            }
        }
    }

    private fun clearTaskList() {
        val emptyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emptyList())
        taskListView.adapter = emptyAdapter
    }
}
