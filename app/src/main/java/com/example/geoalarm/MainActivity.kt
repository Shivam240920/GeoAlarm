package com.example.geoalarm

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerHomeAdapter : RecyclerHomeAdapter
    private lateinit var alarmService: RunningService
    private var isServiceBound = false
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RunningService.AlarmBinder
                alarmService = binder.getService()
                isServiceBound = true

                if (alarmService.isAlarmRunning) {
                    val intent = Intent(this@MainActivity, MessageBroadcast::class.java)
                    startActivity(intent)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
            }
        }

        val runningServiceIntent = Intent(this, RunningService::class.java)
        isServiceBound = bindService(
            runningServiceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerHomeAdapter = RecyclerHomeAdapter(this)
        recyclerView.adapter = recyclerHomeAdapter

        runBlocking {
            val job = launch {
                val db = DatabaseHandler(this@MainActivity)
                db.loadListfromDB()
            }
            job.join()
        }

        val start_Button = findViewById<Button>(R.id.start_button)
        val stop_Button = findViewById<Button>(R.id.stop_button)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        /*------------------------------------------------------------------------------------------------------------------------------------*/

        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsToRequest.add(Manifest.permission.WAKE_LOCK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        ActivityCompat.requestPermissions(this , permissionsToRequest.toTypedArray(),CONSTANTS.HOME_ACTIVITY_REQUEST_CODE)

        start_Button.setOnClickListener {
            permissionsToRequest.clear()
            if (ContextCompat.checkSelfPermission(this , Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.WAKE_LOCK)
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(this , Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this , Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)

            if(permissionsToRequest.isNotEmpty()) ActivityCompat.requestPermissions(this , permissionsToRequest.toTypedArray() , CONSTANTS.START_BUTTON_REQUEST_CODE)
            else startService()
        }

        stop_Button.setOnClickListener {
            stopService()
        }

        floatingActionButton.setOnClickListener {
            permissionsToRequest.clear()
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

            if(permissionsToRequest.isNotEmpty()) ActivityCompat.requestPermissions(this , permissionsToRequest.toTypedArray() , CONSTANTS.START_BUTTON_REQUEST_CODE)
            else startActivity()
        }
    }

    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        recyclerHomeAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            when (requestCode) {
                CONSTANTS.START_BUTTON_REQUEST_CODE -> {
                    startService()
                }
                CONSTANTS.FLOATING_ACTION_BUTTON_REQUEST_CODE -> {
                    startActivity()
                }
            }
        } else if (requestCode == CONSTANTS.START_BUTTON_REQUEST_CODE || requestCode == CONSTANTS.FLOATING_ACTION_BUTTON_REQUEST_CODE){
            Toast.makeText(this, "Please grant permissions", Toast.LENGTH_SHORT).show()
            openAppSettings()
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------------------------*/

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    private fun checkGPSEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val locationMode: Int = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------------------------*/

    private fun startService() {
        if(isServiceBound && !alarmService.isServiceRunning){
            if (!checkGPSEnabled()) {
                Toast.makeText(this, "current location required", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } else {
                val intent = Intent(this, RunningService::class.java)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this , intent.also { it.action = RunningService.Actions.START.toString() })
                } else {
                    startService(intent.also {
                        it.action = RunningService.Actions.START.toString()
                    })
                }
            }
        }
    }

    private fun stopService() {
        if(isServiceBound && alarmService.isServiceRunning){
            val intent = Intent(this, RunningService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent.also { it.action = RunningService.Actions.STOP.toString() })
        }
    }

    @Suppress("MissingPermission")
    private fun startActivity() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) != true) {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
        } else if(checkGPSEnabled()){
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                run {
                    if (location != null) {
                        val intent = Intent(this , MapsActivity::class.java)
                        intent.putExtra("latitude" , location.latitude)
                        intent.putExtra("longitude" , location.longitude)
                        startActivity(intent)
                    }
                }
            }
            fusedLocationProviderClient.lastLocation.addOnFailureListener {
                Toast.makeText(this, "Failed to get last location", Toast.LENGTH_SHORT).show()
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
}