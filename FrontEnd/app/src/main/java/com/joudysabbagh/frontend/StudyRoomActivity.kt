package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyRoomActivity : AppCompatActivity() {
    // var for room result search
    private var txtInptDay: TextInputLayout? = null
    private var txtRoomResult: TextView? = null
    private var txtInpStart: TextInputLayout? = null
    private var txtInptEnd: TextInputLayout? = null
    private var txtInptBuilding: TextInputLayout? = null
    private var findButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyroom)

        // Initialize TextInputLayout elements
        txtInptDay = findViewById(R.id.txtInptDay)
        txtRoomResult = findViewById(R.id.txtRoomResult)
        txtInpStart = findViewById(R.id.txtInpStart)
        txtInptEnd = findViewById(R.id.txtInptEnd)
        txtInptBuilding = findViewById(R.id.txtInptBuilding)
        findButton = findViewById(R.id.btnFind)

        findButton?.setOnClickListener {
            findRoom()
        }

    }

    private fun findRoom() {
        val room = Room()
        room.building= txtInptBuilding?.editText?.text.toString()
        room.day= txtInptDay?.editText?.text.toString()
        room.start_time= txtInpStart?.editText?.text.toString()
        room.end_time= txtInptEnd?.editText?.text.toString()

        RetrofitClient.retrofitCourseAndRoomManagement().filterRoom(room)
            .enqueue(object: Callback <ArrayList<String>>{
                override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {
                    if (response.isSuccessful) {
                        val rooms = response.body() // Assuming the response contains a list of room names
                        if (!rooms.isNullOrEmpty()) {
                            val roomList = rooms.joinToString("\n") // Join room names into a single string
                            txtRoomResult?.text = roomList // Update TextView with room names
                        } else {
                            txtRoomResult?.text = "No rooms found" // Display message if no rooms are found
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
