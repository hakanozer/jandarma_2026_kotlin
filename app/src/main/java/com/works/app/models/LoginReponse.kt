package com.works.app.models

data class LoginReponse (
    val meta: Meta,
    val data: UserData
)

data class UserData (
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val user: User
)

data class User (
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val remember_token: Any? = null,
    val created_at: String,
    val updated_at: String
)

data class Meta (
    val status: Long,
    val message: String
)
