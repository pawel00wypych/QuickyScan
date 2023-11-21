package com.example.quickyscan.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.quickyscan.FileModel
import com.example.quickyscan.services.OCRProcessor
import com.example.quickyscan.R
import com.example.quickyscan.databinding.CameraLayoutBinding
import com.example.quickyscan.services.SQLiteHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var viewBinding: CameraLayoutBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var ocrProcessor: OCRProcessor
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewBinding = CameraLayoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        sqliteHelper = SQLiteHelper(applicationContext)


        val cancelButton: Button = findViewById(R.id.cnclButton)
        val saveButton: Button = findViewById(R.id.svButton)

        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Set up the listeners for take photo button
        saveButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder().build()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        val existingFileNames = getExistingFileNames()

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    if (savedUri != null) {
                        val msg = "Photo capture succeeded: $savedUri"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)

                        val testPath = Uri.fromFile(File("/storage/self/primary/Pictures/CameraX-Image/2222-12-22-22-22-22-222.jpg"))
                        val language = "eng"
                        ocrProcessor = OCRProcessor(this@CameraActivity, assets, savedUri, language)
                        showFileNameDialog(existingFileNames)
                    } else {
                        Log.e(TAG, "Saved URI is null")
                    }
                }
            }
        )
    }

    private fun getExistingFileNames(): List<String> {
        // Get a list of existing file names from the directory where the extracted text files are saved
        val directory = externalMediaDirs.first()
        val fileList = directory.listFiles()
        return fileList?.filter { it.isFile }?.map { it.nameWithoutExtension } ?: emptyList()
    }

    private fun showFileNameDialog(existingFileNames: List<String>) {
        val inputEditText = EditText(this)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter File Name")
            .setView(inputEditText)
            .setPositiveButton("Save") { _, _ ->
                val fileName = inputEditText.text.toString().trim()
                if (fileName.isNotEmpty()) {
                    if (existingFileNames.contains(fileName)) {
                        Toast.makeText(this, "File name already exists. Please provide a different name.", Toast.LENGTH_SHORT).show()
                    } else {
                        launch {
                            ocrCall(fileName)
                        }
                    }
                } else {
                    Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private suspend fun ocrCall(fileName: String) {
        Log.d("OCR", "extracting text..")
        val ret = withContext(Dispatchers.Default) {
            val ret = ocrProcessor.extractText()
            return@withContext ret
        }
        withContext(Dispatchers.Main) {
            Log.d("OCR", "text has been extracted.")
            saveTextToFile(fileName, ret)
        }
    }

    private suspend fun saveTextToFile(fileName: String, text: String) {
        try {
            val path = externalMediaDirs.first()
            Log.d(ContentValues.TAG, "path: $path")

            val outputFile = File(
                path,
                "$fileName.txt"
            )

            withContext(Dispatchers.IO) {

                outputFile.createNewFile()

                val outputStreamWriter = OutputStreamWriter(outputFile.outputStream())
                outputStreamWriter.append(text)
                outputStreamWriter.close()
            }
            Toast.makeText(
                this,
                "Text saved to ${outputFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
            Log.d(ContentValues.TAG, "Text saved to: ${outputFile.absolutePath}")

            val fileModel = FileModel(
                fileName = "$fileName.txt",
                path = outputFile.absolutePath,
                selected = false,
                creationDate = LocalDate.now().toString()
            )
            sqliteHelper.insertFile(fileModel)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).toTypedArray()
    }
}