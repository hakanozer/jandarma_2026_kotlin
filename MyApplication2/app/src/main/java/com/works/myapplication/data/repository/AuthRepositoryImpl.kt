package com.works.myapplication.data.repository

import com.works.myapplication.data.local.datastore.SessionManager
import com.works.myapplication.data.remote.api.AuthApi
import com.works.myapplication.data.remote.dto.LoginRequest
import com.works.myapplication.data.remote.dto.toDomain
import com.works.myapplication.domain.model.User
import com.works.myapplication.domain.repository.AuthRepository
import com.works.myapplication.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                val meta = body?.meta
                val data = body?.data

                if (meta?.status == 200 && data != null) {
                    val user = data.user.toDomain()
                    sessionManager.saveToken(data.accessToken)
                    sessionManager.saveUser(user)
                    Resource.Success(user)
                } else {
                    Resource.Error(meta?.message ?: "Giriş işlemi başarısız. Lütfen bilgilerinizi kontrol edin.")
                }
            } else {
                Resource.Error("Giriş başarısız! Hata kodu: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("İnternet bağlantınızı veya sunucu erişimini kontrol ediniz.", e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    override fun getCurrentUser(): User? {
        return sessionManager.getUser()
    }

    override fun logout() {
        sessionManager.clearSession()
    }
}