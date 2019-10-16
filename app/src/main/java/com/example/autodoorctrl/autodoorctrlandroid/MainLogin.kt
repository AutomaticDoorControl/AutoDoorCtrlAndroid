package com.example.autodoorctrl.autodoorctrlandroid

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import android.os.CancellationSignal
import android.widget.Button
import java.io.*
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import okhttp3.Callback
import okhttp3.Response
import okhttp3.Call
import android.widget.EditText
import org.json.JSONObject


class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)
    private val cancellationSignal: CancellationSignal = CancellationSignal()
    private var client = OkHttpClient()
    private var request = OkHttpRequest(client)
    private val  url = "http://69.55.54.25:80/api/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_student_login)
        hideNavBar()
        val settingsIcon = findViewById<ImageView>(R.id.gear)
        settingsIcon.setOnClickListener {
            it.startAnimation(opacityClick)
            goToSettings()
        }
        val rcsText = findViewById<EditText>(R.id.RCS_ID)
        val loginBtn = findViewById<Button>(R.id.main_login)
        loginBtn.setOnClickListener {
            val rcs = rcsText.text.toString()
            val map: HashMap<String, String> = hashMapOf("RCSid" to rcs)
            loginUser(map)
        }
    }

    private fun loginUser(map:HashMap<String,String>) {
        request.POST(url,map,object: Callback {
            override fun onResponse(call: Call, response: Response)
            {
                try
                {
                    var jsonArray = JSONObject(response.body?.string())
                    println(jsonArray)
                    if(jsonArray.getString("SESSIONID").compareTo("") != 0)
                        sendToMap(map.get("RCSid"))
                    else {
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
    fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    private fun sendCredentials( RCS:String,password:String){

    }
    private fun goToSettings() {
        val intent = Intent(this@MainLogin, Settings::class.java)
        startActivity(intent)
    }

        fun notifyUser(message: String) {
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
        private fun sendToMap(RCSid: String?) {
            var Maps = Intent(this, MapsActivity::class.java)
            Maps.putExtra("RCSid",RCSid)
            startActivity(Maps)
        }

        @TargetApi(Build.VERSION_CODES.P)
        fun getAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {

            return object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    notifyUser("Authentication error: $errString")
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    notifyUser("Authentication Succeeded")
                    super.onAuthenticationSucceeded(result)
                    val intent = Intent(this@MainLogin , MapsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    private fun getCancellationSignal(): CancellationSignal {

        cancellationSignal.setOnCancelListener(CancellationSignal.OnCancelListener { notifyUser("Cancelled via signal") })
        return cancellationSignal
    }
    @TargetApi(Build.VERSION_CODES.P)
    private fun authenticateUser() {
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Biometric Login")
            .setSubtitle("Authentication is required to continue")
            .setDescription("Use Fingerprint to login to Automatic Door Control.")
            .setNegativeButton("Cancel", this.mainExecutor,
                DialogInterface.OnClickListener { dialogInterface, i -> notifyUser("Authentication cancelled") })
            .build()

        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(),
            getAuthenticationCallback());
    }
}


