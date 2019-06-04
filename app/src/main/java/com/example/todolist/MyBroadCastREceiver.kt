package com.example.todolist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadCastREceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var actionName = intent?.action
        Log.d("test001", "asdsadasdsad : "+ actionName)
    }
}