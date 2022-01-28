package com.example.getlocation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.getlocation.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val task = fusedLocationClient.lastLocation


        task.addOnSuccessListener {
            if(it!=null) {
                Toast.makeText(this, "${it.latitude} , ${it.longitude}", Toast.LENGTH_SHORT).show()
                val myLocation = LatLng(it.latitude , it.longitude)
                val myMarker : MarkerOptions  = MarkerOptions().apply {
                    position(myLocation)
                    title("Your Current location")
                }

                mMap.addMarker(myMarker)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))

            }
            else
                Toast.makeText(this, "it = null", Toast.LENGTH_SHORT).show()
        }
    }
}