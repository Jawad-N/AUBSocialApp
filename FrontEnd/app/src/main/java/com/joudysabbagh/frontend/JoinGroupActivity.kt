package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Member
import com.joudysabbagh.frontend.api.model.StudyGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinGroupActivity : AppCompatActivity() {
    private var groupNameInputLayout: TextInputLayout? = null
    private var txtInptUsername: TextInputLayout? = null
    private var joinButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingroup)

        groupNameInputLayout = findViewById(R.id.groupNameInputLayout)
        txtInptUsername = findViewById(R.id.txtInptUsername)

        joinButton = findViewById(R.id.joinButton)

        joinButton?.setOnClickListener {
            joinGroup()
        }
    }

    private fun joinGroup() {
        val member = Member()

        member.member = txtInptUsername?.editText?.text.toString()
        member.group = groupNameInputLayout?.editText?.text.toString().trim()

        RetrofitClient.retrofitStudyGroupManagement().addMemberToGroup(member)
            .enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        Snackbar.make(
                            joinButton as View,
                            "Group joined successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        onComplete()
                    } else {
                        Snackbar.make(
                            joinButton as View,
                            "Failed to join group.${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Snackbar.make(
                        joinButton as View,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun onComplete() {
        val intent = Intent(this, ViewGroupActivity::class.java)
        startActivity(intent)
    }
}