package com.works.myapplication.presentation.profile

import com.works.myapplication.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isDarkMode: Boolean = false
)