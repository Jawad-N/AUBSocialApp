package com.joudysabbagh.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.joudysabbagh.frontend.api.RetrofitClient
import com.joudysabbagh.frontend.api.model.StudyGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ListView
import android.widget.RelativeLayout

class StudyGroupActivity : AppCompatActivity() {
    private lateinit var createGroupButton: Button
    private lateinit var JoinGroupButton: Button
    private lateinit var cancelJoinGroupButton: Button
    private lateinit var studyService: RetrofitClient.StudyService
    private lateinit var popupView: PopupWindow
    private lateinit var listView: ListView
    private lateinit var relativeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studygroup)

        relativeLayout = findViewById(R.id.groupDetailsLayout)
        studyService = RetrofitClient.retrofitStudyGroupManagement()
        listView = findViewById(R.id.studyGroupListView)
        createGroupButton = findViewById(R.id.createGroupButton)
        JoinGroupButton = findViewById(R.id.joinGroupButton)
        cancelJoinGroupButton = findViewById(R.id.backButton)
        val NameGroup : TextView = findViewById(R.id.groupNameTextView)
        val DesGroup : TextView = findViewById(R.id.groupDescriptionTextView)
        val MemGroup : TextView = findViewById(R.id.membersListTextView)
        loadStudyGroups()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedStudyGroup = listView.adapter.getItem(position) as StudyGroup
            relativeLayout.visibility = View.VISIBLE

            NameGroup.text = selectedStudyGroup.name
            DesGroup.text = selectedStudyGroup.description
            MemGroup.text = selectedStudyGroup.members[0]

            JoinGroupButton.setOnClickListener { studyService.addMemberToGroup(selectedStudyGroup) }
            cancelJoinGroupButton.setOnClickListener { relativeLayout.visibility = View.GONE }
        }

        createGroupButton.setOnClickListener { showCreateGroupPopup() }
    }

    private fun loadStudyGroups() {
        studyService.getStudyGroups().enqueue(object : Callback<List<StudyGroup>> {
            override fun onResponse(call: Call<List<StudyGroup>>, response: Response<List<StudyGroup>>) {
                if (response.isSuccessful) {
                    val studyGroups = response.body()
                }
            }
            override fun onFailure(call: Call<List<StudyGroup>>, t: Throwable) {}
        })
    }

    private fun showCreateGroupPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_create_group, null)
        val cancelCreateGroupButton: Button = view.findViewById(R.id.cancelButton)
        val groupNameEditText: EditText = view.findViewById(R.id.groupNameEditText)
        val createButton: Button = view.findViewById(R.id.createButton)

        cancelCreateGroupButton.setOnClickListener { popupView.dismiss() }

        createButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            if (groupName.isNotEmpty()) {
                createStudyGroup(groupName)
                popupView.dismiss()
            }
        }
        popupView = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupView.showAtLocation(view, Gravity.CENTER, 0, 0)

    }

    private fun createStudyGroup(groupName: String) {
        val newStudyGroup = StudyGroup(groupName) // Assuming StudyGroup constructor takes groupName
        studyService.createStudyGroup(newStudyGroup).enqueue(object : Callback<StudyGroup> {
            override fun onResponse(call: Call<StudyGroup>, response: Response<StudyGroup>) {
                if (response.isSuccessful) {
                    loadStudyGroups()
                }
            }
            override fun onFailure(call: Call<StudyGroup>, t: Throwable) {}
        })
    }


}
