package com.example.bricklist1.dbhandler

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream


fun dbOpener(c: Context,f: SQLiteDatabase.CursorFactory?, name:String): SQLiteDatabase {
    val context = c
    val qIS =context.assets.open(name)
    val fDb = File(context.dataDir,"databases")
    if (!fDb.exists()){
        fDb.mkdir()
    }
    val fFile = File(fDb,name)
    if (!fFile.exists()){
        fFile.createNewFile()
        val fOS = FileOutputStream(fFile)
        qIS.copyTo(fOS)
    }
    return SQLiteDatabase.openDatabase(fFile.absolutePath,f,SQLiteDatabase.OPEN_READWRITE)
}