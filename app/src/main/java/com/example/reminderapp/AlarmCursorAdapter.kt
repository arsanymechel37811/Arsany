package com.example.reminderapp

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable

import com.amulyakhare.textdrawable.util.ColorGenerator


class AlarmCursorAdapter(context: Context?, c: Cursor?) :
    CursorAdapter(context, c, 0 /* flags */) {
    private var mTitleText: TextView? = null
    private var mDateAndTimeText: TextView? = null
    private var mRepeatInfoText: TextView? = null
    private var mActiveImage: ImageView? = null
    private var mThumbnailImage: ImageView? = null
    private val mColorGenerator: ColorGenerator = ColorGenerator.DEFAULT
    private var mDrawableBuilder: TextDrawable? = null
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        mTitleText = view.findViewById<View>(R.id.recycle_title) as TextView
        mDateAndTimeText = view.findViewById<View>(R.id.recycle_date_time) as TextView
        mRepeatInfoText = view.findViewById<View>(R.id.recycle_repeat_info) as TextView
        mActiveImage = view.findViewById<View>(R.id.active_image) as ImageView
        mThumbnailImage = view.findViewById<View>(R.id.thumbnail_image) as ImageView
        val titleColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE)
        val dateColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE)
        val timeColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME)
        val repeatColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT)
        val repeatNoColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO)
        val repeatTypeColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE)
        val activeColumnIndex =
            cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE)
        val title = cursor.getString(titleColumnIndex)
        val date = cursor.getString(dateColumnIndex)
        val time = cursor.getString(timeColumnIndex)
        val repeat = cursor.getString(repeatColumnIndex)
        val repeatNo = cursor.getString(repeatNoColumnIndex)
        val repeatType = cursor.getString(repeatTypeColumnIndex)
        val active = cursor.getString(activeColumnIndex)
        val dateTime = "$date $time"
        setReminderTitle(title)
        setReminderDateTime(dateTime)
        setReminderRepeatInfo(repeat, repeatNo, repeatType)
        setActiveImage(active)
    }

    // Set reminder title view
    fun setReminderTitle(title: String?) {
        mTitleText!!.text = title
        var letter = "A"
        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1)
        }
        val color: Int = mColorGenerator.getRandomColor()

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
            .buildRound(letter, color)
        mThumbnailImage!!.setImageDrawable(mDrawableBuilder)
    }

    // Set date and time views
    fun setReminderDateTime(datetime: String?) {
        mDateAndTimeText!!.text = datetime
    }

    // Set repeat views
    @SuppressLint("SetTextI18n")
    fun setReminderRepeatInfo(repeat: String, repeatNo: String, repeatType: String) {
        if (repeat == "true") {
            mRepeatInfoText!!.text = "Every $repeatNo $repeatType(s)"
        } else if (repeat == "false") {
            mRepeatInfoText!!.text = "Repeat Off"
        }
    }

    // Set active image as on or off
    fun setActiveImage(active: String) {
        if (active == "true") {
            mActiveImage!!.setImageResource(R.drawable.ic_notifications_on_white_24dp)
        } else if (active == "false") {
            mActiveImage!!.setImageResource(R.drawable.ic_notifications_off_grey600_24dp)
        }
    }
}
