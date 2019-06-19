package com.example.todolist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.content_graph.*


class BarGraph : AppCompatActivity(){
    var kor = 20
    var mat = 30
    var eng = 120

    var sum = kor+mat+eng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_graph)
        Log.d("test003", "데이터 받아옴 : "+intent.getStringExtra("date"))
        updateChart()
    }

    fun updateChart() {
        // Update the text in a center of the chart:


        number_of_calories.setText(sum.toString()+"분")

        // Calculate the slice size and update the pie chart:
        val kor_p = kor.toDouble()/sum.toDouble()
        val progress1 = (kor_p * 100).toInt()

        val mat_p = mat.toDouble()/sum.toDouble()
        val progress2 = (mat_p * 100).toInt()


        bar_title.setText(intent.getStringExtra("date").split("_")[0])
        stats_progressbar1.setProgress(progress1)
        stats_progressbar2.setProgress(progress2)

        text1.setText("국어 : "+kor)
        text2.setText("수학 : "+mat)
        text3.setText("영어 : "+eng)


    }
}
