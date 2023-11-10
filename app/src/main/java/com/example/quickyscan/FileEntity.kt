package com.example.quickyscan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
class FileEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var fileName: String,
    var selected: Boolean = false,
    var path: String
)