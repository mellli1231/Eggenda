package com.example.eggenda.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.eggenda.MainActivity
import com.example.eggenda.R

class NotifyService : Service() {

    private val CHANNEL_ID = "notification channel"
    private val NOTIFY_ID = 11
    private val PENDINGINTENT_REQUEST_CODE = 0
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private lateinit var notificationManager: NotificationManager

    // Notify Service has to handle both when the egg is ready to hatch, and also when a quest has
    // 10 minutes or less until deadline.

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get type of notification to send
        val notificationType = intent?.getStringExtra("notification_type")

        when (notificationType) {
            "hatch" -> showHatchNotification()
            "deadline" -> {
                val taskId = intent.getLongExtra("task_id", -1)
                val taskName = intent.getStringExtra("task_name")
                if (taskId != -1L) {
                    showDeadlineNotification(taskId, taskName)
                }
            }
        }

        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        println("debug: onDestroy called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun showHatchNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "experience_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Experience Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            // Optional: Add data to specify navigation to HomeFragment
            action = "OPEN_HOME_FRAGMENT"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Wrap the intent in a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Woah! What's this?")
            .setContentText("Your egg is ready to hatch!")
            .setSmallIcon(R.drawable.egg_uncracked_blue_white)
            .setContentIntent(pendingIntent) // Set the PendingIntent
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true) // Dismiss notification on tap
            .build()

        notificationManager.notify(1, notification) // Unique ID for experience notifications
    }

    private fun showDeadlineNotification(taskId: Long, taskName: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "deadline_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Deadline Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Use taskName.hashCode() or a unique ID for the notification ID
        val notificationId = taskName?.hashCode() ?: System.currentTimeMillis().toInt()

        // Create an Intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            // Optional: Add data to specify navigation to HomeFragment
            action = "OPEN_HOME_FRAGMENT"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId) // Pass the unique task ID
        }

        // Wrap the intent in a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            taskName.hashCode(), // Unique request code for each task
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Task Deadline Alert")
            .setContentText("Your task \"$taskName\" is due in 10 minutes or less!")
            .setSmallIcon(R.drawable.exclamation_mark)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification) // Unique IDs for each deadline
    }



    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(myBroadcastReceiver)
        }
    }

}