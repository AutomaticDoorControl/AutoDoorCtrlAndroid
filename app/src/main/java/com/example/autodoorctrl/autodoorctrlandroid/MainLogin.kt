package com.example.autodoorctrl.autodoorctrlandroid

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONException
import java.io.*
import okhttp3.OkHttpClient
import okhttp3.Callback
import okhttp3.Response
import okhttp3.Call
import org.json.JSONObject


class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)
    private val  endpoint = "login"
    private lateinit var prefs: SharedPreferences

    //for biometric login
    private lateinit var cancellationSignal: CancellationSignal
    private val authCallback: BiometricPrompt.AuthenticationCallback
    get() = @RequiresApi(Build.VERSION_CODES.P)
    object: BiometricPrompt.AuthenticationCallback(){
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(this@MainLogin, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            println("AUTH SUCCEEDED")
            prefs.getString(getString(R.string.shared_prefs_user_rcsid), null)?.let {
                sendToMap(it)
                return@let
            }
            Toast.makeText(this@MainLogin, getString(R.string.biometric_login_failed_no_user_info), Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar?.hide()

        prefs = this.getPreferences(Context.MODE_PRIVATE)

        prefs.edit().clear().apply()

        prefs.getString(getString(R.string.shared_prefs_user_rcsid), null)?.let {
            sendToMap(it)
        }


        val settingsIcon = findViewById<ImageView>(R.id.gear)
        settingsIcon.setOnClickListener {
            it.startAnimation(opacityClick)
            goToSettings()
        }
        val rcsText = findViewById<EditText>(R.id.et_rcs_id)
        val passwordText = findViewById<EditText>(R.id.et_password)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        loginBtn.setOnClickListener {
            val rcs = rcsText.text.toString()
            val password = passwordText.text.toString()
            val map: HashMap<String, String> = hashMapOf("rcsid" to rcs, "password" to password)
            loginUser(map)
        }

        val register = findViewById<TextView>(R.id.txt_register)
        register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val fingerprint = findViewById<ImageView>(R.id.img_fingerprint)
        fingerprint.setOnClickListener{
            if(!checkBiometricSupport()) return@setOnClickListener
            val prompt = BiometricPrompt.Builder(this)
                .setTitle("Log in with your fingerprint")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
                    println("AUTH CANCELLED")
                })
                .build()

            prompt.authenticate(getCancellationSignal(), mainExecutor, authCallback)
        }
    }

    private fun loginUser(map:HashMap<String,String>) {
        OkHttpRequest.post(endpoint, map, object: Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonArray = JSONObject(response.body?.string())
                    println(jsonArray)
                    if(jsonArray.getString("SESSIONID").compareTo("") != 0) {
                        with(prefs.edit()){
                            putString(getString(R.string.shared_prefs_user_rcsid), map["rcsid"])
                            putString(getString(R.string.shared_prefs_user_session_id), jsonArray.getString("SESSIONID"))
                            putInt(getString(R.string.shared_prefs_user_admin),jsonArray.getInt("admin"))
                            apply()
                        }
                        sendToMap(map["rcsid"])
                    }else {
                        runOnUiThread{
                            Toast.makeText(applicationContext, "User does not exist", Toast.LENGTH_SHORT)
                                .show()
                        }
                        finish()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                runOnUiThread{
                    Toast.makeText(applicationContext, "Failed to connect ", Toast.LENGTH_SHORT)
                        .show()
                }
                println("Request Failure.")
            }
        })
    }

    private fun goToSettings() {
        val intent = Intent(this@MainLogin, Settings::class.java)
        startActivity(intent)
    }

    private fun sendToMap(RCSid: String?) {
        val maps = Intent(this, MapsActivity::class.java)
        maps.putExtra("RCSid", RCSid)
       startActivity(maps)
    }

    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            println("AUTH CANCELLED")
        }
        return cancellationSignal
    }

    private fun checkBiometricSupport(): Boolean{
        val keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if(!keyGuardManager.isKeyguardSecure){
            Toast.makeText(this, getString(R.string.enable_fingerprint), Toast.LENGTH_SHORT).show()
            return false
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, getString(R.string.enable_fingerprint_permission), Toast.LENGTH_SHORT).show()
            return false
        }

        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) true else true
    }
}


