package com.example.myproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        audiplay.playAudio(context)
        val it = Intent(context,AlarmOn::class.java)
        val strg = intent!!.getStringExtra("messageofalarm")
        it.putExtra("SEC_RINGSTONE",strg)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(it)
    }
}