package com.example.bricklist1

import android.content.Intent
import android.graphics.BitmapFactory
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
            textOnClick()
        }
    }

    fun textOnClick(){
        val intent = Intent(this, projectView::class.java)
        intent.putExtra("nameOfProject","TEST")
        startActivity(intent)
    }
    fun buttonOnClick()
    {
        val intent = Intent(this, addProject::class.java)
        startActivity(intent)
    }

}
