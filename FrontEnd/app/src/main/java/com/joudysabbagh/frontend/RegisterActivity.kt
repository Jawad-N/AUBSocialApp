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
    private var passwordEditText: TextInputLayout? = null
    private var emailEditText: TextInputLayout? = null
    private var registerButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        usernameEditText = findViewById(R.id.txtInptUsernameReg)
        emailEditText = findViewById(R.id.txtInptEmailReg)
        passwordEditText = findViewById(R.id.txtInptPasswordReg)
        registerButton = findViewById<Button>(R.id.btnRegister)

        // Set click listener for register button
        registerButton?.setOnClickListener {
            createUser()
        }
    }
    private fun createUser() {
        val user = User()
        user.username = usernameEditText?.editText?.toString()
        user.email = emailEditText?.editText?.toString()
        user.password = passwordEditText?.editText?.toString()

        if (user.username.isNullOrEmpty() || user.email.isNullOrEmpty() || user.password.isNullOrEmpty()) {
            Snackbar.make(
                registerButton as View,
                "Please fill in all fields.",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        RetrofitClient.createAPI().registerUser(user).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Snackbar.make(
                    registerButton as View,
                    "Could not create account: ${t.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (!response.isSuccessful) {
                    Snackbar.make(
                        registerButton as View,
                        "Registration failed: ${response.message()}",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return
                }
                Snackbar.make(
                    registerButton as View,
                    "Account Created.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun onCompleted() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}