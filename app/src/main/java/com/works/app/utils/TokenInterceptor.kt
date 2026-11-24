package com.works.app.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(context: Context) : Interceptor {

    private val sharedPreferences = context.getSharedPreferences(
        "app",
        Context.MODE_PRIVATE
    )

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = sharedPreferences.getString("token", null)

        val request = chain.request().newBuilder().apply {

            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }

        }.build()

        return chain.proceed(request)
    }
}