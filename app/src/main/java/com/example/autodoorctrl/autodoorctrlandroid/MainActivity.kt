package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar


class MainActivity : AppCompatActivity() {
    /** Duration of splash wait until brought to Login  */
    private val DURATION: Long = 1000
    var currProgress: Int = 0
    private val progressHandler: Handler = Handler()
    private var completedSplash: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        hideNavBar()

        val progressBar: ProgressBar = findViewById(R.id.splash_progress)
//        overridePendingTransition(R.anim.splashenter, R.anim.splashexit)


        Thread(Runnable {
            while(currProgress < 100) {
                currProgress += 1
                android.os.SystemClock.sleep(50)
                progressHandler.post { progressBar.progress = currProgress }
            }
            completedSplash = true

            if (completedSplash) {
                sendToLogin()
            }



        }).start()


    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, Login::class.java)
        startActivity(loginIntent)
    }
}
