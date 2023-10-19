package com.example.quickyscan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilesAdapter(private val fileList: List<FileData>) : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.tvFileName)
        val fileCheckBox: CheckBox = itemView.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_file, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileData = fileList[position]
        holder.fileName.text = fileData.fileName
        holder.fileCheckBox.isChecked = fileData.selected

        holder.fileCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the selected state of the file when the CheckBox is checked or unchecked
            fileData.selected = isChecked
        }
    }

    override fun getItemCount() = fileList.size
}
