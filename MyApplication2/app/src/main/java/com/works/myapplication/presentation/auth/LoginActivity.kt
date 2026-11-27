package com.works.myapplication.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.works.myapplication.databinding.ActivityLoginBinding
import com.works.myapplication.presentation.main.MainActivity
import com.works.myapplication.utils.hide
import com.works.myapplication.utils.show
import com.works.myapplication.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.checkExistingSession()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeUiState()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(email, password)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) {
                        binding.progressBar.show()
                        binding.btnLogin.isEnabled = false
                        binding.tvErrorMessage.hide()
                    } else {
                        binding.progressBar.hide()
                        binding.btnLogin.isEnabled = true
                    }

                    if (state.errorMessage != null) {
                        binding.tvErrorMessage.text = state.errorMessage
                        binding.tvErrorMessage.show()
                    } else {
                        binding.tvErrorMessage.hide()
                    }

                    if (state.isSuccess) {
                        showToast("Hoş geldiniz, ${state.user?.name ?: ""}")
                        navigateToMain()
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}