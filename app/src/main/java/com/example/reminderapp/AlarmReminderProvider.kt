package com.example.reminderapp

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

class AlarmReminderProvider : ContentProvider() {
    private var mDbHelper: AlarmReminderDbHelper? = null
    override fun onCreate(): Boolean {
        mDbHelper = AlarmReminderDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        var selection = selection
        var selectionArgs = selectionArgs
        val database = mDbHelper!!.readableDatabase

        // This cursor will hold the result of the query
        var cursor: Cursor? = null
        val match = sUriMatcher.match(uri)
        when (match) {
            REMINDER -> cursor = database.query(
                AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            REMINDER_ID -> {
                selection = AlarmReminderContract.AlarmReminderEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database.query(
                    AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            REMINDER -> AlarmReminderContract.AlarmReminderEntry.CONTENT_LIST_TYPE
            REMINDER_ID -> AlarmReminderContract.AlarmReminderEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            REMINDER -> insertReminder(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }
    private fun insertReminder(uri: Uri, values: ContentValues?): Uri? {
        val database = mDbHelper!!.writableDatabase
        val id = database.insert(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME, null, values)
        if (id == -1L) {
            Log.e(
                LOG_TAG,
                "Failed to insert row for $uri"
            )
            return null
        }
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val database = mDbHelper!!.writableDatabase
        val rowsDeleted: Int
        val match = sUriMatcher.match(uri)
        when (match) {
            REMINDER -> rowsDeleted = database.delete(
                AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
            REMINDER_ID -> {
                selection = AlarmReminderContract.AlarmReminderEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(
                    AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                    selection,
                    selectionArgs
                )
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }
    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val match = sUriMatcher.match(uri)
        return when (match) {
            REMINDER -> updateReminder(uri, contentValues, selection, selectionArgs)
            REMINDER_ID -> {
                selection = AlarmReminderContract.AlarmReminderEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updateReminder(uri, contentValues, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }
    private fun updateReminder(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (values!!.size() == 0) {
            return 0
        }
        val database = mDbHelper!!.writableDatabase
        val rowsUpdated = database.update(
            AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }
    companion object {
        val LOG_TAG = AlarmReminderProvider::class.java.simpleName
        private const val REMINDER = 100
        private const val REMINDER_ID = 101
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        init {
            sUriMatcher.addURI(
                AlarmReminderContract.CONTENT_AUTHORITY,
                AlarmReminderContract.PATH_VEHICLE,
                REMINDER
            )
            sUriMatcher.addURI(
                AlarmReminderContract.CONTENT_AUTHORITY,
                AlarmReminderContract.PATH_VEHICLE + "/#",
                REMINDER_ID
            )
        }
    }
}
