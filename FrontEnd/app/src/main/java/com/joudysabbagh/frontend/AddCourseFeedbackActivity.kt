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
import com.joudysabbagh.frontend.api.model.CourseFeedback
import com.joudysabbagh.frontend.api.model.Friend
import com.joudysabbagh.frontend.api.model.TutoringSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCourseFeedbackActivity : AppCompatActivity() {
    private lateinit var courseNameEditText: EditText
    private lateinit var courseSectionEditText: EditText
    private lateinit var professorEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course_feedback)

        courseNameEditText = findViewById(R.id.course_title_input)
        courseSectionEditText = findViewById(R.id.course_section_input)
        professorEditText = findViewById(R.id.course_professor_input)

        submitButton = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            addFeedback()
        }
    }

    private fun addFeedback() {
        val feedback = CourseFeedback().apply {
            course_name = courseNameEditText.text.toString().trim()
            course_section = courseSectionEditText.text.toString().trim()
            content = professorEditText.text.toString().trim()
        }

        RetrofitClient.retrofitCourseAndRoomManagement().addFeedback(feedback)
            .enqueue(object : Callback<CourseFeedback> {
                override fun onResponse(call: Call<CourseFeedback>, response: Response<CourseFeedback>) {
                    if (response.isSuccessful) {
                        Snackbar.make(
                            submitButton,
                            "Feedback added successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        onComplete()

                    } else {
                        Snackbar.make(
                            submitButton,
                            "Failed to add feedback. ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CourseFeedback>, t: Throwable) {
                    Snackbar.make(
                        submitButton,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun onComplete() {
        val intent = Intent(this, FeedbackActivity::class.java)
        startActivity(intent)
    }
}
