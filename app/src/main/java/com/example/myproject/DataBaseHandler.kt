package com.example.myproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getStringOrNull


val DATABASE_NAME = "MyDB"
val COL_ID = "id"
val TABLE_NAME = "Users"
val COL_MSG = "message"
val COL_LAT = "latitude"
val COL_LNG = "longitude"

class DataBaseHandler(var context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1){

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_LAT + " REAL," + COL_LNG + " REAL," + COL_MSG + " TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(user : User){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_MSG,user.msg)
        cv.put(COL_LAT,user.lat)
        cv.put(COL_LNG,user.lng)
        val result = db.insert(TABLE_NAME,null,cv)

        if(result == -1.toLong())
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
    }

    fun listfromdb(): Cursor? {
        val db = this.readableDatabase
        val crs = db.rawQuery("select * from $TABLE_NAME", null)
        return crs
    }

    fun checklocation(): LatlongMsg?{
        val db = this.readableDatabase
        val crs = db.rawQuery("select * from $TABLE_NAME",null,null)
        while (crs.moveToNext()){
            val llm=LatlongMsg()
            llm.longitude = crs.getDoubleOrNull(2)!!
            llm.latitude = crs.getDoubleOrNull(1)!!
            llm.msg = crs.getStringOrNull(3)!!
            return llm
        }
        return null
    }

}