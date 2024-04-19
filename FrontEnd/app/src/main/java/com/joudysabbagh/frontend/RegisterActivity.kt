package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.Authentication
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Email
import com.joudysabbagh.frontend.api.model.Token
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
        RetrofitClient.retrofitUserManagement().registerUser(user)
            //Send & handle the response from the network request asynchronously (different thread than UI thread)
            .enqueue(object : Callback<User> {
                // In case of successful response
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // If response is 200, do this
                        verifyDialog()
                    } else {
                        // If response is not 200
                        Snackbar.make(
                            registerButton as View,
                            "ERROR: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
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
    private fun verifyDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_verify, null)
        val codeEditText = dialogView.findViewById<TextInputLayout>(R.id.txtInptCode)
        val user = User()
        user.username = usernameEditText?.editText?.text.toString()
        user.email= emailEditText?.editText?.text.toString()
        user.password = passwordEditText?.editText?.text.toString()

        builder.setView(dialogView)
            .setPositiveButton("Verify") { dialog, _ ->
                val code = codeEditText.editText?.text.toString()
                val email = Email()
                email.code = code
                // Retrofit API call to verify the code against the server
                RetrofitClient.retrofitUserManagement().verifyUser(emailEditText?.editText?.text.toString(), email)
                    .enqueue(object : Callback<Email> {
                        // In case of successful response
                        override fun onResponse(call: Call<Email>, response: Response<Email>) {
                            if (response.isSuccessful) {
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
                                                        registerButton as View,
                                                        "Account created successfully.",
                                                        Snackbar.LENGTH_LONG
                                                    ).show()
                                                    onCompleted()
                                                }
                                            } else {
                                                Snackbar.make(
                                                    registerButton as View,
                                                    "Failed to create account.",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                        // In case of issue with the network request or the server responds with an error status code
                                        override fun onFailure(call: Call<Token>, t: Throwable) {
                                            Snackbar.make(
                                                registerButton as View,
                                                "ERROR: ${t.message}",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                    })
                            } else {
                                // If response is not 200
                                Snackbar.make(
                                    registerButton as View,
                                    "Invalid code, could not create account: ${response.message()}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                        // In case of issue with the network request or the server responds with an error status code
                        override fun onFailure(call: Call<Email>, t: Throwable) {
                            Snackbar.make(
                                dialogView,
                                "An error occured, could not create account: ${t.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    })
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun onCompleted() {
        val intent = Intent(this, entry::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}