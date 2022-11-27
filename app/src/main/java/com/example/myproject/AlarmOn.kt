package com.example.myproject
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class AlarmOn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_on)

        findViewById<Button>(R.id.buttonDeleteAlarm).isVisible = false

        val message = intent.getStringExtra("SEC_RINGSTONE")
        findViewById<TextView>(R.id.AlarmMessage).text = ""+message
        findViewById<Button>(R.id.buttonStopAlarm).setOnClickListener {
            audiplay.stopAudio()
        }
    }
}