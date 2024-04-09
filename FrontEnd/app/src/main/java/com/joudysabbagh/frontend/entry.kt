package com.joudysabbagh.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.joudysabbagh.frontend.api.Authentication

class entry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        Authentication.initialize(this)
    }
}