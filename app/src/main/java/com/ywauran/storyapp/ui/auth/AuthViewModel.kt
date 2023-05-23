package com.ywauran.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.LoginResponse
import com.ywauran.storyapp.repository.AuthRepository
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun handleLogin(email: String, password: String): LiveData<Result<LoginResponse>> =
        authRepository.login(email, password)

    fun handleRegister(name: String, email: String, password: String): LiveData<Result<HandlingResponse>> =
        authRepository.register(name, email, password)
}