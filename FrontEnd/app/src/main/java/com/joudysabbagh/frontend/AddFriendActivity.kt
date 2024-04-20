package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.Authentication
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendActivity : AppCompatActivity() {
    private var usernameTextView: TextInputLayout? = null
    private var addButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriend)

        usernameTextView = findViewById(R.id.txtInptUsername)
        addButton = findViewById(R.id.btnAdd)

        addButton?.setOnClickListener {
            addFriend()
        }
    }

    private fun addFriend() {
        val user = User()
        user.username= usernameTextView?.editText?.text.toString()

        val token = Authentication.getToken()
        Log.d("AddFriendActivity", "Auth token: $token")

        //HTTP POST request to authenticate a user on the server
        RetrofitClient.retrofitUserManagement().addFriend(user, "Bearer ${Authentication.getToken()}")
            //Send & handle the response from the network request asynchronously (different thread than UI thread)
            .enqueue(object : Callback<Any> {
                // In case of successful response
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        Snackbar.make(
                            addButton as View,
                            "Friend added.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        onCompleted()
                    } else {
                        Snackbar.make(
                            addButton as View,
                            "Failed to add friend. ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                // In case of issue with the network request or the server responds with an error status code
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Snackbar.make(
                        addButton as View,
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
}