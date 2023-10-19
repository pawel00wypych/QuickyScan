package com.example.quickyscan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        // Replace this with your logic to fetch a list of saved files
        // Create a list of FileData objects, where FileData is a data class representing file information
        val files = mutableListOf<FileData>()
        files.add(FileData("File 1", true))
        files.add(FileData("File 2", false))
        files.add(FileData("File 3", true))
        files.add(FileData("File 4", false))
        files.add(FileData("File 5", true))
        files.add(FileData("File 6", false))
        files.add(FileData("File 7", true))
        files.add(FileData("File 8", false))
        files.add(FileData("File 9", true))
        files.add(FileData("File 10", false))
        files.add(FileData("File 11", true))
        files.add(FileData("File 12", false))
        files.add(FileData("File 13", true))
        // Add more files as needed
        return files
    }
}