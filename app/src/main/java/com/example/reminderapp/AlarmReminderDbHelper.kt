package com.example.reminderapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlarmReminderDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        // Create a String that contains the SQL statement to create the reminder table
        val SQL_CREATE_ALARM_TABLE =
            ("CREATE TABLE " + AlarmReminderContract.AlarmReminderEntry.TABLE_NAME + " ("
                    + AlarmReminderContract.AlarmReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_TITLE + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_DATE + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_TIME + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE + " TEXT NOT NULL, "
                    + AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE + " TEXT NOT NULL " + " );")

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE)
    }
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}

    companion object {
        private const val DATABASE_NAME = "alarmreminder.db"
        private const val DATABASE_VERSION = 1
    }
}
