package com.example.autodoorctrl.autodoorctrlandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.Call
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private val UPDATE_INTERVAL = (10 * 1000).toLong()
    private val FASTEST_INTERVAL: Long = 2000
    private var mLocationRequest: LocationRequest? = null
    private lateinit var mMap: GoogleMap
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    private val  url = "http://69.55.54.25:80/api/get-doors"
    private var client = OkHttpClient()
    private var request = OkHttpRequest(client)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation:LatLng? = null
    private var lat = 0.0
    private var lng = 0.0
    private var cameraMoved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        hideNavBar()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync(this)
    }
    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
        mMap.setMinZoomPreference(14.0f)
        mMap.setMaxZoomPreference(18.0f)
        mMap.setMyLocationEnabled(true)

        //add this here:
        var list: MutableList<LatLng> = ArrayList()
        request.GET(url,object: Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    var jsonArray = JSONArray(response.body?.string())
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val latitude = item.getDouble("latitude")
                        val longitude = item.getDouble("longitude")
                        val name  = item.getString("name")
                        val tempDoor = LatLng(latitude,longitude)
                        list.add(tempDoor)
                        runOnUiThread{
                            mMap.addMarker(MarkerOptions().position(tempDoor).title(name).snippet("Click to open or close"))
                        }
                    }
                    println("Last location is $lastLocation")
                    runOnUiThread{
                        println("Last location is $lastLocation")
                        if(lastLocation ==  null)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(list[0]))
                        }
                        else
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                println("Request Failure.")
            }
        }
        )

    }
    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onInfoWindowClick(p0: Marker?) {
        Toast.makeText(this, "Info window clicked",
            Toast.LENGTH_SHORT).show();
        val intent = Intent(this@MapsActivity , UnlockActivity::class.java)
        startActivity(intent)
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RECORD_REQUEST_CODE)
    }

    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setFastestInterval(FASTEST_INTERVAL)
        }

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listener
        registerLocationListener()
    }

    private fun registerLocationListener() {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // 4. add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    //
    private fun onLocationChanged(location: Location) {
        val location = LatLng(location.latitude, location.longitude)
        if(!cameraMoved)
        {
            lastLocation=location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            cameraMoved=true
        }
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION ) {
                registerLocationListener()
            }
        }
    }

}
