package com.example.bricklist1.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bricklist1.dbhandler.dbManager
import org.w3c.dom.Node
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

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
    lateinit var ba:ByteArray
    var c:Context? = null
    lateinit var qwIV:ImageView
    fun update(co:Context){
        val dbM = dbManager(co)
        dbM.updatePart(id,found)
        dbM.insertImage(itemID,colorID,ba)
        dbM.close()
    }
    fun updateView(ib:ByteArray){
        bitmap = BitmapFactory.decodeByteArray(ib,0,ib.size)
        ba = ib
        if (c!=null &&bitmap!=null){
            qwIV.setImageBitmap(bitmap)
            update(c!!)
        }
    }
    fun createView(context: Context): LinearLayout{
        val res = LinearLayout(context)
        if (this.found==this.required) res.setBackgroundColor(Color.LTGRAY)
        else res.background = null
        res.orientation = LinearLayout.HORIZONTAL

        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL

        val tv = TextView(context)
        val textToShow = "${this.name} - ${if (colorID!=1) this.color else ""} [${this.code}]"
        tv.text = textToShow
        col.addView(tv,0)
        val butLL = LinearLayout(context)
        butLL.orientation = LinearLayout.HORIZONTAL
        col.addView(butLL)
        res.addView(col)
        val pIV = ImageView(context)
        qwIV = pIV
        res.addView(pIV,0)

        val tvAm = TextView(context)
        tvAm.text = ("${this.found}/${this.required}")
        butLL.addView(tvAm)

        val bPlus = Button(context)
        bPlus.setOnClickListener {
            if (this.found<this.required) this.found+=1
            tvAm.text = ("${this.found}/${this.required}")
            if (this.found==this.required) res.setBackgroundColor(Color.LTGRAY)
            else res.background = null
        }
        bPlus.text ="+"
        butLL.addView(bPlus,0)

        val bMinus = Button(context)
        bMinus.setOnClickListener {
            if (this.found>0) this.found-=1
            tvAm.text = ("${this.found}/${this.required}")
            if (this.found==this.required) res.setBackgroundColor(Color.LTGRAY)
                else res.background = null
        }
        bMinus.text ="-"
        butLL.addView(bMinus,0)

        pIV.setImageBitmap(bitmap)
        pIV.maxHeight =200
        pIV.maxWidth = 200
        c = context
        return res
    }
}