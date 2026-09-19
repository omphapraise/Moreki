package com.cubiccode.moreki.api

import com.cubiccode.moreki.models.AuthResponse
import com.cubiccode.moreki.models.LoginRequest
import com.cubiccode.moreki.models.RegisterRequest
import com.cubiccode.moreki.models.UpdateProfileRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @GET("profile")
    fun getProfile(@Query("email") email: String): Call<AuthResponse>

    @POST("update-profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<AuthResponse>
}