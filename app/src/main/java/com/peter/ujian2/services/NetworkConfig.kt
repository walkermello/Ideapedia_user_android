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
        // Setup HTTP logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttp client with logging and authentication header interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                val token = sharedPreferences.getString("bearer_token", null)

                // Menambahkan header Authorization jika token tersedia
                val requestBuilder = chain.request().newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                // Melanjutkan dengan request yang telah dimodifikasi
                chain.proceed(requestBuilder.build())
            }
            .build()

        // Membangun Retrofit dengan OkHttpClient yang telah dikonfigurasi
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Pastikan URL yang sesuai di Constants
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Konverter Gson untuk LocalDateTime
            .build()
    }

    // Fungsi untuk mendapatkan layanan IdeaServices
    fun getIdeaServices(): IdeaServices = getRetrofit().create(IdeaServices::class.java)

    // Fungsi untuk mendapatkan layanan AuthServices
    fun getAuthService(): AuthServices = getRetrofit().create(AuthServices::class.java)
}
