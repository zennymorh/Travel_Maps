package com.zennymorh.travel_maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    mMap.clear()
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    val geocoder = Geocoder(applicationContext, Locale.getDefault())

                    try {
                        //Getting the address of user
                        val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addressList != null && addressList.size > 0) {
                            println(addressList[0].toString())
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }

        }

        //Checking to see if location permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            //Requesting for permission
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener)
            val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val userLastLocation = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)

            mMap.addMarker(MarkerOptions().position(userLastLocation).title("Your location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15f))
        }


    }

     val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()
            val geocoder = Geocoder(applicationContext, Locale.getDefault())

            var address = ""

            try {
                //Getting the address of user
                val addressList = geocoder.getFromLocation(p0!!.latitude, p0.longitude, 1)

                if (addressList != null && addressList.size > 0) {
                    if (addressList[0].thoroughfare != null) {
                        address += addressList[0].thoroughfare

                        if (addressList[0].subThoroughfare != null) {
                            address += addressList[0].subThoroughfare
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (address == "") {
                address = "No Address"
            }

            mMap.addMarker(MarkerOptions().position(p0!!).title(address))
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
