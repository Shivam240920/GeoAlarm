package com.example.myproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.database.getDoubleOrNull
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    private var dblati:Double = 0.0
    private var dblongi:Double = 0.0
    private var service:Intent?=null
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){

        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        when{
            it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION,false)->{
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                    if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                        backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                }
            }
            it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION,false)->{

            }

        }
    }

    var listItem:ArrayList<String> = ArrayList()

    val db =DataBaseHandler(this)


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this,MapsActivity::class.java)
        val fn = findViewById<FloatingActionButton>(R.id.add)
        fn.setOnClickListener {
            startActivity(intent)
        }
        viewData()

        GlobalScope.launch {
            while (true){
                checklist()
                delay(1000)
            }
        }


        service = Intent(this,LocationService::class.java)

        var btnstart = findViewById<Button>(R.id.btnStartLocationTracking)
        btnstart.setOnClickListener {
            checkPermissions()
        }

        findViewById<Button>(R.id.btnStopLocationTracking).setOnClickListener {
            stopService(service)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.opt,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId:Int = item.itemId
        if(itemId == R.id.helpbutton){
            val intent = Intent(this,helpactivity::class.java)
            startActivity(intent)
        }
        else if (itemId == R.id.contactbutton){
            val intent = Intent(this,contactactivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    fun viewData(){
        var cursor = db.listfromdb()
        if(cursor?.count == 0){
            Toast.makeText(this, "Nothing To Show", Toast.LENGTH_SHORT).show()
        }
        else{
            while (cursor!!.moveToNext()){
                listItem.add(cursor.getString(cursor.getColumnIndex(COL_MSG)))
            }

            var adapter = ArrayAdapter(this,android.R.layout.simple_list_item_activated_1,listItem)
            val listView = findViewById<ListView>(R.id.main_listid)
            listView.adapter = adapter
        }
    }

    private fun checklist(){
        if(matchCoordinates()){
            Log.d("checklol","hi")
        }
        else{
            Log.d("checklol","no")
        }
    }



    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }
    fun checkPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if(ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )!=PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )!=PackageManager.PERMISSION_GRANTED){
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }else{
                startService(service)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }
    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
        findViewById<TextView>(R.id.tvlatitude).text = "Latitude --> ${locationEvent.latitude}"
        findViewById<TextView>(R.id.tvlongitude).text = "Longitude --> ${locationEvent.longitude}"
        dblati = locationEvent.latitude!!
        dblongi = locationEvent.longitude!!
    }

    fun matchCoordinates():Boolean{
        var cc = db.checklocation()
        if(cc!!.latitude/100 == dblati/100 && cc!!.longitude/10==dblongi/10)
            return true
        return false
    }

}