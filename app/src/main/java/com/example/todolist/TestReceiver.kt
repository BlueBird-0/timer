package com.example.todolist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TestReceiver : BroadcastReceiver() {
    //private val TAG : String = TestReceiver.class.getSimpleName();

    override fun onReceive(context: Context?, intent: Intent?) {
        var name : String = intent!!.action

        Log.d("test001", "bbbbbbbb")
        if(name.equals("com.example.todolist.SEND_BROAD_CAST")){
            Log.d("test001", "BroadcastReceiver :: com.example.todolist.SEND_BROAD_CAST::"+intent.getStringExtra("sendString"))
        }
    }




}
