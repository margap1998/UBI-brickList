package com.example.bricklist1

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bricklist1.dbhandler.dbHandler
import kotlinx.android.synthetic.main.activity_project_view.*

class projectView : AppCompatActivity() {
    var projName = ""

    val db = dbHandler(this,"",null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_view)
        val str= this.intent.getStringExtra("nameOfProject")
        if(!str.isNullOrBlank()) projName = str
        labTV.text = projName

        setting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
