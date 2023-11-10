package com.example.quickyscan

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "quickyscan.db"
        private const val TBL_FILES = "tbl_files"
        private const val ID = "id"
        private const val NAME = "name"
        private const val SELECTED = "selected"
        private const val PATH = "path"
        private const val CREATION_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("Database creation:", "onCreate")
        val createTblFiles = ("CREATE TABLE " + TBL_FILES + "("
                + ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                + SELECTED +" BOOLEAN," + PATH + " TEXT,"
                + CREATION_DATE + "TEXT" + ")")

        db.execSQL(createTblFiles)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_FILES")
        onCreate(db)
    }

    fun insertFile(file: FileModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, file.id)
        contentValues.put(NAME, file.fileName)
        contentValues.put(SELECTED, file.selected)
        contentValues.put(PATH, file.path)
        contentValues.put(CREATION_DATE, file.creationDate)

        val success = db.insert(TBL_FILES, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllFiles(): ArrayList<FileModel> {
        val selectQuery = "SELECT * FROM $TBL_FILES"
        return getFiles(selectQuery)
    }

    @SuppressLint("Range")
    fun findFile(name: String): ArrayList<FileModel>{
        val selectQuery = "SELECT * FROM $TBL_FILES WHERE $NAME LIKE %$name%"
        return getFiles(selectQuery)
    }

    @SuppressLint("Range")
    fun getFiles(query: String): ArrayList<FileModel> {
        val fileList: ArrayList<FileModel> = ArrayList()
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }

        var id: Int
        var name: String
        var selected: String
        var path: String
        var creationDate: String

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                selected = cursor.getString(cursor.getColumnIndex("selected"))
                path = cursor.getString(cursor.getColumnIndex("path"))
                creationDate = cursor.getString(cursor.getColumnIndex("creationDate"))

                val file:  FileModel
                file = if(selected.equals("false")) {
                    FileModel(id, name, false, path, creationDate)
                } else {
                    FileModel(id, name, true, path, creationDate)
                }
                fileList.add(file)
            } while(cursor.moveToNext())
        }

        return fileList
    }

    fun deleteFileByName(name: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, name)
        val success = db.delete(TBL_FILES, "name=$name",null)
        db.close()
        return success
    }
}