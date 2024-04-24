package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.StudyGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewGroupActivity : AppCompatActivity() {

    private var createButton: Button? = null
    private var joinButton: Button? = null
    private lateinit var groupList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewgroup)

        createButton = findViewById(R.id.createButton)
        joinButton = findViewById(R.id.joinButton)
        groupList = findViewById(R.id.groupList)

        createButton?.setOnClickListener{
            createGroup()
        }

        listGroups()

        joinButton?.setOnClickListener{
            joinGroup()
        }

    }


    private fun listGroups() {
        RetrofitClient.retrofitStudyGroupManagement().getStudyGroups()
            .enqueue(object: Callback <List<StudyGroup>>{
                override fun onResponse(call: Call<List<StudyGroup>>, response: Response<List<StudyGroup>>) {
                    if (response.isSuccessful) {
                        val studyGroups = response.body()
                        if (!studyGroups.isNullOrEmpty()) {
                            val groupNames = studyGroups.map { it.name }
                            groupList.adapter = ArrayAdapter<String>(this@ViewGroupActivity, android.R.layout.simple_list_item_1, groupNames)
                        } else {
                            Snackbar.make(
                                createButton as View,
                                "Failed to create group.${response.message()}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response
                        Snackbar.make(
                            joinButton as View,
                            "Error 1: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<StudyGroup>>, t: Throwable) {
                    Snackbar.make(
                        createButton as View,
                        "ERROR: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }
    private fun joinGroup() {
        val intent = Intent(this, JoinGroupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun createGroup() {
        val intent = Intent(this, StudyGroupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}