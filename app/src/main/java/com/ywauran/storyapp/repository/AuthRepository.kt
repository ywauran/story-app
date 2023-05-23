package com.ywauran.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ywauran.storyapp.helper.Result
import com.ywauran.storyapp.helper.convertErrorData
import com.ywauran.storyapp.data.remote.api.ApiService
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.LoginResponse
import com.ywauran.storyapp.ui.auth.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        val data: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
        data.postValue(Result.Loading())

        val userModel = UserModel(email = email, password = password)
        try {
            apiService.login(userModel).enqueue(object : Callback<LoginResponse>{
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        data.postValue(Result.Success(response.body() as LoginResponse))
                    }
                    else {
                        val errorData = response.errorBody()?.string()?.let { convertErrorData(it) }
                        data.postValue(Result.Error(errorData?.message, response.code(), null))
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    data.postValue(Result.Error(t.message.toString(), null, null))
                }

            })
        }catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("Error converting data", null, null))
        }
        return data
    }

    fun register(name: String, email: String, password:String): LiveData<Result<HandlingResponse>> {

        val data: MutableLiveData<Result<HandlingResponse>> = MutableLiveData()
        data.postValue(Result.Loading())
        val userData = UserModel(name = name, email = email, password = password)

        try {
            apiService.register(userData).enqueue(object: Callback<HandlingResponse>{
                override fun onResponse(
                    call: Call<HandlingResponse>,
                    response: Response<HandlingResponse>
                ) {
                    if (response.isSuccessful) {
                        data.postValue(Result.Success(response.body() as HandlingResponse))
                    }
                    else {
                        val errorData = response.errorBody()?.let { convertErrorData(it.string()) }
                        data.postValue(Result.Error(errorData?.message, response.code(), null))
                    }
                }

                override fun onFailure(call: Call<HandlingResponse>, t: Throwable) {
                    data.postValue(Result.Error(t.message.toString(), null, null))
                }

            })
        }catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("Error converting data", null, null))
        }
        return data
    }
}