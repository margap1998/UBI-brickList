package com.example.bricklist1.model

import android.content.Context
import android.graphics.Bitmap
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bricklist1.bitmapByteDownloader
import org.w3c.dom.Node
class Part{
    var typeID = 2
    var code:String =""
    var required:Int = 1
    var found:Int=0
    var itemtype:String = ""
    var id:Int =0
    var color:String = ""
    var colorID = 0
    var extra = 0
    var name =""
    var bitmap:Bitmap? = null
    var itemID = 0


    fun createView(context: Context): LinearLayout{
        val res = LinearLayout(context)
        res.orientation = LinearLayout.HORIZONTAL

        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL

        val butLL = LinearLayout(context)
        butLL.orientation = LinearLayout.HORIZONTAL
        col.addView(butLL)
        res.addView(col)
        val pIV = ImageView(context)
        res.addView(pIV,0)
        val tv = TextView(context)
        val textToShow = "${this.name} ${this.color} [${this.code}]"
        tv.text = textToShow
        col.addView(tv)

        val tvAm = TextView(context)
        tvAm.text = ("${this.found}/${this.required}")
        butLL.addView(tvAm)

        val bPlus = Button(context)
        bPlus.setOnClickListener {
            if (this.found<this.required) this.found+=1
        }
        bPlus.text ="+"
        butLL.addView(bPlus,0)

        val bMinus = Button(context)
        bMinus.setOnClickListener {
            if (this.found>0) this.found-=1
        }
        bMinus.text ="-"
        butLL.addView(bMinus,0)

        pIV.setImageBitmap(bitmap)
        return res
    }
}