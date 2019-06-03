package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.content_alarm.*
import java.lang.reflect.Array.set
import java.util.*
import java.util.Calendar

class Alarm : AppCompatActivity() {

    val hour : Int = 0
    val minute : Int = 0
    //val timePicker: TimePicker by bindView(R.id.time_picker)
    //val buttonSet: Button by bindView(R.id.button_set)
    //val buttonCancel: Button by bindView(R.id.button_cancel)
    //val relativeLayout: RelativeLayout by bindView(R.id.activity_main)
    var notificationId = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_alarm)

        time_picker.setIs24HourView(true)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        button_set.setOnClickListener {
            if (edit_text.text.isBlank()) {
                Toast.makeText(applicationContext, "Title is Required!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().apply {
                    //set(Calendar.HOUR_OF_DAY, time_picker.hour)
                        set(Calendar.HOUR_OF_DAY, hour)
                    //set(Calendar.MINUTE, time_picker.minute)
                        set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }.timeInMillis,
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
                        putExtra("notificationId", ++notificationId)
                        putExtra("reminder", edit_text.text)
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            )
            Toast.makeText(applicationContext, "SET!! ${edit_text.text}", Toast.LENGTH_SHORT).show()
            reset()
        }

        button_cancel.setOnClickListener {
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    applicationContext, 0, Intent(applicationContext, AlarmBroadcastReceiver::class.java), 0))
            Toast.makeText(applicationContext, "CANCEL!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow( relativelayout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        relativelayout.requestFocus()
        return super.onTouchEvent(event)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        reset()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun reset() {
        time_picker.apply {
            var now = Calendar.getInstance()
            hour = now.get(Calendar.HOUR_OF_DAY)
            minute != now.get(Calendar.MINUTE)
        }
        edit_text.setText("")
    }
}