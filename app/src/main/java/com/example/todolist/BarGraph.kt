package com.example.todolist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.EditText

import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.content_graph.*
import java.lang.String.valueOf


class BarGraph : AppCompatActivity(){
    var kor = 3.3
    var mat = 1.2
    var eng = 2.0


    var calsBurned = 0
    var calsConsumed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_graph)
    }

    fun addBurned(v: View) { // Get the new value from a user input:
        val newBurnedCals = burned.text.toString().toInt()

        // Update the old value:
        calsBurned = newBurnedCals

        Log.d("test003","호출전")
        updateChart ();}

    fun addConsumed(v: View) { // Get the new value from a user input:
        val newConsumedCals = consumed.text.toString().toInt()
        // Update the old value:
        calsConsumed = newConsumedCals
        updateChart ();}



    fun updateChart() {
        // Update the text in a center of the chart:


        number_of_calories.setText(calsBurned.toString() + " / " + calsConsumed.toString())


        // Calculate the slice size and update the pie chart:
        val d = calsBurned.toDouble() / calsConsumed.toDouble()

        val progress = (d * 100).toInt()

        stats_progressbar.setProgress(progress)


    }
}
