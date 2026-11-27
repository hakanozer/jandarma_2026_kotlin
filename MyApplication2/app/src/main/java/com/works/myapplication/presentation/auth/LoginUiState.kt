package com.works.myapplication.presentation.auth

import com.works.myapplication.domain.model.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)