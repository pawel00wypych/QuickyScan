package com.example.quickyscan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

class SavedFilesActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.files_layout)
        
        if (allPermissionsGranted()) {
            editFiles()
        } else {
            requestPermissions()
        }

    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                editFiles()
            }
        }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private fun editFiles() {
        
        val recyclerView: RecyclerView = findViewById(R.id.rvSavedFiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val filesAdapter = FilesAdapter(getListOfFiles())
        recyclerView.adapter = filesAdapter
        
        val deleteButton: Button = findViewById(R.id.delete_button)
        val exportButton: Button = findViewById(R.id.export_button)
        val menuButton: ImageButton = findViewById(R.id.show_menu)
        
        exportButton.setOnClickListener {
            exportChosenFiles(filesAdapter)
            val intent = Intent(this, SavedFilesActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            deleteChosenFiles(filesAdapter)
            val intent = Intent(this, SavedFilesActivity::class.java)
            startActivity(intent)
        }


        menuButton.setOnClickListener{
            val popupMenu = PopupMenu(this, it)

            popupMenu.menuInflater.inflate(R.menu.drop_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.scan -> {

                        val intent = Intent(this, CameraActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.files -> {
                        true
                    }
                    else -> false
                }
            }

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception){
                Log.e("Files Activity", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun exportChosenFiles(filesAdapter: FilesAdapter) {
        val selectedFiles = filesAdapter.getSelectedFiles()
        val workbook = XSSFWorkbook()

        if (selectedFiles.isNotEmpty()) {
            for (fileData in selectedFiles) {

                val fileToExport = File(fileData.filePath)
                if (fileToExport.exists() && fileToExport.isFile) {
                    val sheet = workbook.createSheet(fileToExport.name + "_sheet")
                    val text = fileToExport.readText()
                    val rows = text.lines()

                    for ((rowIndex, line) in rows.withIndex()) {
                        val row = sheet.createRow(rowIndex)

                        val cells = line.split("\t") // Split by tab, adjust as needed

                        for ((cellIndex, cellValue) in cells.withIndex()) {
                            val cell = row.createCell(cellIndex)
                            cell.setCellValue(cellValue)
                        }
                    }
                    Log.d("fileToExport.path: ", fileToExport.path)
                    val outputFile = File(fileToExport.path.removeSuffix(".txt")+".xlsx")
                    FileOutputStream(outputFile).use { outputStream ->
                        workbook.write(outputStream)
                    }
                }
            }

            filesAdapter.notifyDataSetChanged()

        } else {
            Log.d("selected files"," empty")
        }
    }

    private fun getListOfFiles(): List<FileData> {


        val filesToReturn = mutableListOf<FileData>()

        if (externalMediaDirs.isNotEmpty()) {
            val mediaDir = externalMediaDirs[0]

            val files = mediaDir.listFiles()

            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        filesToReturn.add(FileData(file.name, filePath = file.path.toString()))
                    }else {
                        Log.d("file: ","is not a file")
                    }
                }
            }else {
                Log.d("file list: ","null")
            }
        }

        return filesToReturn
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteChosenFiles(filesAdapter: FilesAdapter) {

        val selectedFiles = filesAdapter.getSelectedFiles()

        if (selectedFiles.isNotEmpty()) {

            for (fileData in selectedFiles) {

                val fileToDelete = File(fileData.filePath)
                if (fileToDelete.exists() && fileToDelete.isFile) {
                    if (fileToDelete.delete()) {
                        Log.d("file deleted","success")
                    } else {
                        Log.d("file deleted","error")

                    }
                }
            }

            filesAdapter.notifyDataSetChanged()

        } else {
            Log.d("selected files"," empty")
        }
    }
}