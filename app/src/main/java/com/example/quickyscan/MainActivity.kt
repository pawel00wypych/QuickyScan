package com.example.quickyscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val showMenu: ImageView = findViewById(R.id.show_menu)

        showMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.scan -> {
                        Toast.makeText(this, "Showing scan!", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        true
                    }

                    R.id.files -> {
                        Toast.makeText(this, "Showing files!", Toast.LENGTH_LONG).show()
                        true
                    }

                    else -> false
                }
            }
        }
    }
}