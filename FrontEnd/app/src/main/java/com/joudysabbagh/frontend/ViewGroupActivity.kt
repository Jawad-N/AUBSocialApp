package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.joudysabbagh.frontend.api.model.StudyGroup

class ViewGroupActivity : AppCompatActivity() {
    private var createButton: Button? = null
    private var joinButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewgroup)

        createButton = findViewById(R.id.createButton)
        joinButton = findViewById(R.id.joinButton)

        createButton?.setOnClickListener{
            createGroup()
        }

        // TO DO JOIN

    }



    private fun createGroup() {
        val intent = Intent(this, StudyGroupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}