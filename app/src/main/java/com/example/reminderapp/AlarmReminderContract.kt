package com.example.reminderapp

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns

object AlarmReminderContract {
    const val CONTENT_AUTHORITY = "com.delaroystudios.alarmreminder"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
    const val PATH_VEHICLE = "reminder-path"
    @SuppressLint("Range")
    fun getColumnString(cursor: Cursor, columnName: String?): String {
        return cursor.getString(cursor.getColumnIndex(columnName))
    }
    object AlarmReminderEntry : BaseColumns {
        @kotlin.jvm.JvmField
        var CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE)
        const val CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE
        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE
        const val TABLE_NAME = "vehicles"
        const val _ID = BaseColumns._ID
        const val KEY_TITLE = "title"
        const val KEY_DATE = "date"
        const val KEY_TIME = "time"
        const val KEY_REPEAT = "repeat"
        const val KEY_REPEAT_NO = "repeat_no"
        const val KEY_REPEAT_TYPE = "repeat_type"
        const val KEY_ACTIVE = "active"
    }
}
