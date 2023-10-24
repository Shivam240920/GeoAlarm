package com.example.geoalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "LocationAlarm:WakeLock"
        )
        wakeLock.acquire(5000)

        val alarmIntent = Intent(context, MessageBroadcast::class.java)
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(alarmIntent)

        wakeLock.release()
    }
}