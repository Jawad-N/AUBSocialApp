package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Email
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    private var emailTextView: TextInputLayout? = null
    private var resetButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)
        emailTextView = findViewById(R.id.txtInptEmail)
        resetButton = findViewById(R.id.btnReset)

        resetButton?.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = Email()
        email.email= emailTextView?.editText?.text.toString()

        //HTTP POST request to authenticate a user on the server
        RetrofitClient.retrofitUserManagement().requestPassword(email)
            //Send & handle the response from the network request asynchronously (different thread than UI thread)
            .enqueue(object : Callback<Email> {
                // In case of successful response
                override fun onResponse(call: Call<Email>, response: Response<Email>) {
                    if (response.isSuccessful) {
                        // If response is 200, do this
                        verifyDialog()
                    } else {
                        // If response is not 200
                        Snackbar.make(
                            resetButton as View,
                            "ERROR: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                // In case of issue with the network request or the server responds with an error status code
                override fun onFailure(call: Call<Email>, t: Throwable) {
                    Snackbar.make(
                        resetButton as View,
                        "Could not create account: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun verifyDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_reset, null)
        val codeEditText = dialogView.findViewById<TextInputLayout>(R.id.txtInptCode)
        val passwordEditText = dialogView.findViewById<TextInputLayout>(R.id.txtNewPassword)


        builder.setView(dialogView)
            .setPositiveButton("Verify") { dialog, _ ->
                val code = codeEditText.editText?.text.toString()
                val password = passwordEditText.editText?.text.toString()
                val email = Email()
                email.code = code
                email.password = password
                email.email= emailTextView?.editText?.text.toString()


                // Retrofit API call to verify the code against the server
                RetrofitClient.retrofitUserManagement().resetPassword(email)
                    .enqueue(object : Callback<Email> {
                        // In case of successful response
                        override fun onResponse(call: Call<Email>, response: Response<Email>) {
                            if (response.isSuccessful) {
                                Snackbar.make(
                                    resetButton as View,
                                    "Password reset successfully.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                onCompleted()
                            } else {
                                // If response is not 200
                                Snackbar.make(
                                    resetButton as View,
                                    "Invalid code, could not reset password: ${response.message()}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                        // In case of issue with the network request or the server responds with an error status code
                        override fun onFailure(call: Call<Email>, t: Throwable) {
                            Snackbar.make(
                                dialogView,
                                "An error occured, could not reset password: ${t.message}",
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
