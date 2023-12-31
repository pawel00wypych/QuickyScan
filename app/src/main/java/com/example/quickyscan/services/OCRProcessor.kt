package com.example.quickyscan.services

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class OCRProcessor(private val context: Context, private val assetManager: AssetManager, private val imageUri: Uri, private val language: String) {

    fun extractText(): String {
        val tessBaseApi = TessBaseAPI()
        val path = getDataPath(language)

        tessBaseApi.init(path, language)
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
        tessBaseApi.setImage(bitmap)
        val extractedText = tessBaseApi.utF8Text
        tessBaseApi.end()

        Log.d(TAG, "extractedText: $extractedText")
        return extractedText
    }

    private fun getDataPath(language: String): String {
        val dir = File(context.getExternalFilesDir(null), "tesseract")
        if (!dir.exists()) dir.mkdirs()

        val trainedDataPath = File(dir, "tessdata")
        if (!trainedDataPath.exists()) trainedDataPath.mkdirs()

        val trainedDataFilePath = File(trainedDataPath, "$language.traineddata")
        if (!trainedDataFilePath.exists()) {
            try {
                val trainedDataFiles = assetManager.list("tessdata")!!
                for (file in trainedDataFiles) {
                    val input = assetManager.open("tessdata/$file")
                    val output = FileOutputStream(File(trainedDataPath, file))
                    input.copyTo(output)
                    input.close()
                    output.close()
                }
                val tessDataFiles = trainedDataPath.listFiles()
                for (file in tessDataFiles!!) {
                    Log.d(TAG, "TessData file: ${file.name}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return dir.path
    }
}
