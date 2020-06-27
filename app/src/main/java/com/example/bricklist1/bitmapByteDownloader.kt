package com.example.bricklist1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class bitmapByteDownloader(): AsyncTask<String, Nothing, ByteArray?>() {
    private var ba = ByteArray(1)
    private fun urlGenerator(type:String,code:String?, color:String?,ext:String):String{
        return  if (color == null){
            "https://img.bricklink.com/$type"+"L/$code.$ext"
        } else{
            "https://img.bricklink.com/$type/$color/$code.$ext"
        }
    }
    private fun downloadImage(type:String,code:String?, color:String?): ByteArray?{
        if (code == null) return null
        var urlS =urlGenerator(type,code,color,"jpg")
        return try {
            var url = URL(urlS)
            var connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)

            if (connection.responseCode==404){
                urlS = urlGenerator(type,code,color,"gif")
                url = URL(urlS)
                connection = url
                    .openConnection() as HttpURLConnection
                connection.setDoInput(true)
            }

            connection.connect()
            val input: InputStream = connection.getInputStream()
            ba = input.readBytes()
            ba
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun doInBackground(vararg params: String?): ByteArray? {
        val res = downloadImage(params[0].toString(),params[1].toString(),params[2].toString())
        return res
    }

    fun show (a:ImageView){
        val tab = this.get()
        if(tab!=null){
            val im = BitmapFactory.decodeByteArray(tab,0,tab.size)
            a.setImageBitmap(im)
        }
    }
}