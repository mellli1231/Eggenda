package com.example.eggenda.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.eggenda.ui.database.entryDatabase.EntryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = EntryDatabase.getInstance(context)
            CoroutineScope(Dispatchers.IO).launch {
                val tasksFlow = database.entryDatabaseDao.getAllTasks() // Return Flow<List<TaskEntry>>

                CoroutineScope(Dispatchers.IO).launch {
                    tasksFlow.collect { tasks ->
                        for (task in tasks) {
                            if (!task.isChecked) {
                                TaskAlarmManager.scheduleTaskAlarm(context, task.id, task.title, task.endTime)
                            }
                        }
                    }
                }
            }
        }
    }
}