package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private var username: EditText? = null
    private var password: EditText? = null
    private var pass: String? = null
    private val adminbtn:Button
    private val studentbtn:Button

    init {
        adminbtn = findViewById<Button>(R.id.admin_login)
        studentbtn = findViewById<Button>(R.id.student_login )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        hideNavBar()
        adminbtn.setOnClickListener { sendToLogin() }
        studentbtn.setOnClickListener { sendToLogin() }
    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, MainLogin::class.java)
        startActivity(loginIntent)
    }

}