package com.example.reminderapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import com.getbase.floatingactionbutton.FloatingActionButton
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class AddReminder : AppCompatActivity(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener,
    androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {
    private var mToolbar: Toolbar? = null
    private var mTitleText: EditText? = null
    private var mDateText: TextView? = null
    private var mTimeText: TextView? = null
    private var mRepeatText: TextView? = null
    private var mRepeatNoText: TextView? = null
    private var mRepeatTypeText: TextView? = null
    private var mFAB1: FloatingActionButton? = null
    private var mFAB2: FloatingActionButton? = null
    private var mCalendar: Calendar? = null
    private var mYear = 0
    private var mMonth = 0
    private var mHour = 0
    private var mMinute = 0
    private var mDay = 0
    private var mRepeatTime: Long = 0
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var mRepeatSwitch: Switch? = null
    private var mTitle: String? = null
    private var mTime: String? = null
    private var mDate: String? = null
    private var mRepeat: String? = null
    private var mRepeatNo: String? = null
    private var mRepeatType: String? = null
    private var mActive: String? = null
    private var mCurrentReminderUri: Uri? = null
    private var mVehicleHasChanged = false

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder2)
        val intent = intent
        mCurrentReminderUri = intent.data
        if (mCurrentReminderUri == null) {
            title = getString(R.string.editor_activity_title_new_reminder)

            invalidateOptionsMenu()
        } else {
            title = getString(R.string.editor_activity_title_edit_reminder)
            androidx.loader.app.LoaderManager.getInstance(this).initLoader(EXISTING_VEHICLE_LOADER,
                null,this)
        }

        // Initialize Views
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        mTitleText = findViewById<View>(R.id.reminder_title) as EditText
        mDateText = findViewById<View>(R.id.set_date) as TextView
        mTimeText = findViewById<View>(R.id.set_time) as TextView
        mRepeatText = findViewById<View>(R.id.set_repeat) as TextView
        mRepeatNoText = findViewById<View>(R.id.set_repeat_no) as TextView
        mRepeatTypeText = findViewById<View>(R.id.set_repeat_type) as TextView
        mRepeatSwitch = findViewById<View>(R.id.repeat_switch) as Switch
        mFAB1 = findViewById<View>(R.id.starred1) as FloatingActionButton
        mFAB2 = findViewById<View>(R.id.starred2) as FloatingActionButton

        // Initialize default values
        mActive = "true"
        mRepeat = "true"
        mRepeatNo = Integer.toString(1)
        mRepeatType = "Hour"
        mCalendar = Calendar.getInstance()
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mMinute = Calendar.getInstance().get(Calendar.MINUTE)
        mYear = Calendar.getInstance().get(Calendar.YEAR)
        mMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        mDay = Calendar.getInstance().get(Calendar.DATE)
        mDate = "$mDay/$mMonth/$mYear"
        mTime = "$mHour:$mMinute"

        // Setup Reminder Title EditText
        mTitleText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mTitle = s.toString().trim { it <= ' ' }
                mTitleText!!.error = null
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Setup TextViews using reminder values
        mDateText!!.text = mDate
        mTimeText!!.text = mTime
        mRepeatNoText!!.text = mRepeatNo
        mRepeatTypeText!!.text = mRepeatType
        mRepeatText!!.text = "Every $mRepeatNo $mRepeatType(s)"

        // To save state on device rotation
        if (savedInstanceState != null) {
            val savedTitle = savedInstanceState.getString(KEY_TITLE)
            mTitleText!!.setText(savedTitle)
            mTitle = savedTitle
            val savedTime = savedInstanceState.getString(KEY_TIME)
            mTimeText!!.text = savedTime
            mTime = savedTime
            val savedDate = savedInstanceState.getString(KEY_DATE)
            mDateText!!.text = savedDate
            mDate = savedDate
            val saveRepeat = savedInstanceState.getString(KEY_REPEAT)
            mRepeatText!!.text = saveRepeat
            mRepeat = saveRepeat
            val savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO)
            mRepeatNoText!!.text = savedRepeatNo
            mRepeatNo = savedRepeatNo
            val savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE)
            mRepeatTypeText!!.text = savedRepeatType
            mRepeatType = savedRepeatType
            mActive = savedInstanceState.getString(KEY_ACTIVE)
        }

        // Setup up active buttons
        if (mActive == "false") {
            mFAB1!!.visibility = View.VISIBLE
            mFAB2!!.visibility = View.GONE
        } else if (mActive == "true") {
            mFAB1!!.visibility = View.GONE
            mFAB2!!.visibility = View.VISIBLE
        }
        setSupportActionBar(mToolbar)
        supportActionBar!!.setTitle(R.string.title_activity_add_reminder)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(KEY_TITLE, mTitleText!!.text)
        outState.putCharSequence(KEY_TIME, mTimeText!!.text)
        outState.putCharSequence(KEY_DATE, mDateText!!.text)
        outState.putCharSequence(KEY_REPEAT, mRepeatText!!.text)
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText!!.text)
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText!!.text)
        outState.putCharSequence(KEY_ACTIVE, mActive)
    }

    // On clicking Time picker
    fun setTime(v: View?) {
        val now = Calendar.getInstance()
        val tpd = TimePickerDialog.newInstance(
            this,
            now[Calendar.HOUR_OF_DAY],
            now[Calendar.MINUTE],
            false
        )
        tpd.isThemeDark = false
        tpd.show(fragmentManager, "Timepickerdialog")
    }

    // On clicking Date picker
    fun setDate(v: View?) {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    // Obtain time from time picker
    override fun onTimeSet(view: RadialPickerLayout, hourOfDay: Int, minute: Int) {
        mHour = hourOfDay
        mMinute = minute
        mTime = if (minute < 10) {
            "$hourOfDay:0$minute"
        } else {
            "$hourOfDay:$minute"
        }
        mTimeText!!.text = mTime
    }

    // Obtain date from date picker
    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var monthOfYear = monthOfYear
        monthOfYear++
        mDay = dayOfMonth
        mMonth = monthOfYear
        mYear = year
        mDate = "$dayOfMonth/$monthOfYear/$year"
        mDateText!!.text = mDate
    }

    // On clicking the active button
    fun selectFab1(v: View?) {
        mFAB1 = findViewById<View>(R.id.starred1) as FloatingActionButton
        mFAB1!!.visibility = View.GONE
        mFAB2 = findViewById<View>(R.id.starred2) as FloatingActionButton
        mFAB2!!.visibility = View.VISIBLE
        mActive = "true"
    }

    // On clicking the inactive button
    fun selectFab2(v: View?) {
        mFAB2 = findViewById<View>(R.id.starred2) as FloatingActionButton
        mFAB2!!.visibility = View.GONE
        mFAB1 = findViewById<View>(R.id.starred1) as FloatingActionButton
        mFAB1!!.visibility = View.VISIBLE
        mActive = "false"
    }

    // On clicking the repeat switch
    @SuppressLint("SetTextI18n")
    fun onSwitchRepeat(view: View) {
        val on = (view as Switch).isChecked
        if (on) {
            mRepeat = "true"
            mRepeatText!!.text = "Every $mRepeatNo $mRepeatType(s)"
        } else {
            mRepeat = "false"
            mRepeatText!!.setText(R.string.repeat_off)
        }
    }

    // On clicking repeat type button
    @SuppressLint("SetTextI18n")
    fun selectRepeatType(v: View?) {
        val items = arrayOfNulls<String>(5)
        items[0] = "Minute"
        items[1] = "Hour"
        items[2] = "Day"
        items[3] = "Week"
        items[4] = "Month"

        // Create List Dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Type")
        builder.setItems(items) { dialog, item ->
            mRepeatType = items[item]
            mRepeatTypeText!!.text = mRepeatType
            mRepeatText!!.text = "Every $mRepeatNo $mRepeatType(s)"
        }
        val alert = builder.create()
        alert.show()
    }

    // On clicking repeat interval button
    @SuppressLint("SetTextI18n")
    fun setRepeatNo(v: View?) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Enter Number")

        // Create EditText box to input repeat number
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        alert.setView(input)
        alert.setPositiveButton(
            "Ok"
        ) { dialog, whichButton ->
            if (input.text.toString().length == 0) {
                mRepeatNo = Integer.toString(1)
                mRepeatNoText!!.text = mRepeatNo
                mRepeatText!!.text = "Every $mRepeatNo $mRepeatType(s)"
            } else {
                mRepeatNo = input.text.toString().trim { it <= ' ' }
                mRepeatNoText!!.text = mRepeatNo
                mRepeatText!!.text = "Every $mRepeatNo $mRepeatType(s)"
            }
        }
        alert.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton ->
            // do nothing
        }
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_add_reminder, menu)
        return true
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        // If this is a new reminder, hide the "Delete" menu item.
        if (mCurrentReminderUri == null) {
            val menuItem = menu.findItem(R.id.discard_reminder)
            menuItem.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // User clicked on a menu option in the app bar overflow menu
        when (item.itemId) {
            R.id.save_reminder -> {
                if (mTitleText!!.text.toString().length == 0) {
                    mTitleText!!.error = "Reminder Title cannot be blank!"
                } else {
                    saveReminder()
                    finish()
                }
                return true
            }
            R.id.discard_reminder -> {
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog()
                return true
            }
            android.R.id.home -> {
                // If the reminder hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mVehicleHasChanged) {
                    NavUtils.navigateUpFromSameTask(this)
                    return true
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                val discardButtonClickListener =
                    DialogInterface.OnClickListener { dialogInterface, i -> // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(this)
                    }

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUnsavedChangesDialog(
        discardButtonClickListener: DialogInterface.OnClickListener
    ) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard, discardButtonClickListener)
        builder.setNegativeButton(
            R.string.keep_editing
        ) { dialog, id -> // User clicked the "Keep editing" button, so dismiss the dialog
            // and continue editing the reminder.
            dialog?.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(
            R.string.delete
        ) { dialog, id -> // User clicked the "Delete" button, so delete the reminder.
            deleteReminder()
        }
        builder.setNegativeButton(
            R.string.cancel
        ) { dialog, id -> // User clicked the "Cancel" button, so dismiss the dialog
            // and continue editing the reminder.
            dialog?.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteReminder() {
        // Only perform the delete if this is an existing reminder.
        if (mCurrentReminderUri != null) {
            // Call the ContentResolver to delete the reminder at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentreminderUri
            // content URI already identifies the reminder that we want.
            val rowsDeleted = contentResolver.delete(mCurrentReminderUri!!, null, null)

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(
                    this, getString(R.string.editor_delete_reminder_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(
                    this, getString(R.string.editor_delete_reminder_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Close the activity
        finish()
    }

    // On clicking the save button
    fun saveReminder() {
        val values = ContentValues()
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE, mTitle)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_DATE, mDate)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TIME, mTime)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT, mRepeat)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO, mRepeatNo)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE, mRepeatType)
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE, mActive)


        // Set up calender for creating the notification
        mCalendar!![Calendar.MONTH] = --mMonth
        mCalendar!![Calendar.YEAR] = mYear
        mCalendar!![Calendar.DAY_OF_MONTH] = mDay
        mCalendar!![Calendar.HOUR_OF_DAY] = mHour
        mCalendar!![Calendar.MINUTE] = mMinute
        mCalendar!![Calendar.SECOND] = 0
        val selectedTimestamp = mCalendar!!.timeInMillis

        // Check repeat type
        if (mRepeatType == "Minute") {
            mRepeatTime = mRepeatNo!!.toInt() * milMinute
        } else if (mRepeatType == "Hour") {
            mRepeatTime = mRepeatNo!!.toInt() * milHour
        } else if (mRepeatType == "Day") {
            mRepeatTime = mRepeatNo!!.toInt() * milDay
        } else if (mRepeatType == "Week") {
            mRepeatTime = mRepeatNo!!.toInt() * milWeek
        } else if (mRepeatType == "Month") {
            mRepeatTime = mRepeatNo!!.toInt() * milMonth
        }
        if (mCurrentReminderUri == null) {
            // This is a NEW reminder, so insert a new reminder into the provider,
            // returning the content URI for the new reminder.
            val newUri =
                contentResolver.insert(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, values)

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(
                    this,
                    getString(R.string.editor_insert_reminder_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(
                    this, getString(R.string.editor_insert_reminder_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val rowsAffected = contentResolver.update(mCurrentReminderUri!!, values, null, null)

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(
                    this, getString(R.string.editor_update_reminder_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(
                    this, getString(R.string.editor_update_reminder_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Create a new notification
        if (mActive == "true") {
            if (mRepeat == "true") {
                AlarmScheduler().setRepeatAlarm(
                    applicationContext,
                    selectedTimestamp,
                    mCurrentReminderUri,
                    mRepeatTime
                )
            } else if (mRepeat == "false") {
                AlarmScheduler().setAlarm(
                    applicationContext,
                    selectedTimestamp,
                    mCurrentReminderUri
                )
            }
            Toast.makeText(
                this, "Alarm time is $selectedTimestamp",
                Toast.LENGTH_LONG
            ).show()
        }
        // Create toast to confirm new reminder
        Toast.makeText(
            applicationContext, "Saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    // On pressing the back button
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        private const val EXISTING_VEHICLE_LOADER = 0

        // Values for orientation change
        private const val KEY_TITLE = "title_key"
        private const val KEY_TIME = "time_key"
        private const val KEY_DATE = "date_key"
        private const val KEY_REPEAT = "repeat_key"
        private const val KEY_REPEAT_NO = "repeat_no_key"
        private const val KEY_REPEAT_TYPE = "repeat_type_key"
        private const val KEY_ACTIVE = "active_key"

        // Constant values in milliseconds
        private const val milMinute = 60000L
        private const val milHour = 3600000L
        private const val milDay = 86400000L
        private const val milWeek = 604800000L
        private const val milMonth = 2592000000L
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

        // This loader will execute the ContentProvider's query method on a background thread
        return androidx.loader.content.CursorLoader(
            this,  // Parent activity context
            mCurrentReminderUri!!,  // Query the content URI for the current reminder
            projection,  // Columns to include in the resulting Cursor
            null,  // No selection clause
            null,  // No selection arguments
            null
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor?) {
        if (data == null || data.count < 1) {
            return
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            val titleColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE)
            val dateColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE)
            val timeColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME)
            val repeatColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT)
            val repeatNoColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO)
            val repeatTypeColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE)
            val activeColumnIndex =
                data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE)

            // Extract out the value from the Cursor for the given column index
            val title = data.getString(titleColumnIndex)
            val date = data.getString(dateColumnIndex)
            val time = data.getString(timeColumnIndex)
            val repeat = data.getString(repeatColumnIndex)
            val repeatNo = data.getString(repeatNoColumnIndex)
            val repeatType = data.getString(repeatTypeColumnIndex)
            val active = data.getString(activeColumnIndex)

            // Update the views on the screen with the values from the database
            mTitleText!!.setText(title)
            mDateText!!.text = date
            mTimeText!!.text = time
            mRepeatNoText!!.text = repeatNo
            mRepeatTypeText!!.text = repeatType
            mRepeatText!!.text = "Every $repeatNo $repeatType(s)"
            // Setup up active buttons
            // Setup repeat switch
            if (repeat == "false") {
                mRepeatSwitch!!.isChecked = false
                mRepeatText!!.setText(R.string.repeat_off)
            } else if (repeat == "true") {
                mRepeatSwitch!!.isChecked = true
            }
        }
    }
    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
    }
}