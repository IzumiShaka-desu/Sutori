package com.darkshandev.sutori.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkshandev.sutori.data.models.LoginResponse
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.request.RegisterRequest
import com.darkshandev.sutori.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    val sessionUser = repository.getSession().stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _loginResponse =
        MutableStateFlow<NetworkResult<LoginResponse>>(NetworkResult.Initial())
    val loginResponse = _loginResponse
    fun loginBy(email: String, password: String) {
        viewModelScope.launch {
            repository.loginBy(email, password).collect { result ->
                _loginResponse.update { result }
                if (result is NetworkResult.Success) this.coroutineContext.cancel()
            }
        }
    }

    private val _registerResponse =
        MutableStateFlow<NetworkResult<PostResponse>>(NetworkResult.Initial())
    val registerResponse = _registerResponse
    fun registerBy(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.registerBy(RegisterRequest(name, email, password)).collect { result ->
                _registerResponse.update { result }
                if (result is NetworkResult.Success) this.coroutineContext.cancel()
            }
        }
    }

    fun logout() = viewModelScope.launch {
        repository.logout()
    }
}