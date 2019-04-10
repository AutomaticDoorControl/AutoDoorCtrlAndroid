package com.example.autodoorctrl.autodoorctrlandroid

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent



class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login)
        hideNavBar()

        val settingsIcon = findViewById<ImageView>(R.id.gear)
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

    fun goToSettings() {
        val intent = Intent(this@MainLogin, Settings::class.java)
        startActivity(intent)
    }

}


