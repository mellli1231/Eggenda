package com.example.eggenda.ui.task

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.R
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitle: EditText
    private lateinit var taskDetails: EditText
    private lateinit var timeTextView: TextView
    private var timeLimit: String = ""

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskTitle = findViewById(R.id.edit_task)
        taskDetails = findViewById(R.id.edit_details)
        timeTextView = findViewById(R.id.edit_time_limit)

        val clockIcon: ImageView = findViewById(R.id.clock)
        val addTaskButton: Button = findViewById(R.id.add_task_btn)
        val cancelButton: Button = findViewById(R.id.cancel_task_btn)

        // Set up time picker
        clockIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                timeLimit = String.format("%02d:%02d:00", selectedHour, selectedMinute)
                timeTextView.text = timeLimit
            }, hour, minute, true).show()
        }

        // Save task
        addTaskButton.setOnClickListener {
            val title = taskTitle.text.toString().trim()
            val details = taskDetails.text.toString().trim()

            if (title.isEmpty() || details.isEmpty() || timeLimit.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate endTime
            val now = Calendar.getInstance()
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, timeLimit.split(":")[0].toInt())
            selectedTime.set(Calendar.MINUTE, timeLimit.split(":")[1].toInt())
            selectedTime.set(Calendar.SECOND, 0)

            // Adjust for the next day if the selected time is earlier than the current time
            if (selectedTime.before(now)) {
                selectedTime.add(Calendar.DAY_OF_YEAR, 1)
            }
            val endTime = selectedTime.timeInMillis

            // Add task to database
            val taskEntry = TaskEntry(title = title, timeLimit = timeLimit, details = details, endTime = endTime)
            CoroutineScope(Dispatchers.IO).launch {
                EntryDatabase.getInstance(applicationContext).entryDatabaseDao.insertTask(taskEntry)
            }

            Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Cancel and go back
        cancelButton.setOnClickListener { finish() }
    }
}
