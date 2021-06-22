package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import android.widget.Button
import java.io.*
import okhttp3.OkHttpClient
import okhttp3.Callback
import okhttp3.Response
import okhttp3.Call
import android.widget.EditText
import org.json.JSONObject


class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)
    private var client = OkHttpClient()
    private var request = OkHttpRequest(client)
    private val  url = "https://rpiadc.com/api/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        hideNavBar()
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
    }

    private fun loginUser(map:HashMap<String,String>) {
        request.post(url,map,object: Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonArray = JSONObject(response.body?.string())
                    println(jsonArray)
                    if(jsonArray.getString("SESSIONID").compareTo("") != 0)
                        sendToMap(map["RCSid"])
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
    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
}


