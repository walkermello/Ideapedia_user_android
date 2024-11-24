package com.peter.ujian2.services

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.peter.ujian2.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NetworkConfig(private val context: Context) {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json: JsonElement, _, _ ->
            try {
                val dateString = json.asString
                // Menggunakan formatter ISO yang lebih fleksibel
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                LocalDateTime.parse(dateString, formatter)
            } catch (e: Exception) {
                e.printStackTrace()
                LocalDateTime.now() // Default to current time if parsing fails
            }
        })
        .create()

    // General Retrofit setup with authentication headers
    private fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttp client with logging and header interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                val token = sharedPreferences.getString("bearer_token", null)

                // Add Bearer Token header if available
                val request = chain.request().newBuilder()
                    .apply {
                        token?.let {
                            addHeader("Authorization", "Bearer $it")
                        }
                    }
                    .build()

                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Function to get IdeaServices (using general Retrofit setup)
    fun getIdeaServices(): IdeaServices = getRetrofit().create(IdeaServices::class.java)

    // Function to get AuthServices (using general Retrofit setup)
    fun getAuthService(): AuthServices = getRetrofit().create(AuthServices::class.java)
}
