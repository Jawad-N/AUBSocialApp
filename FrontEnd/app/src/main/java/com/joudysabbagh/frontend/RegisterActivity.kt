package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegisterActivity : AppCompatActivity() {
    private var usernameEditText: TextInputLayout? = null
    private var emailEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var registerButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        usernameEditText = findViewById(R.id.txtInptUsername)
        emailEditText = findViewById(R.id.txtInptEmail)
        passwordEditText = findViewById(R.id.txtInptPassword)
        registerButton = findViewById(R.id.btnRegister)
        // Set click listener for register button
        registerButton?.setOnClickListener {
            createUser()
        }
    }
    private fun createUser() {
        val user = User()
        user.username = usernameEditText?.editText?.text.toString()
        user.email= emailEditText?.editText?.text.toString()
        user.password = passwordEditText?.editText?.text.toString()

        // Validate user input
        if (user.username.isNullOrEmpty() || user.email.isNullOrEmpty() || user.password.isNullOrEmpty()) {
            Snackbar.make(
                registerButton as View,
                "Please fill in all fields.",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        //HTTP POST request to register a user on the server
        RetrofitClient.createAPI().registerUser(user)
            //Send & handle the response from the network request asynchronously (different thread than UI thread)
            .enqueue(object : Callback<User> {
                // In case of successful response
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Snackbar.make(
                        registerButton as View,
                        "Account Created.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                // In case of issue with the network request or the server responds with an error status code
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Snackbar.make(
                        registerButton as View,
                        "Could not create account: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        })
    }
}