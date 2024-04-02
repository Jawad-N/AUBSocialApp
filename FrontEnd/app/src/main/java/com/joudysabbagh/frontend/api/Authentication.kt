package com.joudysabbagh.frontend.api
import android.content.Context
import android.content.SharedPreferences
object Authentication {
    // create var to store authentication token
    private var token: String? = null

    // create shared preferences instance to store and retrieve token data continuously
    private var preferences: SharedPreferences? = null

    // THIS FUNCTION SETS UP SHARED PREFERENCES IN YOUR APPLICATION AND LOAD AUTHENTICATION TOKEN
    public fun initialize(context: Context) {
        // initialize SharedPreferences with the provided context
        preferences = context.getSharedPreferences("SETTINGS",
            Context.MODE_PRIVATE)
        // load the authentication token from SharedPreferences
        token = preferences?.getString("TOKEN", null)
    }

    // THIS FUNCTION RETRIEVES AUTHENTICATION TOKEN
    public fun getToken(): String? {
        return token
    }

    // THIS FUNCTIONS SAVES AUTHENTICATION TOKEN IN SHARED PREFERENCES
    public fun saveToken(token: String) {
        // Update the token variable
        this.token = token
        // Save the token to SharedPreferences
        preferences?.edit()?.putString("TOKEN", token)?.apply()
    }

    // THIS FUNCTION CLEARS AUTHENTICATION TOKEN FROM SHARED PREFERENCES
    public fun clearToken() {
        // Set the token variable to null
        this.token = null
        // Remove the token from SharedPreferences
        preferences?.edit()?.remove("TOKEN")?.apply()
    }
}
