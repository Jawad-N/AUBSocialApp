package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.Room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyRoomActivity : AppCompatActivity() {
    private var rooms: ArrayList<Room>? = null
    private var txtInptDay: TextInputLayout? = null
    private var txtInpStart: TextInputLayout? = null
    private var txtInptEnd: TextInputLayout? = null
    private var txtInptBuilding: TextInputLayout? = null
    private var findButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyroom)

        // Initialize TextInputLayout elements
        txtInptDay = findViewById(R.id.txtInptDay)
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
            .enqueue(object: Callback <ArrayList<Room>>{
                override fun onResponse(call: Call<ArrayList<Room>>, response: Response<ArrayList<Room>>) {
                    if (response.isSuccessful) {
                        // Clear the previous list and add all from the response
                        rooms?.clear()
                        val responseBody = response.body()
                        if (responseBody.isNullOrEmpty()) {
                            // Show snack-bar when no rooms are available
                            Snackbar.make(
                                findButton as View,
                                "No room available",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            // Add all the rooms from the response
                            rooms?.addAll(responseBody)
                            // Call onCompleted with the new list
                            onCompleted(responseBody)
                        }
                    } else {
                        // Show snack-bar when there is an error with the response
                        Snackbar.make(
                            findButton as View,
                            "Error 1: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }


                override fun onFailure(call : Call <ArrayList<Room>>, t:Throwable){
                    Snackbar.make(
                        findButton as View,
                        "Error ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })

    }

    private fun onCompleted(rooms: ArrayList<Room>) {
        // Send the room list to the next activity where it will be displayed
        val intent = Intent(this, FilteredRoom::class.java)
        intent.putExtra("filtered_rooms", ArrayList(rooms))
        startActivity(intent)
    }
}
