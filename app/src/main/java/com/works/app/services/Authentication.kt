package com.works.app.services

import com.works.app.models.LoginReponse
import com.works.app.models.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Authentication {

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest) : Call<LoginReponse>

    @GET("profile/me")
    fun profileMe() : Call<Any>

    @GET("auth/logout")
    fun logout() : Call<Any>

}