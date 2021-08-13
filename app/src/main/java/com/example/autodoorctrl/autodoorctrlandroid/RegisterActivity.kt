package com.example.autodoorctrl.autodoorctrlandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        val backToLogin = findViewById<TextView>(R.id.txt_back_to_login)
        backToLogin.setOnClickListener{
            //go back to register
            finish()
        }

        val rcsId = findViewById<EditText>(R.id.et_rcs_id_register)
        val submit = findViewById<Button>(R.id.btn_register)
        submit.setOnClickListener { register(hashMapOf("rcsid" to rcsId.text.toString())) }
    }

    private fun register(map: HashMap<String, String>) {
        OkHttpRequest.post("request_access", map, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonArray = JSONObject(response.body?.string())
                    println(jsonArray)
                    if (jsonArray.getString("SESSIONID").compareTo("") != 0)
//                        sendToMap(map["RCSid"])
                    else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "User does not exist",
                                Toast.LENGTH_SHORT
                            )
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
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to connect ", Toast.LENGTH_SHORT)
                        .show()
                }
                println("Request Failure.")
            }
        })
    }
}