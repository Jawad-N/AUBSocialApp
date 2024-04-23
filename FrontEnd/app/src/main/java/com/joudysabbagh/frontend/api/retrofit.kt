package com.joudysabbagh.frontend.api

import com.joudysabbagh.frontend.api.model.Email
import com.joudysabbagh.frontend.api.model.Friend
import com.joudysabbagh.frontend.api.model.Member
import com.joudysabbagh.frontend.api.model.Room
import com.joudysabbagh.frontend.api.model.Token
import com.joudysabbagh.frontend.api.model.User
import com.joudysabbagh.frontend.api.model.StudyGroup
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.GET

object RetrofitClient {
    // API URL from which we will send and receive packages
    private const val API_URL_UM: String = "http://10.0.2.2:5001"
    private const val API_URL_CRM: String = "http://10.0.2.2:5002"
    private const val API_URL_CM: String = "http://10.0.2.2:5003"
    private const val API_URL_SGM: String = "http://10.0.2.2:5004"

    // Set up details on how to send and receive these data packages

    // USER MANAGEMENT API FUNCTION CALL
    fun retrofitUserManagement(): UserService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL_UM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an implementation of the UserService interface that handles making HTTP requests to the specified base URL
        return retrofit.create(UserService::class.java)
    }

    // COURSE AND ROOM MANAGEMENT API FUNCTION CALL
    fun retrofitCourseAndRoomManagement(): CourseAndRoomService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL_CRM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an implementation of the ChatService interface that handles making HTTP requests to the specified base URL
        return retrofit.create(CourseAndRoomService::class.java)
    }

    // CHAT ROOM MANAGEMENT API FUNCTION CALL
    fun retrofitChatManagement(): ChatService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL_CM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an implementation of the CourseAndRoomService interface that handles making HTTP requests to the specified base URL
        return retrofit.create(ChatService::class.java)
    }

    // STUDY GROUP MANAGEMENT API FUNCTION CALL
    fun retrofitStudyGroupManagement(): StudyService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL_SGM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an implementation of the StudyService interface that handles making HTTP requests to the specified base URL
        return retrofit.create(StudyService::class.java)
    }

    interface UserService  {
        // Registers a new user
        @POST("/user/register")
        fun registerUser(@Body userInfo: User): Call<User>

        // Verifies a user's email
        @POST("/user/verify/{email}")
        fun verifyUser(@Path("email") email: String, @Body codeInfo: Email): Call<Email>

        // Authenticates a user
        @POST("/user/authentication")
        fun authenticateUser(@Body loginInfo: User): Call<Token>

        // Request password for a user
        @POST("user/request_password_reset")
        fun requestPassword(@Body emailInfo: Email) : Call <Email>

        // Reset password for a user
        @POST("user/reset_password")
        fun resetPassword(@Body passwordInfo: Email) : Call <Email>

        @POST("user/add_friend")
        fun addFriend(@Body friendInfo: Friend,
                      @Header("Authorization") authorization: String?) : Call <Friend>
    }
    interface ChatService {
    }
    interface CourseAndRoomService {
        // Filter empty rooms
        @POST("room/find_empty_rooms")
        fun filterRoom(@Body roomInfo: Room) : Call <ArrayList<String>>

    }
    interface StudyService {
        @GET("study/getGroups")
        fun getStudyGroups(): Call<ArrayList<StudyGroup>>

        @POST("study/addGroup")
        fun createStudyGroup(@Body groupInfo: StudyGroup): Call<StudyGroup>

        @POST("study/addMember")
        fun addMemberToGroup(@Body memberInfo: Member): Call<Member>
    }
}
