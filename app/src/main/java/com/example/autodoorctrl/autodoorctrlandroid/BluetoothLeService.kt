package com.example.autodoorctrl.autodoorctrlandroid

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import android.os.IBinder
import android.util.Log

class BluetoothLeService(private var bluetoothGatt: BluetoothGatt?,private var context: Context):Service(){
    private val STATE_DISCONNECTED = 0
    private val STATE_CONNECTED = 2
    val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
    val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
    val ACTION_GATT_SERVICES_DISCOVERED =
        "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
    val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
    val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
    private var connectionState = STATE_DISCONNECTED
    private val TAG = BluetoothLeService::class.java.simpleName

    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: " +
                            bluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }
        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                else -> Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }
        // Result of a characteristic read operation
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    println("Yes")
                    broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic)
                }
            }
        }
    }
//    private val gattUpdateReceiver = object : BroadcastReceiver() {
//
//        private lateinit var bluetoothLeService: BluetoothLeService
//
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            when (action){
//                ACTION_GATT_CONNECTED -> {
//                    (context as? Activity)?.invalidateOptionsMenu()
//                }
//                ACTION_GATT_DISCONNECTED -> {
//                    (context as? Activity)?.invalidateOptionsMenu()
//                }
//                ACTION_GATT_SERVICES_DISCOVERED -> {
//                    // Show all the supported services and characteristics on the
//                    // user interface.
//
//                }
//                ACTION_DATA_AVAILABLE -> {
//
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
//                }
//            }
//        }
//    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }
    private fun broadcastUpdate(action: String,characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        val data: ByteArray? = characteristic.value
        if (data?.isNotEmpty() == true) {
            val hexString: String = data.joinToString(separator = " ") {
                String.format("%02X", it)
            }
            println("Data is \"$data\\n$hexString\"")
            intent.putExtra(EXTRA_DATA, "$data\n$hexString")
        }
        context.sendBroadcast(intent)
    }
}