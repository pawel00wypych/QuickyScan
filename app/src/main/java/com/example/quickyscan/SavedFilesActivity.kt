package com.example.quickyscan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class SavedFilesActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.files_layout)

        val deleteButton: Button = findViewById(R.id.delete_button)


        val exportButton: Button = findViewById(R.id.export_button)
        val menuButton: ImageButton = findViewById(R.id.show_menu)
        var recyclerView: RecyclerView = findViewById(R.id.rvSavedFiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var filesAdapter = FilesAdapter(getListOfFiles())
        recyclerView.adapter = filesAdapter

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