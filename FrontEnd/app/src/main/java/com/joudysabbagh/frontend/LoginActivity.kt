package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.Authentication
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Token
import com.joudysabbagh.frontend.api.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var loginButton: Button? = null
    private var resetButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameEditText = findViewById(R.id.txtInptUsername)
        passwordEditText = findViewById(R.id.txtInptPassword)
        loginButton = findViewById(R.id.btnLogin)
        resetButton = findViewById(R.id.btnReset)
        // Set click listener for register button
        loginButton?.setOnClickListener {
            authenticateUser()
        }
        resetButton?.setOnClickListener {
            resetPassword()
        }
    }

    private fun authenticateUser(){
        val user = User()
        user.username= usernameEditText?.editText?.text.toString()
        user.password = passwordEditText?.editText?.text.toString()

        //HTTP POST request to authenticate a user on the server
        RetrofitClient.retrofitUserManagement().authenticateUser(user)
            //Send & handle the response from the network request asynchronously (different thread than UI thread)
            .enqueue(object : Callback<Token> {
                // In case of successful response
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        response.body()?.token?.let {
                            // save token to the shared preferences
                            Authentication.saveToken(it)
                            Snackbar.make(
                                loginButton as View,
                                "Logged in successfully.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            onCompleted()
                        }
                    } else {
                        Snackbar.make(
                            loginButton as View,
                            "Failed to login. Please check your credentials.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                // In case of issue with the network request or the server responds with an error status code
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Snackbar.make(
                        loginButton as View,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun onCompleted() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun resetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}