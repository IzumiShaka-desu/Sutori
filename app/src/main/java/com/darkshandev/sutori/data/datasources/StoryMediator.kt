package com.darkshandev.sutori.data.datasources

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.darkshandev.sutori.data.database.AppDatabase
import com.darkshandev.sutori.data.database.RemoteKeys
import com.darkshandev.sutori.data.database.RemoteKeysDao
import com.darkshandev.sutori.data.database.StoryDao
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.repositories.StoryRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
open class StoryMediator @Inject constructor(
    private val datasource: RemoteStoryDatasource,
    private val localDataSource: StoryDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, Story>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: StoryRepository.DEFAULT_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val responseData = datasource.getStories(page = page, size = state.config.pageSize)
            if (responseData is NetworkResult.Error) throw java.lang.Exception(responseData.message)
            val endOfPaginationReached = responseData.data?.listStory?.isEmpty() ?: true

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.deleteRemoteKeys()
                    localDataSource.deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.data?.listStory?.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys ?: emptyList())
                localDataSource.insertStory(responseData.data?.listStory ?: emptyList())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            remoteKeysDao.getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            remoteKeysDao.getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeysDao.getRemoteKeysId(id)
            }
        }
    }
}
