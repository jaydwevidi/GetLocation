package com.example.getlocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationRequest = LocationRequest.create()
        locationRequest.setInterval(2000)
        locationRequest.setFastestInterval(1000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        getLocationFine()
        checkSettingsThenStart()

        findViewById<Button>(R.id.button1).setOnClickListener {
            startMapActivity()
        }

    }


    private fun checkSettingsThenStart(){
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()

        val client = LocationServices.getSettingsClient(this);

        val task : Task<LocationSettingsResponse> = client.checkLocationSettings(request)

        val successListener = object : OnSuccessListener<LocationSettingsResponse> {
            override fun onSuccess(p0: LocationSettingsResponse?) {
                startLocationUpdates()
            }

        }

        val failureListener = object : OnFailureListener {
            override fun onFailure(p0: Exception) {
                Log.e("Failed", "onFailureListener: " )
            }

        }

        task.addOnSuccessListener (successListener)
        task.addOnFailureListener (failureListener)

    }

    val locationCallback = object : LocationCallback(){

        override fun onLocationResult(locationResult: LocationResult) {
            for (i in locationResult.locations){
                Log.d("TAG", "onLocationResult: ${i.latitude} , ${i.longitude}")
            }
            super.onLocationResult(locationResult)
        }
    }

    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "updates Location no permission", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper())
    }

    private fun stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationFinePermission() : Boolean {
        val requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val checkVal: Int = applicationContext.checkCallingOrSelfPermission(requiredPermission)
        return checkVal== PackageManager.PERMISSION_GRANTED

    }

    private fun getLocationFineDexter() {
        val listener= object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                //displayContacts()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        }
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(listener)
            .check()
    }

    private fun startMapActivity(){
        val i = Intent(this , MapsActivity::class.java)
        startActivity(i)
    }

    fun getLocationFine(){
        //Toast.makeText(this, "get Fine called", Toast.LENGTH_SHORT).show()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this , arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION //, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) , 101)
            //finish()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val task = fusedLocationClient.lastLocation

        task.addOnSuccessListener {
            if(it!=null) {
                Toast.makeText(this, "${it.latitude} , ${it.longitude}", Toast.LENGTH_SHORT).show()
                //mapActivity()
            }
            else
                Toast.makeText(this, "it = null", Toast.LENGTH_SHORT).show()
        }

    }
}