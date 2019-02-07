package com.example.autodoorctrl.autodoorctrlandroid

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private var username: EditText? = null
    private var password: EditText? = null
    private var pass: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        hideNavBar()
//        overridePendingTransition(R.anim.splashenter, R.anim.splashexit)
    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

}