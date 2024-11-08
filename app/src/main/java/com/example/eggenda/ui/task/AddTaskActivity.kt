package com.example.eggenda.ui.task

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.R
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Back button setup
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Go back to ConfirmTaskActivity
        }

        // Task and Details EditText setup
        val taskEditText: EditText = findViewById(R.id.edit_task)
        val detailsEditText: EditText = findViewById(R.id.edit_details)

        taskEditText.setOnClickListener {
            taskEditText.isCursorVisible = true // Show cursor for editing
        }

        detailsEditText.setOnClickListener {
            detailsEditText.isCursorVisible = true // Show cursor for editing
        }

        // Time Picker setup for Time Limit field
        val clockIcon: ImageView = findViewById(R.id.clock)
        val timeText: TextView = findViewById(R.id.edit_time_limit)
        clockIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d:00", selectedHour, selectedMinute)
                timeText.text = formattedTime
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // Add and Cancel buttons setup
        val addTaskButton: Button = findViewById(R.id.add_task_btn)
        val cancelTaskButton: Button = findViewById(R.id.cancel_task_btn)

        val backToConfirmTask = {
            finish() // Go back to ConfirmTaskActivity
        }

        addTaskButton.setOnClickListener { backToConfirmTask() }
        cancelTaskButton.setOnClickListener { backToConfirmTask() }
    }
}