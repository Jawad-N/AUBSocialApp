package com.joudysabbagh.frontend

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddTutoringSessionActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutoring_session)

        titleEditText = findViewById(R.id.session_title_input)
        descriptionEditText = findViewById(R.id.session_description_input)
        priceEditText = findViewById(R.id.session_price_input)
        submitButton = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val price = priceEditText.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty()) {
                // Save the session details to your data source (e.g., database or API)
                saveTutoringSession(title, description, price)

                // Clear the input fields
                titleEditText.text.clear()
                descriptionEditText.text.clear()
                priceEditText.text.clear()

                // Show a success message
                Toast.makeText(this, "Tutoring session added successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Show an error message if any field is empty
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTutoringSession(title: String, description: String, price: String) {
        // Implement your logic to save the tutoring session details to your data source
        // This could involve making an API call or interacting with a database
        // For demonstration purposes, we'll just log the details
        println("Saving tutoring session:")
        println("Title: $title")
        println("Description: $description")
        println("Location: $price")
    }
}