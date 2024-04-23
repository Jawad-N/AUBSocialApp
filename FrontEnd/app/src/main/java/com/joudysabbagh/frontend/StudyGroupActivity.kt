package com.joudysabbagh.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Friend
import com.joudysabbagh.frontend.api.model.StudyGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyGroupActivity : AppCompatActivity() {
    private var groupNameInputLayout: TextInputLayout? = null
    private var txtInptUsername: TextInputLayout? = null
    private var groupDescInputLayout: TextInputLayout? = null
    private var createButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studygroup)

        groupNameInputLayout = findViewById(R.id.groupNameInputLayout)
        txtInptUsername = findViewById(R.id.txtInptUsername)
        groupDescInputLayout = findViewById(R.id.groupDescInputLayout)
        createButton = findViewById(R.id.createButton)

        createButton?.setOnClickListener {
            createGroup()
        }
    }

    private fun createGroup() {
        val group = StudyGroup()

        group.name = groupNameInputLayout?.editText?.text.toString()
        group.description = groupDescInputLayout?.editText?.text.toString()
        group.creator = txtInptUsername?.editText?.text.toString().trim()

        RetrofitClient.retrofitStudyGroupManagement().createStudyGroup(group)
            .enqueue(object : Callback<StudyGroup> {
                override fun onResponse(call: Call<StudyGroup>, response: Response<StudyGroup>) {
                    if (response.isSuccessful) {
                        Snackbar.make(
                            createButton as View,
                            "Group created successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        Snackbar.make(
                            createButton as View,
                            "Failed to create group.${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudyGroup>, t: Throwable) {
                    Snackbar.make(
                        createButton as View,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }
}
