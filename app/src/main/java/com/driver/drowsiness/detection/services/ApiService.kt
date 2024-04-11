package com.driver.drowsiness.detection.services

import com.driver.drowsiness.detection.models.SignInCredentials
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("signin")
    fun signIn(@Body credentials: SignInCredentials): Call<String>

    @POST("signup")
    @FormUrlEncoded
    fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<String>
}