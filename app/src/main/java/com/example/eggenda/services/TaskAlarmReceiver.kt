package com.example.eggenda.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("task_id", -1)
        val taskName = intent.getStringExtra("task_name")
        val notificationIntent = Intent(context, NotifyService::class.java).apply {
            putExtra("notification_type", "deadline")
            putExtra("task_id", taskId)
            putExtra("task_name", taskName)
        }
        context.startService(notificationIntent)
    }
}