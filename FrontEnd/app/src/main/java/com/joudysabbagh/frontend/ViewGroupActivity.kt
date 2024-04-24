package com.joudysabbagh.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.joudysabbagh.frontend.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson  // For JSON parsing
import org.json.JSONObject


class ViewGroupActivity : AppCompatActivity() {

    private var createButton: Button? = null
    private var joinButton: Button? = null
    private var groupList: ListView? = null

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
            .enqueue(object: Callback <Any>{
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        val studyGroups = response.body()
                        if (studyGroups != null) {
                            val jsonObject = try {
                                JSONObject(Gson().toJson(studyGroups))  // Parse response as JSONObject
                            } catch (e: Exception) {
                                // Handle parsing exception
                                return
                            }

                            val data = jsonObject.optJSONArray("data")  // Get the "data" array
                            if (data != null && data.length() > 0) {
                                val groupNames = mutableListOf<String>()
                                val groupDesc = mutableListOf<String>()
                                for (i in 0 until data.length()) {
                                    val groupObject = data.optJSONObject(i)  // Get each group object
                                    if (groupObject != null) {
                                        val name = groupObject.optString("group_name")  // Extract the name
                                        val description = groupObject.optString("description")  // Extract the name
                                        if (name.isNotEmpty()) {
                                            groupNames.add(name)
                                        }
                                        if (description.isNotEmpty()) {
                                            groupDesc.add(description)
                                        }
                                    }
                                }
                                val adapter = GroupListAdapter(this@ViewGroupActivity, groupNames, groupDesc)
                                groupList?.adapter = adapter

                            }   else {
                                    groupList?.adapter = ArrayAdapter<String>(this@ViewGroupActivity, android.R.layout.simple_list_item_1, arrayOf("No Groups Yet"))
                            }
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

                override fun onFailure(call: Call<Any>, t: Throwable) {
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

class GroupListAdapter(context: Context, private val groupNames: List<String>, private val groupDescriptions: List<String>) : BaseAdapter() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = groupNames.size

    override fun getItem(position: Int): Any = groupNames[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.group_item, parent, false)
        val groupNameTextView = view.findViewById<TextView>(R.id.groupNameTextView)
        val groupDescriptionTextView = view.findViewById<TextView>(R.id.groupDescriptionTextView)

        groupNameTextView.text = groupNames[position]
        groupDescriptionTextView.text = groupDescriptions[position]

        return view
    }
}
