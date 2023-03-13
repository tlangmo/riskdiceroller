package com.motesque.riskdice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.random.Random

class LaplaceActivity : AppCompatActivity() {
    var statisticsView: TextView? = null
    var startButton: Button? = null
    var trialsEdit : EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val random = Random.Default
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laplace)
        statisticsView = findViewById(R.id.text_view_statistics)
        startButton = findViewById(R.id.button_start)
        trialsEdit = findViewById(R.id.edit_trials)
        startButton?.setOnClickListener {
            val stats = mutableMapOf<Int,Int>()
            val maxTrials = Math.min(10000000,Integer.valueOf(trialsEdit?.text.toString())-1)
            for (i in 0..maxTrials) {
               // val result = Random.nextInt(1, 7)
               // val result = (1..7).random()
                val result = random.nextInt(1,7)
                stats.put(result, stats.getOrDefault(result, 0) + 1)
            }
            println(stats)
            var msg : String = ""
            for (i in 1..6) {
                val num = stats.getOrDefault(i,0)
                msg += "$i: $num\n"
            }
            statisticsView?.setText(msg)
        }
    }
}