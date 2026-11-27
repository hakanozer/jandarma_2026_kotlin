package com.works.myapplication.presentation.main

import androidx.lifecycle.ViewModel
import com.works.myapplication.domain.model.User
import com.works.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
}