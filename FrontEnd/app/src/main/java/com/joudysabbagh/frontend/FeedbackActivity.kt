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
import com.joudysabbagh.frontend.api.model.CourseFeedback
import com.joudysabbagh.frontend.api.model.TutoringSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private var feedbacks: ArrayList<CourseFeedback>? = ArrayList()
    private var adapter: FeedbackAdapter? = null

    private var findButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        listView = findViewById(R.id.listview)
        adapter = FeedbackAdapter(layoutInflater, feedbacks!!)
        listView?.adapter = adapter

        getCourseFeedback()

        val addSessionButton: Button = findViewById(R.id.add_feedback_button)
        addSessionButton.setOnClickListener {
            val intent = Intent(this, AddCourseFeedbackActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCourseFeedback() {
        RetrofitClient.retrofitCourseAndRoomManagement().filterFeedback()
            .enqueue(object : Callback<ArrayList<CourseFeedback>> {
                override fun onResponse(
                    call: Call<ArrayList<CourseFeedback>>,
                    response: Response<ArrayList<CourseFeedback>>
                ) {
                    if (response.isSuccessful) {
                        feedbacks?.clear()
                        feedbacks?.addAll(response.body() ?: emptyList())
                        adapter?.notifyDataSetChanged()
                    } else {
                        Snackbar.make(
                            findButton as View,
                            "Error: ${response.message()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<CourseFeedback>>, t: Throwable) {
                    Snackbar.make(
                        findButton as View,
                        "Error: ${t.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    inner class FeedbackAdapter(
        private val inflater: LayoutInflater,
        private val dataSource: ArrayList<CourseFeedback>
    ) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = convertView
            if (view == null) {
                view = inflater.inflate(R.layout.item_feedback, parent, false)
            }

            val course_name = view?.findViewById<TextView>(R.id.course_name)
            val course_section = view?.findViewById<TextView>(R.id.course_section)
            val professor = view?.findViewById<TextView>(R.id.professor)

            val feedback = getItem(position)

            course_name?.text = "Course Name: ${feedback.course_name}"
            course_section?.text = "Course Section: ${feedback.course_section}"
            professor?.text = "Professor: ${feedback.professor}"

            return view!!
        }

        override fun getItem(position: Int): CourseFeedback {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return dataSource[position].course_name?.toLong() ?: 0
        }

        override fun getCount(): Int {
            return dataSource.size
        }
    }
}
