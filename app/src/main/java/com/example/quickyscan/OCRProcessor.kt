package com.example.quickyscan

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI


class OCRProcessor(private val context: Context, private val imageUri: Uri, private val language: String) {

    fun extractText(onResult: (String) -> Unit) {
        Log.d(TAG, "imageUri: $imageUri")
        val tessBaseApi = TessBaseAPI()
        val dataPath = context.filesDir.path

        tessBaseApi.init("$dataPath/tesseract/", language)
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
        Log.d(TAG, "bitmap: $bitmap")

        tessBaseApi.setImage(bitmap)

        val extractedText = tessBaseApi.utF8Text

        tessBaseApi.end()
        Log.d(TAG, "extractedText: $extractedText")

        onResult(extractedText)
    }
}
