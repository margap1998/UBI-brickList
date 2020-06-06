package com.example.bricklist1.dbhandler

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dbHandler(context:Context, tableName:String?, factory:SQLiteDatabase.CursorFactory?) :SQLiteOpenHelper(context,DATABASE_NAME,factory,DATABASE_VERSION){
    companion object{
        private val DATABASE_VERSION: Int = 1
        private  val DATABASE_NAME = "BrickList.db"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    fun addProject(name: String)
    {
        TODO()
    }
    fun deactivationProject(name: String)
    {
        
    }

}