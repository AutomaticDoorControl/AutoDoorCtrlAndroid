package com.example.autodoorctrl.autodoorctrlandroid

import android.os.Bundle
import android.preference.PreferenceFragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.content.DialogInterface
import android.content.Intent
import android.preference.PreferenceScreen


class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavBar()
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                .add(android.R.id.content, SettingsFragment()).commit()
        }
    }


    class SettingsFragment : PreferenceFragment()  {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_layout)
        }
    }

    private fun hideNavBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
