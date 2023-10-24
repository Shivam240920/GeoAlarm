package com.example.geoalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddAlarm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val latitude = intent.getDoubleExtra("latitude" , -1e5)
        val longitude = intent.getDoubleExtra("longitude",-1e9)
        val button = findViewById<Button>(R.id.enter_button)
        val editText = findViewById<EditText>(R.id.edit_text)

        button.setOnClickListener {
            if (editText.toString().isNotEmpty()) {
                val db = DatabaseHandler(this)
                val item = Item(editText.text.toString(),latitude,longitude)
                db.insertData(item)
                itemList.add(item)
                setResult(RESULT_OK, Intent().putExtra("result","added"))
                finish()
            } else {
                Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}