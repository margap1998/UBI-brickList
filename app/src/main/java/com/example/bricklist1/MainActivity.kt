package com.example.bricklist1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addButton.setOnClickListener {
            buttonOnClick()
        }
        testText.setOnClickListener{
            val intent = Intent(this, projectView::class.java)
            startActivity(intent)
        }
    }


    fun buttonOnClick()
    {
        var ap = addProject()
        val intent = Intent(this, ap.javaClass)
        startActivity(intent)
    }

}
