package com.example.reminderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlertReciever : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val nb: NotificationCompat.Builder = notificationHelper.channelNotification
        notificationHelper.manager?.notify(1, nb.build())
    }
}