package com.example.geoalarm

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlin.math.abs

class RunningService : Service() {
    private companion object{
        const val CHANNEL_ID = "running_channel"
        const val NOTIFICATION_ID = 1
    }

    private val binder = AlarmBinder()

    inner class AlarmBinder : Binder() {
        fun getService(): RunningService {
            return this@RunningService
        }
    }

    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private var ringtone: Ringtone? = null
    var message:String = ""
    var isAlarmRunning = false
    var isServiceRunning = false

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private var locationcallback = object : LocationCallback() {

        override fun onLocationResult(locationresult: LocationResult) {
            super.onLocationResult(locationresult)
            if (locationresult.lastLocation != null){
                latitude = locationresult.lastLocation!!.latitude
                longitude = locationresult.lastLocation!!.longitude
                Log.d("mylocations", "onLocationResult: $latitude , $longitude")

                notificationBuilder.setContentText(String.format("Location -> %.4f , %.4f", latitude, longitude))
                notificationBuilder.setContentText(String.format("Location -> %.4f , %.4f", latitude, longitude))
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

                if(!isAlarmRunning) {
                    for(item in itemList){
                        if (abs(item.latitude - latitude) <= 0.00168 && abs(item.longitude - longitude) <= 0.00168){
                            isAlarmRunning = true
                            message = item.message
                            playDefaultAlarmRingtone()
                            wakeupMainActivity()
                            return
                        }
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            when (intent.action) {
                Actions.START.toString() -> startLocationService()
                Actions.STOP.toString() -> stopLocationService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startLocationService(){

        val resultIntent = Intent(this,MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(applicationContext,0,resultIntent,
            PendingIntent.FLAG_IMMUTABLE)



        isServiceRunning = true
        val locationRequest = LocationRequest()
            .setInterval(4000)
            .setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,locationcallback,
            Looper.getMainLooper())


        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("GeoAlarm")
            .setContentText(String.format("Location -> %.4f , %.4f" , latitude , longitude))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent,true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)

        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    fun stopLocationService(){
        Log.d("mylocations", "stopLocationService")
        notificationManager.cancel(NOTIFICATION_ID)
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationcallback)
        isServiceRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        stopRingtone()
    }

    private fun wakeupMainActivity(){
        val intent = Intent(applicationContext, MessageBroadcast::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun playDefaultAlarmRingtone() {
        val defaultRingtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, defaultRingtoneUri)

        if (ringtone != null) {
            ringtone!!.play()
        }
    }

    fun stopRingtone() {
        if (ringtone != null && ringtone!!.isPlaying) {
            ringtone!!.stop()
        }
        ringtone = null
        isAlarmRunning = false
    }

    enum class Actions {
        START , STOP
    }
}