package com.driver.drowsiness.detection.services

import com.driver.drowsiness.detection.models.SignInCredentials
import com.driver.drowsiness.detection.models.SignUpCredentials
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("signin")
    fun signIn(@Body credentials: SignInCredentials): Call<String>

    @POST("signup")
    fun signUp(@Body credentials: SignUpCredentials): Call<String>

}