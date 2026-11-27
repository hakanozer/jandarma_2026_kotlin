package com.works.myapplication.presentation.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.works.myapplication.data.local.datastore.SessionManager
import com.works.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = authRepository.getCurrentUser()
        val isDarkMode = sessionManager.isDarkMode()
        _uiState.update {
            it.copy(
                user = user,
                isDarkMode = isDarkMode
            )
        }
    }

    fun setDarkMode(enabled: Boolean) {
        sessionManager.setDarkMode(enabled)
        _uiState.update { it.copy(isDarkMode = enabled) }

        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun logout() {
        authRepository.logout()
    }
}