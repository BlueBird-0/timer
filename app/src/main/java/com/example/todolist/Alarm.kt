package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.content_alarm.*
import kotlinx.android.synthetic.main.popup_confirm.*
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class Alarm : AppCompatActivity() {

    var timePicker_mil :Long = 0
    var notificationId = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

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
                    set(Calendar.HOUR_OF_DAY, time_picker.hour)
                        //set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, time_picker.minute)
                    //    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    Log.d("test003","시:"+time_picker.hour.toString()+" 분:"+time_picker.minute.toString())
                    var cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, time_picker.hour)
                    cal.set(Calendar.MINUTE, time_picker.minute)
                    timePicker_mil = cal.timeInMillis
                    Log.d("test003","선택 시간 : " + timePicker_mil)
                    var dayTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    Log.d("test003", "선택 2 : "+ dayTime.format(timePicker_mil))
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


            var current_time = Calendar.getInstance().timeInMillis
            Log.d("test003","현재시간 : "+ current_time)
            var dayTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            Log.d("test003", "선택 2 : "+ dayTime.format(current_time))

            Log.d("test003", "남으시간 :"+ (timePicker_mil - current_time) )

            // 시간 구하기

            //var time : Long = Calendar.getInstance().apply { this
            //get(Calendar.HOUR_OF_DAY)
            //get(Calendar.MINUTE)
            //get(Calendar.SECOND)}.timeInMillis

            //Log.d("test003","시:"+(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))+"분:"+time/1000/60%60)
            //Log.d("test003","현재시간 밀리초: "+ time)


  //          var pickedtime = Calendar.getInstance().apply {
  //              //set(Calendar.HOUR_OF_DAY, time_picker.hour)
  //              time_picker.hour
  //              time_picker.minute
  //          }.timeInMillis

           Log.d("test003","선택시간 밀리초:"+ Calendar.getInstance().apply {  time_picker.hour
                 time_picker.minute}.timeInMillis)
            var eventually = timePicker_mil - current_time
            if((timePicker_mil - current_time)<0){
                eventually += 86400000
            }
            Log.d("test003",""+eventually.toString())

            //데이터 전송
            var  data = Intent()
            eventually += 1000
            data.putExtra("결과",(eventually))
            setResult(0,data)

            Toast.makeText(this, ""+(eventually/1000/60).toString()+"분 후 알람이 울립니다.", Toast.LENGTH_SHORT).show()

            finish()
        }

        reset()
        button_cancel.setOnClickListener {
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    applicationContext, 0, Intent(applicationContext, AlarmBroadcastReceiver::class.java), 0))
            Toast.makeText(applicationContext, "CANCEL!!", Toast.LENGTH_SHORT).show()
        }


        test_alarm.setOnClickListener(View.OnClickListener {
            //데이터 전송
            var  data = Intent()
            var eventually = 1000
            data.putExtra("결과",(eventually))
            setResult(0,data)

            Toast.makeText(this, ""+(eventually/1000/60).toString()+"분 후 알람이 울립니다.", Toast.LENGTH_SHORT).show()
            finish()
        })
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.hideSoftInputFromWindow( relativelayout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//        relativelayout.requestFocus()
//        return super.onTouchEvent(event)
//    }

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