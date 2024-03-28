package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.joudysabbagh.frontend.databinding.ActivityMainBinding // Import the generated binding class

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Declare a variable to hold the binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate the layout using view binding
        setContentView(binding.root) // Set the root view of the layout

        // Set click listener for the login button
        binding.loginButton.setOnClickListener {
            // Start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the register button
        binding.registerButton.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
