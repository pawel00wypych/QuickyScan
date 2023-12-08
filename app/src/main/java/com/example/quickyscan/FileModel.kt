package com.example.quickyscan

import kotlin.random.Random

class FileModel(
    var id: Int = getAutoId(),
    var fileName: String,
    var selected: Boolean,
    var path: String,
    var content: String,
    var creationDate: String
) {
    companion object{
        fun getAutoId(): Int {
            val random = Random
            return random.nextInt(200)
        }
    }
}