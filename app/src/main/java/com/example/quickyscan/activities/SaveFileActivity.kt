package com.example.quickyscan.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickyscan.FileModel
import com.example.quickyscan.R
import com.example.quickyscan.services.SQLiteHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SaveFileActivity: AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var saveFileButton: Button
    private lateinit var cancelFileButton: Button
    private lateinit var fileNameEditText: EditText
    private lateinit var fileContentEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.save_file_layout)
        sqliteHelper = SQLiteHelper(applicationContext)
        editFile()

    }

    private fun editFile() {
        initView()
        val existingFileNames = getExistingFileNames()

        saveFileButton.setOnClickListener {
            if (fileNameEditText.text.toString().isNotEmpty()) {
                if (existingFileNames.contains(fileNameEditText.text.toString())) {
                    Toast.makeText(this, "File name already exists. Please provide a different name.", Toast.LENGTH_SHORT).show()
                } else {
                    launch {
                        saveFile()
                    }
                }
            } else {
                Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        cancelFileButton.setOnClickListener {
            finish()
        }
    }


    private suspend fun saveFile() {
        try {

            val fileName  = fileNameEditText.text.toString()
            val fileContent = fileContentEditText.text.toString()

            val path = externalMediaDirs.first()

            val outputFile = File(
                path,
                "$fileName.txt"
            )

            withContext(Dispatchers.IO) {

                outputFile.createNewFile()

                val outputStreamWriter = OutputStreamWriter(outputFile.outputStream())
                outputStreamWriter.append(fileContent)
                outputStreamWriter.close()
            }
            Toast.makeText(
                this,
                "Text saved to ${outputFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
            Log.d(ContentValues.TAG, "Text saved to: ${outputFile.absolutePath}")

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            val fileModel = FileModel(
                fileName = "$fileName.txt",
                path = outputFile.absolutePath,
                selected = false,
                content = fileContent,
                creationDate = LocalDateTime.now().format(formatter).toString()
            )
            sqliteHelper.insertFile(fileModel)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getExistingFileNames(): List<String> {
        // Get a list of existing file names from the directory where the extracted text files are saved
        val directory = externalMediaDirs.first()
        val fileList = directory.listFiles()
        return fileList?.filter { it.isFile }?.map { it.nameWithoutExtension } ?: emptyList()
    }

    private fun initView() {

        saveFileButton = findViewById(R.id.saveFileButton)
        cancelFileButton = findViewById(R.id.cancelFileButton)
        fileNameEditText = findViewById(R.id.editTextFileName)
        fileContentEditText = findViewById(R.id.editTextFileContent)
        fileContentEditText.setText(intent.getStringExtra("ocrText"))
    }


}