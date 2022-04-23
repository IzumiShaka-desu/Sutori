package com.darkshandev.sutori.data.network

import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryService {
    @GET("stories")
    suspend fun getStory(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") isLocationAvailable: Int? = null
    ): Response<StoriesResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Response<PostResponse>
}
