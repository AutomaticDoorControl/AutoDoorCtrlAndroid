package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.animation.AnimationUtils

class  Login : AppCompatActivity() {
    private val opacityClick:AlphaAnimation = AlphaAnimation(0.8f, 0.4f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val adminBtn = findViewById<Button>(R.id.admin_login)
        val studentBtn = findViewById<Button>(R.id.student_login)
        val settingsIcon = findViewById<ImageView>(R.id.gear)

        hideNavBar()
        adminBtn.setOnClickListener {
//            it.startAnimation(animScale)
            Handler().postDelayed({ sendToLogin() }, 100)
        }
        studentBtn.setOnClickListener {
//            it.startAnimation(animScale)
            Handler().postDelayed({ sendToLogin() }, 100)
        }

        settingsIcon.setOnClickListener {
            it.startAnimation(opacityClick)
            goToSettings()

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

    fun goToSettings() {
        val intent = Intent(this@Login, Settings::class.java)
        startActivity(intent)
    }

}