package com.example.reminderapp

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.TaskStackBuilder

class ReminderAlarmService : IntentService(TAG) {
    var cursor: Cursor? = null
    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val uri = intent!!.data

        //Display a notification to view the task details
        val action = Intent(this, AddReminderActivity::class.java)
        action.data = uri
        val operation = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(action)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        //Grab the task description
        if (uri != null) {
            cursor = contentResolver.query(uri, null, null, null, null)
        }
        var description = ""
        try {
            if (cursor != null && cursor!!.moveToFirst()) {
                description = AlarmReminderContract.getColumnString(
                    cursor!!,
                    AlarmReminderContract.AlarmReminderEntry.KEY_TITLE
                )
            }
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val i = Intent(this, AlertReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, i, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent)


    }

    companion object {

        private val TAG = ReminderAlarmService::class.java.simpleName

        //This is a deep link intent, and needs the task stack
        @SuppressLint("UnspecifiedImmutableFlag")
        fun getReminderPendingIntent(context: Context?, uri: Uri?): PendingIntent {
            val action = Intent(context, ReminderAlarmService::class.java)
            action.data = uri
            return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}