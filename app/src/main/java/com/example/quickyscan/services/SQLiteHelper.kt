package com.example.quickyscan.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.quickyscan.FileModel

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
                + CREATION_DATE + " TEXT" + ")")

        db.execSQL(createTblFiles)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_FILES")
        if (db != null) {
            onCreate(db)
        }
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
        val selectQuery = "SELECT * FROM $TBL_FILES WHERE $NAME LIKE '%$name%'"
        return getFiles(selectQuery)
    }

    @SuppressLint("Range", "Recycle")
    fun getFiles(query: String): ArrayList<FileModel> {
        val fileList: ArrayList<FileModel> = ArrayList()
        val db = this.readableDatabase
        Log.d("query ",query)

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

        if(cursor != null && cursor.moveToFirst()){


            val columnIndexId = cursor.getColumnIndex(ID)
            val columnIndexName = cursor.getColumnIndex(NAME)
            val columnIndexSelected = cursor.getColumnIndex(SELECTED)
            val columnIndexPath = cursor.getColumnIndex(PATH)
            val columnIndexCreationDate = cursor.getColumnIndex(CREATION_DATE)

            do {
                if (columnIndexId != -1 &&
                    columnIndexName != -1 &&
                    columnIndexSelected != -1 &&
                    columnIndexPath != -1 &&
                    columnIndexCreationDate != -1) {

                    id = cursor.getInt(columnIndexId)
                    name = cursor.getString(columnIndexName)
                    selected = cursor.getString(columnIndexSelected)
                    path = cursor.getString(columnIndexPath)
                    creationDate = cursor.getString(columnIndexCreationDate)
                    Log.d("name ",name)

                    val file:  FileModel
                    file = if(selected == "0") {
                        FileModel(id, name, false, path, creationDate)
                    } else {
                        FileModel(id, name, true, path, creationDate)
                    }
                    fileList.add(file)
                } else {
                    Log.e("Cursor Error", "One or more column indices are -1")
                }
            } while(cursor.moveToNext())
        }

        return fileList
    }

    fun deleteFileByName(name: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_FILES, "$NAME=?",arrayOf(name))
        db.close()
        return success
    }
}