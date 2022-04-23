package com.darkshandev.sutori.presentation.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.data.repositories.LocationRepository
import com.darkshandev.sutori.data.repositories.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _image = MutableStateFlow<File?>(null)
    val image = _image.asStateFlow()
    private val _request = MutableStateFlow(PostPhotoRequest("", null, null))
    val userRequest = _request.asStateFlow()
    fun setImage(image: File) {
        this._image.update { image }
    }

    private val _isLocationActive = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val locationUpdates: StateFlow<Location?> =
        _isLocationActive.distinctUntilChanged { old, new -> old == new }
            .transformLatest { isActive ->
                if (isActive) {
                    locationRepository.getLocations().collect {
                        emit(it)
                    }
                } else {
                    emit(null)
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                null
            )

    fun setLocationUpdateListenTo(isActive: Boolean) = _isLocationActive.update { isActive }
    fun setLocation(lat: Double?, lon: Double?) {
        this._request.update { it.copy(lat = lat, lon = lon) }
    }

    fun setDescription(desc: String) {
        this._request.update { it.copy(description = desc) }
    }

    fun postImage() {
        val selectedImage = _image.value
        val userRequest = _request.value
        if (selectedImage != null) {
            viewModelScope.launch {
                repository.postPhoto(selectedImage, userRequest).collect { result ->
                    _postResponse.update { result }
                    if (result is NetworkResult.Success) {
                        fetchStories()
                        this.coroutineContext.cancel()
                    }
                }
            }
        }
    }

    fun resetForm() {
        _image.update { null }
        _request.update { PostPhotoRequest("", null, null) }
        _postResponse.update { NetworkResult.Initial() }
    }

    private val _postResponse =
        MutableStateFlow<NetworkResult<PostResponse>>(NetworkResult.Initial())
    val postResponse = _postResponse.asStateFlow()

    private val _stories = MutableStateFlow<NetworkResult<StoriesResponse>>(NetworkResult.Initial())
    val stories = _stories.asStateFlow()

    fun fetchStories() {
        viewModelScope.launch {
            repository.getStories().collect { result ->
                _stories.update { result }
                if (result is NetworkResult.Success) this.coroutineContext.cancel()
            }
        }
    }

    private val _selectedStory = MutableStateFlow<Story?>(null)
    val selectedStory = _selectedStory.asStateFlow()
    fun setSelectedPost(story: Story) = _selectedStory.update { story }


}