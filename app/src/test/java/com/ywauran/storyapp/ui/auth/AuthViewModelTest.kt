package com.ywauran.storyapp.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.LoginResponse
import com.ywauran.storyapp.repository.AuthRepository
import com.ywauran.storyapp.helper.Result
import com.ywauran.storyapp.utils.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel


    companion object{
        private const val LOGIN_CORRECT_EMAIL = "ywauran16@gmail.com"
        private const val LOGIN_CORRECT_PASSWORD = "HarkeWauran16"
        private const val LOGIN_WRONG_PASSWORD = "harkewauran16"

        private const val REGISTER_NAME = "Harke"
        private const val REGISTER_EMAIL = "harke@gmail.com"
        private const val REGISTER_EXISTING_EMAIL = "harke123@gmail.com"
        private const val REGISTER_PASSWORD = "12345678"
    }

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `when login with correct email and password, then return Result Success`() {
        val successResponse = generateSuccessLoginResponse()
        val expectedValue = MutableLiveData<Result<LoginResponse>>().apply {
            value = Result.Success(data = successResponse)
        }
        Mockito.`when`(authRepository.login(LOGIN_CORRECT_EMAIL, LOGIN_CORRECT_PASSWORD))
            .thenReturn(expectedValue)

        val actualValue = authViewModel.handleLogin(LOGIN_CORRECT_EMAIL, LOGIN_CORRECT_PASSWORD)
            .getOrAwaitValue()

        Mockito.verify(authRepository).login(LOGIN_CORRECT_EMAIL, LOGIN_CORRECT_PASSWORD)
        assertNotNull(actualValue)
        assertEquals(expectedValue.value, actualValue)
    }

    @Test
    fun `when login with wrong password, then return result error`() {
        val errorResponse = generateErrorLoginResponse()
        val expectedValue = MutableLiveData<Result<LoginResponse>>().apply {
            value = Result.Error(code = 401, data = errorResponse)
        }
        Mockito.`when`(authRepository.login(LOGIN_CORRECT_EMAIL, LOGIN_WRONG_PASSWORD))
            .thenReturn(expectedValue)

        val actualValue =
            authViewModel.handleLogin(LOGIN_CORRECT_EMAIL, LOGIN_WRONG_PASSWORD).getOrAwaitValue()

        Mockito.verify(authRepository).login(LOGIN_CORRECT_EMAIL, LOGIN_WRONG_PASSWORD)
        assertNotNull(actualValue)
        assertEquals(expectedValue.value?.data?.message, actualValue.data?.message)
    }

    @Test
    fun `when register then return result success and success response data`() {
        val dummySuccessRegisterResponse = generateSuccessRegisterResponse()
        val expectedValues = MutableLiveData<Result<HandlingResponse>>().apply {
            value = Result.Success(dummySuccessRegisterResponse)
        }
        Mockito.`when`(
            authRepository.register(REGISTER_NAME, REGISTER_EMAIL, REGISTER_PASSWORD)
        ).thenReturn(expectedValues)

        val actualResponse = authViewModel.handleRegister(
            REGISTER_NAME, REGISTER_EMAIL, REGISTER_PASSWORD
        ).getOrAwaitValue()

        Mockito.verify(authRepository).register(REGISTER_NAME, REGISTER_EMAIL, REGISTER_PASSWORD)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(expectedValues.value, actualResponse)
    }

    @Test
    fun `when login with wrong email, then return result error`() {
        val dummyErrorRegisterResponse = generateErrorRegisterResponse()
        val expectedValues = MutableLiveData<Result<HandlingResponse>>().apply {
            value = Result.Error(code = 400, data = dummyErrorRegisterResponse)
        }
        Mockito.`when`(
            authRepository.register(
                REGISTER_NAME, REGISTER_EXISTING_EMAIL, REGISTER_PASSWORD
            )
        ).thenReturn(expectedValues)

        val actualResponse = authViewModel.handleRegister(
            REGISTER_NAME, REGISTER_EXISTING_EMAIL, REGISTER_PASSWORD
        ).getOrAwaitValue()

        Mockito.verify(authRepository).register(
            REGISTER_NAME, REGISTER_EXISTING_EMAIL, REGISTER_PASSWORD
        )

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
        assertEquals(
            (expectedValues.value as Result.Error).data?.message,
            actualResponse.data?.message
        )
    }
}