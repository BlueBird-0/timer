package com.example.todolist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import android.R
import android.os.Bundle
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button


class TimeRecord : AppCompatActivity() {

    var data1 = "국어, 2019.06.07, 38"
    var data2 = "수학, 2019.06.07, 52"


    // 상수 관련
    internal var dbName = "apList.db" // name of Database;
    internal var tableName = "apListTable" // name of Table;
    internal var dbMode = Context.MODE_PRIVATE

    // Database 관련 객체들
    internal var db: SQLiteDatabase ?= null

    // GUI 관련
    var btCreateDB: Button ?= null
    var btCreateTable: Button?= null
    var btRemoveTable: Button?= null
    var btInsertData: Button?= null
    var btRemoveData: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.todolist.R.layout.activity_calendar)

        // Database 생성 및 열기
        createDatabase(dbName,dbMode)

        Log.d("test002","디ㅣ샏성")

        // Table 생성
        createTable()

        Log.d("test002","테이블생성끝")

        insertData("김소정")
        insertData("전인학")
        insertData("이한솔")

        selectAll()
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
            Toast.makeText(this, "index= $id voca=$voca", 0).show()
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
