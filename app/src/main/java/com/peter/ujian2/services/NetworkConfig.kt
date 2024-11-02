package com.peter.ujian2.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkConfig {
    fun getRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(HeaderInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://7edc-140-213-190-87.ngrok-free.app/cicool/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getServiceUser() = getRetrofit().create(UserServices::class.java)
}