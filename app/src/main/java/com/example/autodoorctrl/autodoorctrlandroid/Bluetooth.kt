package com.example.autodoorctrl.autodoorctrlandroid

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState







class Bluetooth:AppCompatActivity(){
    private var bluetooth: BluetoothSPP? = BluetoothSPP(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetooth?.setBluetoothStateListener(
            fun (state:Int) {
                when(state)
                {
                    BluetoothState.STATE_CONNECTED->Toast.makeText(applicationContext, "Connected to the Arduino", Toast.LENGTH_SHORT)
                        .show()
                    BluetoothState.STATE_CONNECTING->Toast.makeText(applicationContext, "Connecting to the Arduino", Toast.LENGTH_SHORT)
                        .show()
                    BluetoothState.STATE_LISTEN->Toast.makeText(applicationContext, "Waiting for connection to the Arduino", Toast.LENGTH_SHORT)
                        .show()
                    BluetoothState.STATE_NONE->Toast.makeText(applicationContext, "No connection", Toast.LENGTH_SHORT)
                        .show()
                }
        }
        )
    }
    public override fun onStart() {
        super.onStart()
        if (bluetooth?.isBluetoothEnabled!!) {
            bluetooth?.enable()
        } else {
            if (bluetooth?.isServiceAvailable!!) {
                bluetooth?.setupService()
                bluetooth?.startService(BluetoothState.DEVICE_OTHER)
            }
        }
    }
}
