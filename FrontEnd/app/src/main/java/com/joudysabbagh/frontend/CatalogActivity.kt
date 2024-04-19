package com.joudysabbagh.frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.joudysabbagh.frontend.api.Authentication

class CatalogActivity : AppCompatActivity() {
    private var logoutButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        Authentication.initialize(this)

        logoutButton = findViewById(R.id.btnLogout)

        logoutButton?.setOnClickListener {
            logout()
        }

    }

    private fun logout() {
        Authentication.clearToken()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}