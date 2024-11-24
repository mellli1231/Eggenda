package com.example.eggenda.ui.task

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConfirmTasksActivity : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private val taskAdapter by lazy { ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_tasks)

        taskListView = findViewById(R.id.task_list)
        taskListView.adapter = taskAdapter

        loadTasks()

        val addTaskImage: ImageView = findViewById(R.id.add_task)
        addTaskImage.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        val acceptButton: Button = findViewById(R.id.accept_quest)
        val declineButton: Button = findViewById(R.id.decline_quest)

        acceptButton.setOnClickListener {
            Toast.makeText(this, "Quest Accepted!", Toast.LENGTH_SHORT).show()
            finish()
        }
        declineButton.setOnClickListener {
            Toast.makeText(this, "Quest Declined!", Toast.LENGTH_SHORT).show()
            finish()
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
}
