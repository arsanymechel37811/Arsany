package com.example.reminderapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.getbase.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity2 : AppCompatActivity(),androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {
    private var mAddReminderButton: FloatingActionButton? = null
    private var mToolbar: Toolbar? = null
    var mCursorAdapter: AlarmCursorAdapter? = null
    var reminderListView: ListView? = null

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        mToolbar!!.setTitle(R.string.app_name)
        reminderListView = findViewById<View>(R.id.list) as ListView
        val emptyView = findViewById<View>(R.id.empty_view)
        reminderListView!!.emptyView = emptyView
        mCursorAdapter = AlarmCursorAdapter(this, null)
        reminderListView!!.adapter = mCursorAdapter
        reminderListView!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, id ->
                val intent = Intent(this, AddReminder::class.java)
                val currentVehicleUri = ContentUris.withAppendedId(
                    AlarmReminderContract.AlarmReminderEntry.CONTENT_URI,
                    id
                )
                // Set the URI on the data field of the intent
                intent.data = currentVehicleUri
                startActivity(intent)
            }
        mAddReminderButton = findViewById<View>(R.id.fab) as FloatingActionButton
        mAddReminderButton!!.setOnClickListener { v ->
            val intent = Intent(v.context, AddReminder::class.java)
            startActivity(intent)
        }
        androidx.loader.app.LoaderManager.getInstance(this).initLoader(VEHICLE_LOADER,null,this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("my not", "my not", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    fun not(view: View?) {
        val builder = NotificationCompat.Builder(this, "my not")
        builder.setContentTitle("ReminderApp")
        builder.setContentText("Your Date is Now")
        builder.setSmallIcon(R.drawable.clock)
        builder.setAutoCancel(true)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(1, builder.build())
    }

    companion object {
        private const val VEHICLE_LOADER = 0
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        val projection = arrayOf(
            AlarmReminderContract.AlarmReminderEntry._ID,
            AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
            AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
            AlarmReminderContract.AlarmReminderEntry.KEY_TIME,
            AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT,
            AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,
            AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,
            AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE
        )
        return androidx.loader.content.CursorLoader(
            this,  // Parent activity context
            AlarmReminderContract.AlarmReminderEntry.CONTENT_URI,  // Provider content URI to query
            projection,  // Columns to include in the resulting Cursor
            null,  // No selection clause
            null,  // No selection arguments
            null
        ) // Default sort order
    }
    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor?) {
        mCursorAdapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mCursorAdapter!!.swapCursor(null)
    }
}
