package com.example.eggenda.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
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
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get type of notification to send
        val notificationType = intent?.getStringExtra("notification_type")

        when (notificationType) {
            "hatch" -> showHatchNotification()
            "deadline" -> showDeadlineNotification(intent.getStringExtra("task_name"))
        }

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

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Experience Points Alert")
            .setContentText("You have enough experience points to hatch an egg!")
            .setSmallIcon(R.drawable.app_icon)
            .build()

        notificationManager.notify(1, notification) // Unique ID for experience notifications
    }

    private fun showDeadlineNotification(taskName: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "deadline_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Deadline Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Task Deadline Alert")
            .setContentText("Your task \"$taskName\" is due in 10 minutes!")
            .setSmallIcon(R.drawable.app_icon)
            .build()

        notificationManager.notify(2, notification) // Unique ID for deadline notifications
    }



    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(myBroadcastReceiver)
        }
    }

}