package com.example.quickyscan

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quickyscan.services.FilesDAO

@Database(entities = [FileEntity::class], version = 1)
abstract class DatabaseApp : RoomDatabase(){
    abstract fun filesDao(): FilesDAO
}