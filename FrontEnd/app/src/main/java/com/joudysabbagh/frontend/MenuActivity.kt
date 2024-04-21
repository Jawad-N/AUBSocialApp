package com.joudysabbagh.frontend

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView

class MenuActivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView
    private lateinit var listView: ListView
    private lateinit var titleTextView: TextView
    private lateinit var backButton: Button
    private val restaurants = arrayOf("Upper Cafeteria", "OSB Cafeteria", "T marbouta", "Spill Caffe")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Initialize PDFView, ListView, titleTextView, and backButton from layout
        pdfView = findViewById(R.id.pdfView)
        listView = findViewById(R.id.listView)
        titleTextView = findViewById(R.id.titleTextView)
        backButton = findViewById(R.id.backButton)

        // Set adapter for the ListView to display restaurant names
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurants)
        listView.adapter = adapter

        // Set item click listener for the ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            // Load PDF file based on the selected restaurant
            val selectedRestaurant = restaurants[position]
            val pdfFileName = getPDFFileName(selectedRestaurant)
            displayPDF(pdfFileName)
        }

        // Set click listener for the backButton
        backButton.setOnClickListener {
            // Hide PDFView and backButton, show ListView and titleTextView again
            pdfView.visibility = View.GONE
            backButton.visibility = View.GONE
            listView.visibility = View.VISIBLE
            titleTextView.visibility = View.VISIBLE
        }
    }

    private fun getPDFFileName(restaurant: String): String {
        // Define PDF file names for each restaurant (replace with actual file names)
        return when (restaurant) {
            "Upper Cafeteria" -> "upper_cafeteria_menu.pdf"
            "OSB Cafeteria" -> "osb_cafeteria_menu.pdf"
            "T marbouta" -> "t_marbouta_menu.pdf"
            "Spill Caffe" -> "spill_caffe_menu.pdf"
            else -> "default_menu.pdf" // Default file name in case of unknown restaurant
        }
    }

    private fun displayPDF(pdfFileName: String) {
        // Set the PDF file to display in the PDFView
        pdfView.fromAsset(pdfFileName).load()

        // Hide ListView and titleTextView, show PDFView and backButton
        listView.visibility = View.GONE
        titleTextView.visibility = View.GONE
        pdfView.visibility = View.VISIBLE
        backButton.visibility = View.VISIBLE
    }
}
