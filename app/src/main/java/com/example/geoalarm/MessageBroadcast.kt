package com.example.geoalarm

import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class MessageBroadcast : AppCompatActivity() {

    private lateinit var alarmService: RunningService
    private var isServiceBound = false
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RunningService.AlarmBinder
                alarmService = binder.getService()
                findViewById<TextView>(R.id.messagetext).text = alarmService.message
                isServiceBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
            }
        }


        val serviceIntent = Intent(this, RunningService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        findViewById<Button>(R.id.stopbtn).setOnClickListener {
            if (isServiceBound) alarmService.stopLocationService()

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}