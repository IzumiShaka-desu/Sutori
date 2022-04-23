package com.darkshandev.sutori.data.repositories

import com.darkshandev.sutori.data.datasources.RemoteStoryDatasource
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.utils.reduceFileImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

interface StoryRepository {
    suspend fun getStories(): Flow<NetworkResult<StoriesResponse>>
    suspend fun postPhoto(image: File, request: PostPhotoRequest): Flow<NetworkResult<PostResponse>>
}

open class StoryRepositoryImpl @Inject constructor(
    private val datasource: RemoteStoryDatasource
) : StoryRepository {

    override suspend fun getStories(): Flow<NetworkResult<StoriesResponse>> = flow {
        emit(NetworkResult.Loading())
        val result = datasource.getStories()
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun postPhoto(
        image: File,
        request: PostPhotoRequest
    ): Flow<NetworkResult<PostResponse>> = flow {
        emit(NetworkResult.Loading())
        val result = datasource.postImage(reduceFileImage(image), request)
        emit(result)
    }.flowOn(Dispatchers.IO)
}