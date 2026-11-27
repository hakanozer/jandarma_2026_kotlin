package com.works.myapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.works.myapplication.domain.model.User

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("meta") val meta: MetaDto,
    @SerializedName("data") val data: AuthDataDto?
)

data class MetaDto(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class AuthDataDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("user") val user: UserDto
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}