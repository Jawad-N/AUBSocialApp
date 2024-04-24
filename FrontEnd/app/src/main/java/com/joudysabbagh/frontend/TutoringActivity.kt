package com.joudysabbagh.frontend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.TutoringSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutoringActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private var sessions: ArrayList<TutoringSession>? = ArrayList()
    private var adapter: TutoringAdapter? = null

    private var findButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutoring)

        listView = findViewById(R.id.listview)
        adapter = TutoringAdapter(layoutInflater, sessions!!)
        listView?.adapter = adapter

        getTutoringSession()

        val addSessionButton: Button = findViewById(R.id.add_session_button)
        addSessionButton.setOnClickListener {
            val intent = Intent(this, AddTutoringSessionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getTutoringSession() {
        RetrofitClient.retrofitCourseAndRoomManagement().filterSession()
            .enqueue(object : Callback<ArrayList<TutoringSession>> {
                override fun onResponse(
                    call: Call<ArrayList<TutoringSession>>,
                    response: Response<ArrayList<TutoringSession>>
                ) {
                    if (response.isSuccessful) {
                        sessions?.clear()
                        sessions?.addAll(response.body() ?: emptyList())
                        adapter?.notifyDataSetChanged()
                    } else {
                        Snackbar.make(
                            findButton as View,
                            "Error: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<TutoringSession>>, t: Throwable) {
                    Snackbar.make(
                        findButton as View,
                        "Error: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    inner class TutoringAdapter(
        private val inflater: LayoutInflater,
        private val dataSource: ArrayList<TutoringSession>
    ) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = convertView
            if (view == null) {
                view = inflater.inflate(R.layout.item_session, parent, false)
            }

            val courseID = view?.findViewById<TextView>(R.id.courseID)
            val description = view?.findViewById<TextView>(R.id.description)
            val price = view?.findViewById<TextView>(R.id.price)

            val session = getItem(position)

            courseID?.text = "Course Id: ${session.courseID}"
            description?.text = "Description: ${session.description}"
            price?.text = "Price: ${session.price}"

            return view!!
        }

        override fun getItem(position: Int): TutoringSession {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return dataSource[position].courseID?.toLong() ?: 0
        }

        override fun getCount(): Int {
            return dataSource.size
        }
    }
}
