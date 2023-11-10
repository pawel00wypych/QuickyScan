package com.example.quickyscan.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickyscan.FileData
import com.example.quickyscan.FileModel
import com.example.quickyscan.services.FilesAdapter
import com.example.quickyscan.R
import com.example.quickyscan.SQLiteHelper
import java.io.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.time.LocalDate

class SavedFilesActivity : AppCompatActivity()  {

    private lateinit var deleteButton: Button
    private lateinit var exportButton: Button
    private lateinit var menuButton: ImageButton
    private lateinit var findButton: ImageButton
    private lateinit var fileToSearch: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var sqliteHelper: SQLiteHelper
    private var filesAdapter: FilesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.files_layout)
        sqliteHelper = SQLiteHelper(this)

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

        initView()
        initRecyclerView()

        exportButton.setOnClickListener {
            filesAdapter?.let { it1 -> exportChosenFiles(it1) }
            initRecyclerView()
        }

        deleteButton.setOnClickListener {
            filesAdapter?.let { it1 -> deleteChosenFiles(it1) }
            initRecyclerView()


//            val builder = AlertDialog.Builder(this)
//            builder.setMessage("Are you sure you want to delete files?")
//            builder.setCancelable(true)
//            builder.setPositiveButton("Yes"){ dialog, _ ->
//                filesAdapter?.let { it1 -> deleteChosenFiles(it1) }
//                initRecyclerView()
//                dialog.dismiss()
//            }
//            builder.setNegativeButton("No"){ dialog, _ ->
//                initRecyclerView()
//                dialog.dismiss()
//            }
//            initRecyclerView()
        }

        findButton.setOnClickListener {
            fileToSearch.setText("")
            initRecyclerView()
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

                    try {
                        val fileModel = FileModel(
                            fileName = fileToExport.name.removeSuffix(".txt") + ".xlsx",
                            path = fileToExport.path,
                            selected = false,
                            creationDate = LocalDate.now().toString()
                        )
                        sqliteHelper.insertFile(fileModel)
                    }catch (e: Exception) {
                        Log.e("insertFile", e.printStackTrace().toString())
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
                val name = fileToDelete.name
                if (fileToDelete.exists() && fileToDelete.isFile) {
                    if (fileToDelete.delete()) {
                        Log.d("file deleted","success")
                        if(sqliteHelper.deleteFileByName(name) <= -1) {
                            Log.e("deleteFileByName","file was not deleted successfully from DB!")
                        }
                    } else {
                        Log.e("file deleted","error")

                    }
                }
            }

            filesAdapter.notifyDataSetChanged()

        } else {
            Log.d("selected files"," empty")
        }
    }

    private fun initView() {

        recyclerView = findViewById(R.id.rvSavedFiles)


        deleteButton = findViewById(R.id.delete_button)
        exportButton = findViewById(R.id.export_button)
        menuButton = findViewById(R.id.show_menu)
        findButton = findViewById(R.id.find_file)
        fileToSearch = findViewById(R.id.search_bar)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        filesAdapter = FilesAdapter(getListOfFiles())
        recyclerView.adapter = filesAdapter
    }

}