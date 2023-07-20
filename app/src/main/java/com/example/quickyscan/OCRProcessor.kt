package com.example.quickyscan

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class OCRProcessor(private val context: Context, private val assetManager: AssetManager, private val imagePath: String, private val language: String) {

    fun extractText(onResult: (String) -> Unit) {
        Log.d(TAG, "imagePath: " +imagePath)
        val tessBaseApi = TessBaseAPI()
        tessBaseApi.init(getDataPath(language), language)

        val bitmap = BitmapFactory.decodeFile(imagePath)
        tessBaseApi.setImage(bitmap)

        val extractedText = tessBaseApi.utF8Text

        tessBaseApi.end()

        onResult(extractedText)
    }

    private fun getDataPath(language: String): String {
        val dir = File(context.getExternalFilesDir(null), "tesseract")
        if (!dir.exists()) dir.mkdirs()

        val nonAbsolutePath = context.filesDir.path
        val absolutePath = context.filesDir.absolutePath
        val nonAbsolutePathcache = context.cacheDir.path
        Log.d(TAG, "nonAbsolutePath: " +nonAbsolutePath)
        Log.d(TAG, "absolutePath: " +absolutePath)
        Log.d(TAG, "nonAbsolutePathcache: " +nonAbsolutePathcache)

        val trainedDataPath = "assets/tessdata/"

        val trainedData = File(context.getExternalFilesDir(null),"/tesseract/tessdata/")
        if (!trainedData.exists()) trainedData.mkdirs()

        if (!File(trainedDataPath + language +".traineddata").exists()) {
            try {
                val trainedDataFiles = assetManager.list("tessdata")!!
                for (file in trainedDataFiles) {
                    val input = assetManager.open("tessdata/$file")
                    val output = FileOutputStream(trainedDataPath + file)
                    input.copyTo(output)
                    input.close()
                    output.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Log.d(TAG, "getDataPath: " +dir.path)
        return dir.path



    }
}
