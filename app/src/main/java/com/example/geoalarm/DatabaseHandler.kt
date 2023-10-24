package com.example.geoalarm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

const val DATABASE_NAME = "MyDB"
const val TABLE_NAME = "Users"
const val COL_MSG = "message"
const val COL_LAT = "latitude"
const val COL_LNG = "longitude"

class DatabaseHandler (private var context: Context) : SQLiteOpenHelper (context , DATABASE_NAME , null , 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COL_LAT REAL, $COL_LNG REAL, $COL_MSG TEXT,PRIMARY KEY ($COL_LAT,$COL_LNG,$COL_MSG));")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(message: Item) {
        val db = this.writableDatabase
        val cv = ContentValues()
        Log.d("checkcheck", "insertData: ${message.message}")
        cv.put(COL_MSG,message.message)
        cv.put(COL_LAT,message.latitude)
        cv.put(COL_LNG,message.longitude)

        val insertionResult = db.insert(TABLE_NAME,null,cv)

        if (insertionResult == (-1).toLong()){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadListfromDB(){
        val db = this.readableDatabase
        val crs = db.rawQuery("select * from $TABLE_NAME", null)

        itemList.clear()
        while (crs.moveToNext()){
            val message = (crs.getString(crs.getColumnIndexOrThrow(COL_MSG)))
            val latitude = crs.getDouble(crs.getColumnIndexOrThrow(COL_LAT))
            val longitude = crs.getDouble(crs.getColumnIndexOrThrow(COL_LNG))

            itemList.add(Item(message,latitude,longitude))
        }
        crs.close()
    }

    fun deletefromDB(message : String , latitude : Double , longitude : Double) {
        val selectionArgs = arrayOf(message,latitude.toString(),longitude.toString())
        val db = this.writableDatabase
        db.delete(TABLE_NAME,"$COL_MSG = ? AND $COL_LAT = ? AND $COL_LNG = ? ",selectionArgs)
    }
}