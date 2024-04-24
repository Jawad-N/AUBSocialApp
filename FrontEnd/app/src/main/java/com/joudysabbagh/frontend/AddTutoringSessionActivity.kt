package com.joudysabbagh.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.joudysabbagh.frontend.api.Authentication
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Friend
import com.joudysabbagh.frontend.api.model.TutoringSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            addSession()
        }
    }

    private fun addSession() {
        val session = TutoringSession().apply {
            course_name = titleEditText.text.toString().trim()
            description = descriptionEditText.text.toString().trim()
            price = priceEditText.text.toString().toFloatOrNull()
        }

        RetrofitClient.retrofitCourseAndRoomManagement().addSession(session)
            .enqueue(object : Callback<TutoringSession> {
                override fun onResponse(call: Call<TutoringSession>, response: Response<TutoringSession>) {
                    if (response.isSuccessful) {
                        Snackbar.make(
                            submitButton,
                            "Session added successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        onComplete()

                    } else {
                        Snackbar.make(
                            submitButton,
                            "Failed to add session. ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TutoringSession>, t: Throwable) {
                    Snackbar.make(
                        submitButton,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun onComplete() {
        val intent = Intent(this, TutoringActivity::class.java)
        startActivity(intent)
    }
}
