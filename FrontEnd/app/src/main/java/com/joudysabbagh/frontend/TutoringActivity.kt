package com.joudysabbagh.frontend

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Room
import com.joudysabbagh.frontend.api.model.TutoringSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutoringActivity : AppCompatActivity() {
    private var txtInptCourseID: TextInputLayout? = null
    private var txtInptCourseDescription: TextInputLayout? = null
    private var txtSessionResult: TextView? = null
    private var txtInptPrice: TextInputLayout? = null
    private var findButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutoring)
        val addSessionButton: Button = findViewById(R.id.add_session_button)
        addSessionButton.setOnClickListener {
            val intent = Intent(this, AddTutoringSessionActivity::class.java)
            startActivity(intent)
        }
        getTutoringSession()
    }
    private fun getTutoringSession() {
        val session = TutoringSession()
        session.courseID= txtInptCourseID?.editText?.text.toString()
        session.description= txtInptCourseDescription?.editText?.text.toString()
        session.price= txtInptPrice?.editText?.text.toString()

        RetrofitClient.retrofitCourseAndRoomManagement().filterSession(session)
            .enqueue(object: Callback<ArrayList<String>> {
                override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {
                    if (response.isSuccessful) {
                        val sessions = response.body() // Assuming the response contains a list of room names
                        if (!sessions.isNullOrEmpty()) {
                            val sessionList = sessions.joinToString("\n") // Join room names into a single string
                            txtSessionResult?.text = sessionList // Update TextView with room names
                        } else {
                            txtSessionResult?.text = "No sessions found" // Display message if no rooms are found
                        }
                    } else {
                        // Handle unsuccessful response
                        Snackbar.make(
                            findButton as View,
                            "Error 1: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                    Snackbar.make(
                        findButton as View,
                        "Error ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })

    }

}