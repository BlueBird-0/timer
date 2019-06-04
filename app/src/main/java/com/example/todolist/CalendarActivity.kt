package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ognev.kotlin.agendacalendarview.CalendarController
import com.ognev.kotlin.agendacalendarview.CalendarManager
import com.ognev.kotlin.agendacalendarview.builder.CalendarContentManager
import com.ognev.kotlin.agendacalendarview.models.*
import com.ognev.kotlin.agendacalendarview.render.DefaultEventAdapter
import com.ognev.kotlin.agendacalendarview.utils.DateHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_calendar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.applikeysolutions.cosmocalendar.listeners.OnMonthChangeListener
import java.time.Month


class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_calendar)

        //toolbar.setTitle("")
        ///setSupportActionBar(toolbar)

        calendar_view.setOnMonthChangeListener(OnMonthChangeListener {

        })

    }
}
