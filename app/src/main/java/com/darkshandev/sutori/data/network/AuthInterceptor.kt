package com.darkshandev.sutori.data.network

import com.darkshandev.sutori.data.datasources.SessionService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val sessionService: SessionService) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val basicReqBuilder = req.newBuilder()
        var token: String?
        runBlocking { token = sessionService.getUser().first()?.token }
        if (token != null) basicReqBuilder.addHeader("Authorization", "Bearer $token")
        val basicReq = basicReqBuilder.build()
        return chain.proceed(basicReq)
    }
}