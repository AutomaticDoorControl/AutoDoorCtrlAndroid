package com.example.autodoorctrl.autodoorctrlandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactFixx : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fixx_dialog)
        supportActionBar?.hide()


        // get reference to button
        val btn = findViewById<Button>(R.id.fixx_send)
        val name= findViewById<EditText>(R.id.name)
//        val email =  findViewById<EditText>(R.id.rpiemail)
        val msg = findViewById<EditText>(R.id.msg_fixx)
        btn.setOnClickListener {
//            val username = name.text.toString()
//            val rpi_email =  email.text.toString()
            val msgStr = msg.text.toString()

            val fixx = "fixx@rpi.edu"
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(fixx))
            email.putExtra(Intent.EXTRA_SUBJECT, "Issue with Automatic Door")
            email.putExtra(Intent.EXTRA_TEXT, msgStr)

            email.type = "message/rfc822"
            startActivity(Intent.createChooser(email, "Choose an Email client :"))
            Log.v("EditText", name.text.toString())
            Toast.makeText(this@ContactFixx, "Sending email to Fixx", Toast.LENGTH_SHORT).show()
        }


    }

}

