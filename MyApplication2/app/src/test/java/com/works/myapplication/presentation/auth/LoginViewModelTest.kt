package com.works.myapplication.presentation.auth

import com.works.myapplication.domain.model.User
import com.works.myapplication.domain.repository.AuthRepository
import com.works.myapplication.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk(relaxed = true)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty email sets error message`() {
        viewModel.login("", "123456")
        assertEquals("Lütfen e-posta adresinizi giriniz.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login with invalid email sets error message`() {
        viewModel.login("invalid-email", "123456")
        assertEquals("Lütfen geçerli bir e-posta adresi giriniz.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login with empty password sets error message`() {
        viewModel.login("test@gmail.com", "")
        assertEquals("Lütfen şifrenizi giriniz.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login with short password sets error message`() {
        viewModel.login("test@gmail.com", "123")
        assertEquals("Şifre en az 6 karakter olmalıdır.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login success updates state with user`() = runTest {
        val user = User(
            id = 1,
            name = "Hakan ÖZER",
            email = "hakanozer02@gmail.com",
            role = "admin",
            createdAt = "2024-12-01",
            updatedAt = "2024-12-01"
        )
        coEvery { authRepository.login("hakanozer02@gmail.com", "123456") } returns Resource.Success(user)

        viewModel.login("hakanozer02@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals(user, viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `checkExistingSession delegates to authRepository`() {
        every { authRepository.isLoggedIn() } returns true
        assertTrue(viewModel.checkExistingSession())
    }
}