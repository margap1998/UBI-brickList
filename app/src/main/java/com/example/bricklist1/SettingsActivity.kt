package com.example.bricklist1

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        urlPrefixTV.setText(urlStr)
        if ( actualProject.active == 1) {actSwitch.text="Active";actSwitch.setBackgroundColor(Color.GREEN)} else {actSwitch.text="Inactive"; actSwitch.setBackgroundColor(Color.RED)}
        saveSAButton.setOnClickListener {
            urlStr = urlPrefixTV.text.toString()
        }
        actSwitch.setOnClickListener() {
            actualProject.active = kotlin.math.abs(actualProject.active - 1)
            if ( actualProject.active == 1) {actSwitch.text="Active";actSwitch.setBackgroundColor(Color.GREEN)} else {actSwitch.text="Inactive"; actSwitch.setBackgroundColor(Color.RED)}

        }
    }
}