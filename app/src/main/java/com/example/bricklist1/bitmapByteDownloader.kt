package com.example.bricklist1

import android.os.AsyncTask
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class bitmapByteDownloader(): AsyncTask<String, Nothing, ByteArray?>() {
    private var ba:ByteArray? = ByteArray(1)
    private fun urlGenerator(type:String,code:String?, color:String?,ext:String):String{
        val t = if (type=="P" && color=="0") "PL" else type
        val c = if (color=="0") null else color
        return  if (c == null){
            "https://img.bricklink.com/$t/$code.$ext"
        } else{
            "https://img.bricklink.com/$t/$c/$code.$ext"
        }
    }
    private fun downloadImage(type:String,code:String?, color:String?): ByteArray?{
        var urlS =urlGenerator(type,code,color,"gif")
        Log.i("d",urlS)
        return try {
            var url = URL(urlS)
            var connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)
            Log.i("qw","downloading gif")
            if (connection.responseCode==404){
                urlS = urlGenerator(type,code,color,"jpg")
                Log.i("d",urlS)
                url = URL(urlS)
                connection = url
                    .openConnection() as HttpURLConnection
                connection.setDoInput(true)
                Log.i("qw","downloading jpg")
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
        return downloadImage(params[0].toString(),params[1].toString(),params[2].toString())
    }
}