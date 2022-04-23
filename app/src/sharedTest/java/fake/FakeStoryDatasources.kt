package com.darkshandev.sutori.fake

import com.darkshandev.sutori.data.datasources.RemoteStoryDatasource
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import java.io.File

class FakeStoryDatasources : RemoteStoryDatasource {
    var state: State = State.Error
    val listTest = List(10) {
        Story(it.toString(), "name$it", "desc", "url", "createat$it", null, null)
    }

    override suspend fun postImage(
        image: File,
        photoRequest: PostPhotoRequest
    ): NetworkResult<PostResponse> =
        if (state == State.Error) NetworkResult.Error(
            "fail",
            PostResponse(true, "fail")
        ) else NetworkResult.Success(
            PostResponse(false, "success")
        )

    override suspend fun getStories(
        page: Int?,
        size: Int?,
        isLocationAvailable: Boolean?
    ): NetworkResult<StoriesResponse> {
        return if (state == State.Error) NetworkResult.Error(
            "fail", StoriesResponse(
                true, "fail",
                emptyList()
            )
        ) else NetworkResult.Success(
            StoriesResponse(
                false,
                "success",
                listTest
            )
        )
    }


}