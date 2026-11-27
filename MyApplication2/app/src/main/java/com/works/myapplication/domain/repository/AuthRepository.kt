package com.works.myapplication.domain.repository

import com.works.myapplication.domain.model.User
import com.works.myapplication.utils.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
    fun logout()
}