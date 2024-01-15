package com.example.quickyscan.services

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class OCRProcessor(private val context: Context,
                   private val assetManager: AssetManager,
                   private val imageUri: Uri,
                   private val language: String) {

    fun extractText(): String {

        val imagePath = getRealPathFromUri()
        val dir = imagePath?.let { extractDirectoryPath(it) }
        val newFilePath = "$dir/new_file_name.jpg"
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))

        val scaledBitmap = resizeBitmap(bitmap, 2000)
        val file = File(newFilePath)
        val outputStream = FileOutputStream(file)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        triggerMediaScan(newFilePath)
        Log.d(TAG, "newFilePath: $newFilePath")

        val tessBaseApi = TessBaseAPI()
        val path = getDataPath(language)
        tessBaseApi.init(path, language)
        tessBaseApi.setImage(scaledBitmap)
        val extractedText = tessBaseApi.utF8Text
        tessBaseApi.end()

        Log.d(TAG, "extractedText: $extractedText")
        return extractedText
    }

    private fun triggerMediaScan(filePath: String) {
        // Send a broadcast to the media scanner to scan the file
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(File(filePath))
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
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

    fun resizeBitmap(bitmap: Bitmap, targetLongerSide: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratio = targetLongerSide.toFloat() / Math.max(width, height).toFloat()
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        val matrix = Matrix()
        matrix.postRotate(90F)
        matrix.postScale(ratio, ratio)
        var resizedBitmap: Bitmap

            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)

            if (resizedBitmap.width != newWidth || resizedBitmap.height != newHeight) {

                Log.d(TAG, "createScaledBitmap:  newHeight  $newHeight  newWidth $newWidth")

                return Bitmap.createScaledBitmap(resizedBitmap, newHeight, newWidth, true)
            }



        return resizedBitmap
    }

    fun extractDirectoryPath(filePath: String): String {
        val file = File(filePath)
        return file.parent ?: ""
    }

    fun getRealPathFromUri(): String? {
        val contentResolver = context.contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(imageUri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        } else {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(imageUri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }

            val path = imageUri.path
            if (path != null) {
                return if (File(path).exists()) path else null
            }
        }

        return null
    }
}
