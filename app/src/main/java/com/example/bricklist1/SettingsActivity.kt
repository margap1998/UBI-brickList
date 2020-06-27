package com.example.bricklist1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        urlPrefixTV.setText(urlStr)
        saveSAButton.setOnClickListener {
            urlStr = urlPrefixTV.text.toString()
        }
    }
}