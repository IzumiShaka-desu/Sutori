package com.darkshandev.sutori.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.darkshandev.sutori.data.database.StoryDao
import com.darkshandev.sutori.data.datasources.RemoteStoryDatasource
import com.darkshandev.sutori.data.datasources.StoryMediator
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.utils.reduceFileImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

interface StoryRepository {

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        private const val DEFAULT_PAGE_SIZE = 10
        fun getDefaultPageConfig(): PagingConfig {
            return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = false)
        }
    }

    suspend fun postPhoto(image: File, request: PostPhotoRequest): Flow<NetworkResult<PostResponse>>
    fun getStoryPaged(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
    ): Flow<PagingData<Story>>

    fun getStoriesOnMap(): Flow<NetworkResult<StoriesResponse>>
}

open class StoryRepositoryImpl @Inject constructor(
    private val datasource: RemoteStoryDatasource,
    private val localDataSource: StoryDao,
    private val mediator: StoryMediator
) : StoryRepository {


    override suspend fun postPhoto(
        image: File,
        request: PostPhotoRequest
    ): Flow<NetworkResult<PostResponse>> = flow {
        emit(NetworkResult.Loading())
        val result = datasource.postImage(reduceFileImage(image), request)
        emit(result)
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    override fun getStoryPaged(
        pagingConfig: PagingConfig,

        ): Flow<PagingData<Story>> {

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { localDataSource.getAllStory() },
            remoteMediator = mediator
        ).flow
    }

    override fun getStoriesOnMap(): Flow<NetworkResult<StoriesResponse>> =
        flow {
            emit(NetworkResult.Loading())
            emit(datasource.getStories(isLocationAvailable = true))
        }.flowOn(Dispatchers.IO)
}