package com.ywauran.storyapp.data.remote.api

import com.ywauran.storyapp.data.remote.response.*
import com.ywauran.storyapp.ui.auth.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest")
    @POST("login")
    fun login(@Body user: UserModel): Call<LoginResponse>

    @POST("register")
    fun register(@Body user: UserModel): Call<HandlingResponse>

    @GET("stories")
    suspend fun getListStory(@Header("Authorization") authorization: String, @Query("page") page: Int, @Query("size") size: Int): ListStoryResponse

    @GET("stories")
    fun getListStory(@Header("Authorization") authorization: String, @Query("location") location: Int): Call<ListStoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(@Header("Authorization") authorization: String, @Path("id") id: String): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(@Header("Authorization") authorization: String, @Part("description") description: RequestBody, @Part file: MultipartBody.Part): Call<HandlingResponse>

}