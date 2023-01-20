package com.example.reminderapp

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }
    val channelNotification: NotificationCompat.Builder
        @RequiresApi(Build.VERSION_CODES.S)
        get() = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(getString(R.string.reminder_title))
            .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
            .setAutoCancel(true).setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)

    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
    }
}
