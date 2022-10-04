package com.example.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.roundToInt

class Addalarm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addalarm)
        val fromintent = intent
        val lati = fromintent.getDoubleExtra("latitude",0.0)
        val longi =fromintent.getDoubleExtra("longitude",0.0)
        val coordinatesentered = findViewById<TextView>(R.id.addalarmtextview)
        val lat = (lati * 100.0).roundToInt()/100.0
        val lon = (longi * 100.0).roundToInt()/100.0
        coordinatesentered.text = lat.toString() + " , " + lon.toString()
    }
}