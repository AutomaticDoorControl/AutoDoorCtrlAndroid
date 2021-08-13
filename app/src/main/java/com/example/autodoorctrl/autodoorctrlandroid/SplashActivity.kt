package com.example.autodoorctrl.autodoorctrlandroid
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity;

class SplashActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val intent = Intent(this, MainLogin::class.java)
        startActivity(intent)
        Handler().postDelayed({ finish() }, 900)
    }
}