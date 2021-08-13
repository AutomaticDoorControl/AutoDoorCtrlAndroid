package com.example.autodoorctrl.autodoorctrlandroid

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Location
import android.os.Looper
import android.util.Log
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
import android.content.Context
import okhttp3.OkHttpClient
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
//GoogleMap.OnMarkerClickListener
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private val updateInterval = (10 * 1000).toLong()
    private val fastInterval: Long = 2000
    private var mLocationRequest: LocationRequest? = null
    private lateinit var mMap: GoogleMap
    private val  endpoint = "get-doors"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation:LatLng? = null
    private var cameraMoved = false
    private var bluetoothGatt: BluetoothGatt? = null
    private val myLoc =LatLng(42.7287362,-73.6736838)
    private val macAdress ="88:3F:4A:E5:BE:C6"
    private val MY_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    // A service that interacts with the BLE device via the Android BLE API.
    private lateinit var device:BluetoothDevice
    private val TAG = "Bluetooth"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        device = bluetoothAdapter!!.getRemoteDevice(macAdress)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        mMap.setMinZoomPreference(14.0f)
        mMap.setMaxZoomPreference(18.0f)
        mMap.isMyLocationEnabled= true

        //add this here:
        val list: MutableList<LatLng> = ArrayList()

        OkHttpRequest.get(endpoint, object: Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonArray = JSONArray(response.body?.string())
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val latitude = item.getDouble("latitude")
                        val longitude = item.getDouble("longitude")
                        val name = item.getString("name")
                        val tempDoor = LatLng(latitude, longitude)
                        list += tempDoor
                        runOnUiThread {
                            mMap.addMarker(MarkerOptions().position(tempDoor).title(name).snippet("Click to open or close"))
                        }
                    }
                    runOnUiThread{
                        mMap.addMarker(MarkerOptions().position(myLoc).title("ADC").snippet("Click to open or close"))
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
//        mMap.setOnMarkerClickListener(this)
    }
    override fun onStart() {
        super.onStart()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        startLocationUpdates()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode ==  Activity.RESULT_OK)
        {
            super.onActivityResult(requestCode, resultCode, data)
        }
        else
        {
            runOnUiThread{
                Toast.makeText(this,"Bluetooth is required to use this app",Toast.LENGTH_LONG).show()
            }
        }
    }
//    override fun onMarkerClick(p0: Marker?): Boolean
//    {
//        println("Check1")
//        bluetoothGatt = device.connectGatt(this, false,BluetoothLeService(bluetoothGatt,applicationContext).gattCallback)
//        println("Bluetooth gat is $bluetoothGatt")
//        return true
//    }
    override fun onInfoWindowClick(p0: Marker?) {
        Toast.makeText(this, "Info window clicked",
            Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MapsActivity , UnlockActivity::class.java)
        startActivity(intent)
    }
    private fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
            interval=updateInterval
            fastestInterval=fastInterval
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
                onLocationChanged(locationResult!!.lastLocation)
            }
        }
        // 4. add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    //
    private fun onLocationChanged(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        if(!cameraMoved)
        {
            lastLocation=myLocation
            println("Last location is $lastLocation")
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
            cameraMoved=true
        }
        else
        {
            lastLocation=myLocation
        }
    }


    private fun checkPermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                registerLocationListener()
            }
        }
    }

}
