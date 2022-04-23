package com.darkshandev.sutori.data.repositories

import com.darkshandev.sutori.data.datasources.RemoteUserDataSources
import com.darkshandev.sutori.data.datasources.SessionService
import com.darkshandev.sutori.data.models.LoginResponse
import com.darkshandev.sutori.data.models.LoginResult
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.request.LoginRequest
import com.darkshandev.sutori.data.models.request.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UserRepository {
    fun getSession(): Flow<LoginResult?>
    suspend fun logout()
    suspend fun loginBy(email: String, password: String): Flow<NetworkResult<LoginResponse>>
    suspend fun registerBy(request: RegisterRequest): Flow<NetworkResult<PostResponse>>
}

class UserRepositoryImpl @Inject constructor(
    private val dataSources: RemoteUserDataSources,
    private val sessionService: SessionService
) : UserRepository {
    override fun getSession(): Flow<LoginResult?> = sessionService.getUser()
    override suspend fun logout() = withContext(Dispatchers.IO) { sessionService.logout() }
    override suspend fun loginBy(
        email: String,
        password: String
    ): Flow<NetworkResult<LoginResponse>> = flow {
        emit(NetworkResult.Loading())
        val result = dataSources.login(LoginRequest(email, password))
        emit(result)
        result.data?.loginResult?.let {
            sessionService.saveUser(it)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun registerBy(request: RegisterRequest): Flow<NetworkResult<PostResponse>> =
        flow {
            emit(NetworkResult.Loading())
            emit(dataSources.register(request))
        }.flowOn(Dispatchers.IO)

}