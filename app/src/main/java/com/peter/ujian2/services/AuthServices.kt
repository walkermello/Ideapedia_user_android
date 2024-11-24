package com.peter.ujian2.services

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("message")
    val message: String
)

interface AuthServices {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}