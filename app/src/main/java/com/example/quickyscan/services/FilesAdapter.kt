package com.example.quickyscan.services

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.quickyscan.FileModel
import com.example.quickyscan.R
import java.io.File


class FilesAdapter(private val fileList: List<FileModel>, private val context: Context) : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var parent: ViewGroup
    private lateinit var onlyName: String


    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.tvFileName)
        val fileCheckBox: CheckBox = itemView.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(par: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(par.context).inflate(R.layout.item_saved_file, par, false)
        sqliteHelper = SQLiteHelper(par.context)
        parent = par

        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileData = fileList[position]
        onlyName = fileData.fileName
        val txt = fileData.fileName + "\n" + fileData.creationDate
        holder.fileName.text = txt
        holder.fileCheckBox.isChecked = fileData.selected

        holder.fileCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the selected state of the file when the CheckBox is checked or unchecked
            fileData.selected = isChecked
        }

        holder.fileName.setOnClickListener {
            val editDialog = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val editDialogView = inflater.inflate(R.layout.update_file_layout, null)
            val contentEditText: EditText = editDialogView.findViewById(R.id.editTextFileContent)
            val fileNameView: TextView = editDialogView.findViewById(R.id.FileNameTextView)

            val model = sqliteHelper.findFile(onlyName)[0]
            contentEditText.setText(model.content)
            fileNameView.text = model.fileName

            editDialog.setView(editDialogView).setTitle("Edit file content").setPositiveButton("Save") { _, _ ->
                val updatedContent = contentEditText.text.toString()
                model.content = updatedContent
                val file = getFileFromDIR(onlyName)
                file?.writeText(updatedContent)
                sqliteHelper.updateFileOnName(model,onlyName)
            }.setNegativeButton("Cancel") {
                    dialog, _ -> dialog.dismiss()
            }.create().show()
        }
    }

    fun getSelectedFiles(): List<FileModel> {
        return fileList.filter { it.selected }
    }

    override fun getItemCount() = fileList.size

    private fun getFileFromDIR(name: String): File? {

        if (parent.context.externalMediaDirs.isNotEmpty()) {
            val mediaDir = parent.context.externalMediaDirs[0]

            val files = mediaDir.listFiles()

            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        if(file.name.toString() == name)
                            return file
                    }else {
                        Log.d("file: ","is not a file")
                    }
                }
            }else {
                Log.d("file list: ","null")
            }
        }

        return null
    }
}