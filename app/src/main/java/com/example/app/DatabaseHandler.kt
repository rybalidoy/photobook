package com.example.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context):
    SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "PhotoDatabase"
        private val TABLE_PHOTO = "PhotoTable"
        private val KEY_ID = "_id"
        private val KEY_DATE = "date"
        private val KEY_DESCRIPTION = "description"
        private val KEY_IMAGE = "_img"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PHOTO_TABLE = ("CREATE TABLE " + TABLE_PHOTO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_IMAGE + " "
                + " BLOB"+ ")")
        db?.execSQL(CREATE_PHOTO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PHOTO")
        onCreate(db)
    }
    fun addRecord(model : Model) : Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_DATE,model.date)
        contentValues.put(KEY_DESCRIPTION,model.description)
        contentValues.put(KEY_IMAGE,model.image)

        val success = db.insert(TABLE_PHOTO,null,contentValues)
        db.close()
        return success
    }
    fun updateRecord(model : Model) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_DATE,model.date)
        contentValues.put(KEY_DESCRIPTION,model.description)
        contentValues.put(KEY_IMAGE,model.image)

        val success = db.update(TABLE_PHOTO,contentValues, KEY_ID + "=" + model.id,null)
        db.close()
        return success
    }
    fun deleteRecord(model : Model) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID,model.id)
        val success = db.delete(TABLE_PHOTO, KEY_ID + "=" + model.id,null)
        db.close()
        return success
    }
    fun viewDatabase() : ArrayList<Model> {
        val list = ArrayList<Model>()
        val selectQuery = "SELECT * FROM $TABLE_PHOTO"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        if (cursor.moveToFirst()){
            do {
                val modelRecord = Model(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE))
                )
                list.add(modelRecord)
            } while (cursor.moveToNext())
        }
        db.close()
        return list
    }
}