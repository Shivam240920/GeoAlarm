package com.example.myproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class Addalarm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addalarm)
        val fromintent = intent
        val context = this
        val lati = fromintent.getDoubleExtra("latitude",0.0)
        val longi =fromintent.getDoubleExtra("longitude",0.0)
        val coordinatesentered = findViewById<TextView>(R.id.addalarmtextview)
        val lat = (lati * 10000000.0).roundToInt()/100.0
        val lon = (longi * 10000000.0).roundToInt()/100.0
        coordinatesentered.text = lat.toString() + " , " + lon.toString()

        val addalarmbtn = findViewById<Button>(R.id.addalarmbtn)
        addalarmbtn.setOnClickListener({
            val addalarmedittext = findViewById<EditText>(R.id.addalarmeditview)
            if(addalarmedittext.text.toString().length>0){
                var user = User(lati,longi,addalarmedittext.text.toString())
                var db = DataBaseHandler(context)
                db.insertData(user)
                
                intent.putExtra("refresh", false)
            }
            else{
                Toast.makeText(context, "Please Enter Some Message", Toast.LENGTH_SHORT).show()
            }
        })
    }
}