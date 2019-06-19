package com.example.todolist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import android.R
import android.os.Bundle
import android.app.Activity
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager
import com.applikeysolutions.cosmocalendar.selection.selectionbar.MultipleSelectionBarAdapter
import com.applikeysolutions.cosmocalendar.settings.date.DateInterface
import com.applikeysolutions.cosmocalendar.settings.lists.CalendarListsInterface
import com.applikeysolutions.cosmocalendar.settings.selection.SelectionInterface
import kotlinx.android.synthetic.main.content_alarm.*
import kotlinx.android.synthetic.main.content_calendar.*
import java.util.*
import android.R.attr.textColor
import android.content.Intent
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays




class TimeRecord : AppCompatActivity(), OnDaySelectedListener{
    // 상수 관련
    internal var dbName = "apList.db" // name of Database;
    internal var tableName = "apListTable" // name of Table;
    internal var dbMode = Context.MODE_PRIVATE

    // Database 관련 객체들
    internal var db: SQLiteDatabase ?= null

    val days = TreeSet<Long>()

    override fun onDaySelected() {
            Log.d("test003", "선택 : "+calendar_view.selectedDays.get(0).calendar.get(Calendar.DAY_OF_MONTH))
        if(calendar_view.selectedDays.get(0).calendar.get(Calendar.DAY_OF_MONTH) == 13) {
            val intent = Intent(this, BarGraph::class.java)
            intent.putExtra("date", "2019:6:10_20")
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.todolist.R.layout.activity_calendar)

        calendar_view.selectionManager = SingleSelectionManager(this)


        var s1 = "2019:6:10_20"
        var s2 = "2019:6:11_320"
        var s3 = "2019:6:12_180"


        var studyingCalendar = Calendar.getInstance()
        days.add(studyingCalendar.timeInMillis)
        studyingCalendar.set(Calendar.DAY_OF_MONTH, 13)

        //Define colors
        var textColor = Color.parseColor("#F39C12")
        var selectedTextColor = Color.parseColor("#F39C12")
        val disabledTextColor = Color.parseColor("#F39C12")
        val connectedDays = ConnectedDays(days, textColor, selectedTextColor, disabledTextColor)

        calendar_view.addConnectedDays(connectedDays);
        /*
        // Database 생성 및 열기
        createDatabase(dbName,dbMode)

        Log.d("test002","디ㅣ샏성")

        // Table 생성
        //createTable()

        Log.d("test002","테이블생성끝")
        var s1 = "2019:6:10_20"
        var s2 = "2019:6:11_320"
        var s3 = "2019:6:12_180"

        insertData(s1)
        insertData(s2)
        insertData(s3)

        selectAll()*/
    }


    // Database 생성 및 열기
    fun createDatabase(dbName: String, dbMode: Int) {
        db = openOrCreateDatabase(dbName, dbMode, null)

    }

    // Table 생성
    fun createTable() {
        val sql = "create table $tableName(id integer primary key autoincrement, voca text not null)"
        db?.execSQL(sql)
    }

    // Table 삭제
    fun removeTable() {
        val sql = "drop table $tableName"
        db?.execSQL(sql)
    }

    // Data 추가
    fun insertData(voca: String) {
        val sql = "insert into $tableName values(NULL, '$voca');"
        db?.execSQL(sql)
    }

    // Data 업데이트
    fun updateData(index: Int, voca: String) {
        val sql = "update $tableName set voca = '$voca' where id = $index;"
        db?.execSQL(sql)
    }

    // Data 삭제
    fun removeData(index: Int) {
        val sql = "delete from $tableName where id = $index;"
        db?.execSQL(sql)
    }

    // Data 읽기(꺼내오기)
    fun selectData(index: Int) {
        val sql = "select * from $tableName where id = $index;"
        val result = db?.rawQuery(sql, null)

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result!!.moveToFirst()) {
            val id = result.getInt(0)
            val voca = result.getString(1)
            Toast.makeText(this, "index= $id voca=$voca", Toast.LENGTH_SHORT).show()
        }
        result.close()
    }


    // 모든 Data 읽기
    fun selectAll() {
        val sql = "select * from $tableName;"
        val results = db?.rawQuery(sql, null)

        results?.moveToFirst()

        while (results?.isAfterLast == false) {
            val id = results.getInt(0)
            val voca = results.getString(1)
            Toast.makeText(this, "index= $id voca=$voca", Toast.LENGTH_SHORT).show()
            Log.d("test002", "" + id + voca)
            results.moveToNext()
        }
        results?.close()
    }


}
