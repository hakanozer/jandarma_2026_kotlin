package com.works.myapplication.data.remote.api

import com.works.myapplication.data.remote.dto.LoginRequest
import com.works.myapplication.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}