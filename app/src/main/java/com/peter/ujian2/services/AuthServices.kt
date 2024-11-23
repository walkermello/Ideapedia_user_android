package com.peter.ujian2.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val message: String)

interface AuthServices {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}