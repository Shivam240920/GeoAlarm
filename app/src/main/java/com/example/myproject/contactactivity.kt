package com.example.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class contactactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactactivity)
        val contacttext = findViewById<TextView>(R.id.ctbutton)
        contacttext.setText(R.string.contactcontent)

    }
}