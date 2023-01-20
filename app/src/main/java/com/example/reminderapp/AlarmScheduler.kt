package com.example.reminderapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build

class AlarmScheduler {
    /**
     * Schedule a reminder alarm at the specified time for the given task.
     *
     * @param context Local application or activity context
     *
     * @param reminderTask Uri referencing the task in the content provider
     */
    fun setAlarm(context: Context?, alarmTime: Long, reminderTask: Uri?) {
        val manager = AlarmManagerProvider.getAlarmManager(context!!)
        val operation: PendingIntent =
            ReminderAlarmService.getReminderPendingIntent(context, reminderTask)
        if (Build.VERSION.SDK_INT >= 23) {
            manager!!.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, operation)
        } else if (Build.VERSION.SDK_INT >= 19) {
            manager!!.setExact(AlarmManager.RTC_WAKEUP, alarmTime, operation)
        } else {
            manager!![AlarmManager.RTC_WAKEUP, alarmTime] = operation
        }
    }
    fun setRepeatAlarm(context: Context?, alarmTime: Long, reminderTask: Uri?, RepeatTime: Long) {
        val manager = AlarmManagerProvider.getAlarmManager(context!!)
        val operation: PendingIntent =
            ReminderAlarmService.getReminderPendingIntent(context, reminderTask)
        manager!!.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, RepeatTime, operation)
    }
    fun cancelAlarm(context: Context?, reminderTask: Uri?) {
        val manager = AlarmManagerProvider.getAlarmManager(context!!)
        val operation: PendingIntent =
            ReminderAlarmService.getReminderPendingIntent(context, reminderTask)
        manager!!.cancel(operation)
    }
}