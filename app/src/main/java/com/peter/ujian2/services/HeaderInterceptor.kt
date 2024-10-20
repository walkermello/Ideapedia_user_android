package com.peter.ujian2.services

import com.peter.ujian2.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}