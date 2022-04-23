package com.darkshandev.sutori.data.datasources

import android.content.Context
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.data.models.request.toPartMap
import com.darkshandev.sutori.data.network.StoryService
import com.darkshandev.sutori.utils.ErrorUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

interface RemoteStoryDatasource {
    suspend fun postImage(
        image: File,
        photoRequest: PostPhotoRequest
    ): NetworkResult<PostResponse>

    suspend fun getStories(): NetworkResult<StoriesResponse>
}

class RemoteStoryDatasourceImpl @Inject constructor(
    private val retrofit: Retrofit,
    @ApplicationContext private val context: Context
) : RemoteStoryDatasource {
    private val service: StoryService = retrofit.create(StoryService::class.java)
    override suspend fun getStories(): NetworkResult<StoriesResponse> =
        getResponse(context.getString(R.string.unable_fetch_stories)) {
            service.getStory()
        }

    override suspend fun postImage(
        image: File,
        photoRequest: PostPhotoRequest
    ): NetworkResult<PostResponse> =
        getResponse(context.getString(R.string.unable_post_photo)) {
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImageFile
            )
            service.postStory(
                photo = imageMultipart,
                photoRequest.toPartMap()["description"]!!,
                photoRequest.toPartMap()["lat"],
                photoRequest.toPartMap()["lon"]

            )
        }

    private suspend fun <T> getResponse(
        defaultErrorMessage: String,
        request: suspend () -> Response<T>

    ): NetworkResult<T> {
        return try {
            val result = request.invoke()
            if (result.isSuccessful) {
                val body = result.body()
                body?.let {
                    return NetworkResult.Success(it)
                } ?: run {
                    return error("${result.code()} ${result.message()}")
                }
            } else {
                return if (result.code() == 401) {
                    error(context.getString(R.string.unauthorized_fetch))
                } else {
                    val errorResponse = ErrorUtils.parseError(result, retrofit)
                    error(errorResponse?.localizedMessage ?: defaultErrorMessage)
                }
            }
        } catch (e: Throwable) {
            NetworkResult.Error(context.getString(R.string.error_request), null)
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)
}