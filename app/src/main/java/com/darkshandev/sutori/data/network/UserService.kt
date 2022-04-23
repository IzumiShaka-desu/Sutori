package com.darkshandev.sutori.data.network

import com.darkshandev.sutori.data.models.LoginResponse
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.request.LoginRequest
import com.darkshandev.sutori.data.models.request.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<PostResponse>

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>
}