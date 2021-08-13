package com.example.autodoorctrl.autodoorctrlandroid

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class UnlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock)
        window.decorView.setBackgroundColor(Color.RED);
        supportActionBar?.hide()
    }
}
