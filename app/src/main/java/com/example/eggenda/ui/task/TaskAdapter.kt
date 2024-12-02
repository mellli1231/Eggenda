package com.example.eggenda.ui.task

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.eggenda.R
import com.example.eggenda.services.NotifyService
import com.example.eggenda.services.TaskAlarmManager
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import com.example.eggenda.ui.database.entryDatabase.TaskEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Custom adapter for additional task properties (checkbox, time limit, etc.)
class TaskAdapter(
    context: Context,
    private val tasks: List<TaskEntry>
) : ArrayAdapter<TaskEntry>(context, R.layout.custom_list_item, tasks) {

    private val timers = mutableMapOf<Long, CountDownTimer>() // To manage timers by task ID

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.taskCheckBox)
        val taskText = view.findViewById<TextView>(R.id.taskItemText)
        val remainingTimeText = view.findViewById<TextView>(R.id.remainingTimeText)

        val task = getItem(position)
        task?.let {
            taskText.text = it.title
            checkBox.isChecked = it.isChecked // Add a property `isChecked` to TaskEntry
            applyStrikeThrough(taskText, it.isChecked)

            if (it.isChecked) {
                // Task marked as complete, show "Paused"
                remainingTimeText.text = "Completed"
                stopTimer(it.id) // Stop any active timer for this task
                TaskAlarmManager.cancelTaskAlarm(context, task.id)
            } else {
                // Task not complete, start/resume countdown
                val remainingMillis = it.endTime - System.currentTimeMillis()
                if (remainingMillis > 0) {
                    startCountDown(it.id, remainingMillis, remainingTimeText)
                } else {
                    remainingTimeText.text = "Expired"
                }
                if (remainingMillis > 10 * 60 * 1000) {
                    TaskAlarmManager.scheduleTaskAlarm(context, it.id, it.title, it.endTime)
                }
            }

            // listener for the checkbox, if checked: cross out and pause time
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                it.isChecked = isChecked
                if (isChecked && !it.timerStarted) {
                    // Mark task as complete and pause timer
                    stopTimer(it.id)
                    it.remainingTime = it.endTime - System.currentTimeMillis() // Save remaining time
                    remainingTimeText.text = "Paused"
                    TaskAlarmManager.cancelTaskAlarm(context, it.id)
                } else {
                    // Mark task as incomplete and resume countdown
                    val remainingMillis = it.remainingTime
                    if (remainingMillis > 0) {
                        it.endTime = System.currentTimeMillis() + remainingMillis // Adjust endTime
                        startCountDown(it.id, remainingMillis, remainingTimeText)
                        TaskAlarmManager.scheduleTaskAlarm(context, it.id, it.title, it.endTime)
                    }
                }

                // Update the task in the database
                CoroutineScope(Dispatchers.IO).launch {
                    EntryDatabase.getInstance(context).entryDatabaseDao.updateTask(it)
                }
                applyStrikeThrough(taskText, it.isChecked)
            }
        }
        return view
    }

    // Helper method to apply or remove the strike-through effect
    private fun applyStrikeThrough(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            Log.d("TaskAdapter", "Strike-through applied")
            textView.paintFlags = textView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(context.getColor(R.color.strike_through_color))
        } else {
            Log.d("TaskAdapter", "Strike-through applied")
            textView.paintFlags = textView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(context.getColor(R.color.normal_text_color))
        }
        textView.invalidate() // force ui refresh
    }

    private fun startCountDown(taskId: Long, remainingMillis: Long, textView: TextView) {
        stopTimer(taskId) // Stop any existing timer for this task

        val timer = object : CountDownTimer(remainingMillis, 1000) {
            var hasNotified = false

            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / (1000 * 60 * 60))
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                textView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                // Check if 10 minutes remain and send notification
                if (millisUntilFinished <= 10 * 60 * 1000 && !hasNotified) {
                    hasNotified = true // Ensure notification is sent only once
                    triggerDeadlineNotification(taskId)
                }
            }

            override fun onFinish() {
                textView.text = "Expired"
            }
        }

        timers[taskId] = timer // Save the timer reference
        timer.start()
    }

    private fun stopTimer(taskId: Long) {
        timers[taskId]?.cancel() // Cancel the timer if it exists
        timers.remove(taskId) // Remove it from the map
    }

    private fun triggerDeadlineNotification(taskId: Long) {
        val task = tasks.find { it.id == taskId } // Find the task by its ID
        task?.let {
            val intent = Intent(context, NotifyService::class.java).apply {
                putExtra("notification_type", "deadline")
                putExtra("task_name", it.title) // Pass the task name
            }
            context.startService(intent) // Start the service to send the notification
        }
    }
}