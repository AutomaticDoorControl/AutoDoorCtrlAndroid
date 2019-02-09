package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.animation.AnimationUtils

internal class BouncingInterpolator(amplitude: Double, frequency: Double) : android.view.animation.Interpolator {
    private var mAmplitude = 1.0
    private var mFrequency = 10.0

    init {
        mAmplitude = amplitude
        mFrequency = frequency
    }

    override fun getInterpolation(time: Float): Float {
        return (-1.0 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1).toFloat()
    }
}

class  Login : AppCompatActivity() {
    private val btnClick:AlphaAnimation = AlphaAnimation(1f, 0.6f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val adminBtn = findViewById<Button>(R.id.admin_login)
        val studentBtn = findViewById<Button>(R.id.student_login)
        val animScale = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_anim)


        hideNavBar()
        adminBtn.setOnClickListener {
            val interpolator = BouncingInterpolator(0.2, 20.toDouble())
            animScale.interpolator = interpolator
            it.startAnimation(animScale)
            sendToLogin()
        }
        studentBtn.setOnClickListener {
            val interpolator = BouncingInterpolator(0.2, 20.toDouble())
            animScale.interpolator = interpolator
            it.startAnimation(animScale)
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