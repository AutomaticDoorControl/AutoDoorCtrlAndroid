package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class  Login : AppCompatActivity() {
    private val adminBtn:Button = findViewById(R.id.admin_login)
    private val studentBtn:Button = findViewById(R.id.student_login)
    private val btnClick:AlphaAnimation

    init {
        btnClick = AlphaAnimation(1f, 0.8f)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        hideNavBar()
        adminBtn.setOnClickListener {
            it.startAnimation(btnClick)
            sendToLogin()
        }
        studentBtn.setOnClickListener {
            it.startAnimation(btnClick)
            sendToLogin()
        }
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