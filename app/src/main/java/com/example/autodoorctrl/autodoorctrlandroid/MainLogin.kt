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

import androidx.core.app.ActivityCompat
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import android.os.CancellationSignal;
import android.util.Log


class MainLogin : AppCompatActivity() {
    private val opacityClick: AlphaAnimation = AlphaAnimation(0.8f, 0.4f)
    private val cancellationSignal: CancellationSignal = CancellationSignal()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login)
        hideNavBar()

        val settingsIcon = findViewById<ImageView>(R.id.gear)
        settingsIcon.setOnClickListener { it.startAnimation(opacityClick) }
        val boolean : Boolean = checkBiometricSupport()
        if (boolean){
            authenticateUser()
        }
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
        val fingerprintManager = FingerprintManagerCompat.from(this)

        if(!fingerprintManager.isHardwareDetected){
            notifyUser("Device does not have fingerprint hardware")
            return false
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            notifyUser("SDK version does not support fingerprint authentication")
            return false
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            notifyUser("SDK version does not support biometric prompts")
            print(Build.VERSION.SDK_INT)
            print(Build.VERSION_CODES.P)
            return false
        }



        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Lock screen security not enabled in Settings")
            return false
        }

        if(!fingerprintManager.hasEnrolledFingerprints()){
            notifyUser("No fingerprints registered with device")
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

    @TargetApi(Build.VERSION_CODES.P)
    private fun getAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {

        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                notifyUser("Authentication error: $errString")
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationHelp(
                helpCode: Int,
                helpString: CharSequence
            ) {
                super.onAuthenticationHelp(helpCode, helpString)
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
