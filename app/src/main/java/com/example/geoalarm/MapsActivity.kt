package com.example.geoalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.geoalarm.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val currentLatitude = intent.getDoubleExtra("latitude",-34.0)
        val currentLongitude = intent.getDoubleExtra("longitude",151.0)

        val currentLocation = LatLng(currentLatitude,currentLongitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,18f))
        mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))


        mMap.setOnMapClickListener {
            mMap.setOnMapClickListener { latlng ->
                mMap.clear()
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
                val latitude = latlng.latitude
                val longitude = latlng.longitude
                val location = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(location).title("Mark here"))
                val intent = Intent(this@MapsActivity, AddAlarm::class.java)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                startActivity(intent)
            }
        }
    }
}