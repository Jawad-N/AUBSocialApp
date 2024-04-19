package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.joudysabbagh.frontend.api.Authentication

class CatalogActivity : AppCompatActivity() {
    private var logoutButton: Button? = null
    private var tutoringButton: Button? = null
    private var feedbackButton: Button? = null
    private var menuButton: Button? = null
    private var studyGroupButton: Button? = null
    private var studyRoomButton: Button? = null
    private var addFriendButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        Authentication.initialize(this)

        logoutButton = findViewById(R.id.btnLogout)
        tutoringButton = findViewById(R.id.btnTutoring)
        feedbackButton = findViewById(R.id.btnFeedback)
        menuButton = findViewById(R.id.btnMenu)
        studyGroupButton = findViewById(R.id.btnStudyGroup)
        studyRoomButton = findViewById(R.id.btnStudyRoom)
        addFriendButton = findViewById(R.id.btnAddFriend)

        logoutButton?.setOnClickListener {
            logout()
        }

        tutoringButton?.setOnClickListener {
            goToTutoringActivity()
        }

        feedbackButton?.setOnClickListener {
            goToFeedbackActivity()
        }

        menuButton?.setOnClickListener {
            goToMenuActivity()
        }

        studyGroupButton?.setOnClickListener {
            goToStudyGroupActivity()
        }

        studyRoomButton?.setOnClickListener {
            goToStudyRoomActivity()
        }

        addFriendButton?.setOnClickListener {
            goToAddFriendActivity()
        }

    }

    private fun logout() {
        Authentication.clearToken()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun goToTutoringActivity() {
        val intent = Intent(this, TutoringActivity::class.java)
        startActivity(intent)
    }

    private fun goToFeedbackActivity() {
        val intent = Intent(this, FeedbackActivity::class.java)
        startActivity(intent)
    }

    private fun goToMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun goToStudyGroupActivity() {
        val intent = Intent(this, StudyGroupActivity::class.java)
        startActivity(intent)
    }

    private fun goToStudyRoomActivity() {
        val intent = Intent(this, StudyRoomActivity::class.java)
        startActivity(intent)
    }

    private fun goToAddFriendActivity() {
        val intent = Intent(this, AddFriendActivity::class.java)
        startActivity(intent)
    }
}
