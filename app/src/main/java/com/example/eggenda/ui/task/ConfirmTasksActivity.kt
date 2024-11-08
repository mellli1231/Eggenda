package com.example.eggenda.ui.task

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.R
import java.util.Calendar

class ConfirmTasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_tasks)

        // Back button setup
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Go back to the previous activity (HomeFragment)
        }

        // Date Picker setup
        val calendarIcon: ImageView = findViewById(R.id.nq_calendar)
        val dateText: TextView = findViewById(R.id.nq_date)

        calendarIcon.setOnClickListener {
            // Show DatePickerDialog
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Update dateText with selected date in Day/Month/Year format
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateText.text = formattedDate
            }, year, month, day)

            datePickerDialog.show()
        }

        // Add task button
        val addTaskImage: ImageView = findViewById(R.id.add_task)
        addTaskImage.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Accept and Decline buttons setup
        val acceptButton: Button = findViewById(R.id.accept_quest)
        val declineButton: Button = findViewById(R.id.decline_quest)

        val backToHome = {
            finish() // Go back to the previous activity (HomeFragment)
        }

        acceptButton.setOnClickListener {
            val toast = Toast.makeText(this, "Quest Accepted!", Toast.LENGTH_SHORT)
            toast.show()
            backToHome()
        }
        declineButton.setOnClickListener {
            val toast = Toast.makeText(this, "Quest Declined!", Toast.LENGTH_SHORT)
            toast.show()
            backToHome()
        }
    }
}
