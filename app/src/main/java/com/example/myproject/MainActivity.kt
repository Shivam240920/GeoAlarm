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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    private var service:Intent?=null
    private lateinit var job: Job

    private var dblat:Int = 0
    private var dblong:Int = 0

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




        service = Intent(this,LocationService::class.java)

        var btnstart = findViewById<Button>(R.id.btnStartLocationTracking)
        btnstart.setOnClickListener {

            job = GlobalScope.launch {
                while (true){
                    checklist()
                    checkPermissions()
                    delay(1000)
                }
            }
        }

        findViewById<Button>(R.id.btnStopLocationTracking).setOnClickListener {
            job.cancel()
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
        if(matchCoordinates()!=null){
            Log.d("checklol",matchCoordinates().toString())
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
        dblat = (locationEvent.latitude!!*1000).toInt()
        dblong = (locationEvent.longitude!!*1000).toInt()
    }

    fun matchCoordinates():String?{

        var ans = db.checklocation()
        for (cr in ans){
            if (dblat==(cr!!.latitude*1000).toInt() && dblong==(cr!!.longitude*1000).toInt())
                return cr.msg
        }
        return null
    }

}