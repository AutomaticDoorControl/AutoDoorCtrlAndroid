package com.example.autodoorctrl.autodoorctrlandroid

import android.Manifest
import android.annotation.TargetApi
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.USE_BIOMETRIC
import androidx.core.app.ActivityCompat
import android.app.KeyguardManager
import android.content.Context


class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login)
        hideNavBar()
        checkBiometricSupport()
        val settingsIcon = findViewById<ImageView>(R.id.gear)
        settingsIcon.setOnClickListener { it.startAnimation(opacityClick) }
    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun notifyUser(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun checkBiometricSupport(): Boolean {

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val packageManager = this.packageManager

        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Lock screen security not enabled in Settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            notifyUser("Fingerprint authentication permission not enabled")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true

    }


/*    @TargetApi(Build.VERSION_CODES.P)
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
*/
}
