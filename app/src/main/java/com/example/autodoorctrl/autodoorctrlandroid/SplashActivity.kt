package com.example.autodoorctrl.autodoorctrlandroid
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;

class SplashActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()

    }
}