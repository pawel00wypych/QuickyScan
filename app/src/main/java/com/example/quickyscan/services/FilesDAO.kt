package com.example.quickyscan.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quickyscan.FileEntity

@Dao
interface FilesDAO {
    @Insert
    fun insert(entity: FileEntity)

    @Query("SELECT * FROM files")
    fun getAllEntities(): List<FileEntity>
}