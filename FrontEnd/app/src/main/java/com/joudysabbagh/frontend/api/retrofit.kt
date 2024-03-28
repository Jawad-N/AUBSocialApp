package com.joudysabbagh.frontend.api

import com.joudysabbagh.frontend.api.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

object RetrofitClient {
    // API URL from which we will send and receive packages
    private const val API_URL: String = "http://10.0.2.2:5000"

    // Set up details on how to send and receive these data packages
    fun createAPI(): ApiService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an instance of our User Management API interface.
        return retrofit.create(ApiService::class.java)
    }

    interface ApiService {
        // Registers a new user
        @POST("/user/register")
        fun registerUser(@Body userInfo: User): Call<User>

        // Verifies a user's email
        @POST("/user/verify/{email}")
        fun verifyUser(@Path("email") email: String, @Body codeInfo: Map<String, String>): Call<VerificationResponse>

        // Authenticates a user
        @POST("/user/authentication")
        fun authenticateUser(@Body loginInfo: User): Call<AuthenticationResponse>
    }
}

// You might need to create data classes to represent the request bodies and the responses. For example:
data class UserResponse(val redirect: String)
data class VerificationResponse(val token: String?)
data class AuthenticationResponse(val token: String)
