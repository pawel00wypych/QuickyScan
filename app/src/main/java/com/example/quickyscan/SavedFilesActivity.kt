package com.example.quickyscan

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val menuButton: ImageButton = findViewById(R.id.show_menu)
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

        var recyclerView: RecyclerView = findViewById(R.id.rvSavedFiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var filesAdapter = FilesAdapter(getListOfFiles())

        recyclerView.adapter = filesAdapter
    }

    private fun getListOfFiles(): List<FileData> {
        val path = "/storage/self/primary/Android/media/com.example.quickyscan"
        val files = mutableListOf<FileData>()

        val directory = File(path)

        if (directory.exists() && directory.isDirectory) {
            val fileList = directory.listFiles()

            if (fileList != null) {
                val listOfFiles = fileList.filter { it.isFile }.toList()

                for (file in listOfFiles) {
                    println(file.name)
                    files.add(FileData(file.name))
                }
            } else {
                Log.d("getListOfFiles:","Failed to list files in the directory.")
            }
        } else {
            Log.d("getListOfFiles path:","The specified path is not a valid directory.")
        }

        return files
    }
}