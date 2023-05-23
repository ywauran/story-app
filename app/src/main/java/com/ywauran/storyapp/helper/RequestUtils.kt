package com.ywauran.storyapp.helper

import com.google.gson.Gson
import com.ywauran.storyapp.data.remote.response.ErrorResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URLConnection

fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
    val name = URLConnection.guessContentTypeFromName(file.name)

    val requestFile: RequestBody = file.asRequestBody(name.toMediaTypeOrNull())

    return  MultipartBody.Part.createFormData(partName, file.name, requestFile)
}

fun convertErrorData(errorData: String): ErrorResponse{
    return try {
        val gson = Gson()
        gson.fromJson(errorData, ErrorResponse::class.java)
    }catch (e: Exception) {
        e.printStackTrace()
        ErrorResponse(true, null)
    }
}

