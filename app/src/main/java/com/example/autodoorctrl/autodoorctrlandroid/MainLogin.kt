package com.example.autodoorctrl.autodoorctrlandroid

import android.annotation.TargetApi
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login)
        hideNavBar()

        val settingsIcon = findViewById<ImageView>(R.id.gear)
        settingsIcon.setOnClickListener { it.startAnimation(opacityClick) }
    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }





    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback:BiometricCallback) {
        BiometricPrompt.Builder(context)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButton(negativeButtonText, context.getMainExecutor(), object: DialogInterface.OnClickListener() {
                fun onClick(dialogInterface:DialogInterface, i:Int) {
                    biometricCallback.onAuthenticationCancelled()
                }
            })
            .build()
    }

}
