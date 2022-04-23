package com.darkshandev.sutori.data.datasources

import android.content.Context
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.LoginResponse
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.request.LoginRequest
import com.darkshandev.sutori.data.models.request.RegisterRequest
import com.darkshandev.sutori.data.network.UserService
import com.darkshandev.sutori.utils.ErrorUtils
import com.darkshandev.sutori.utils.EspressoIdlingResource
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

interface RemoteUserDataSources {
    suspend fun register(request: RegisterRequest): NetworkResult<PostResponse>
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
}

class RemoteUserDataSourcesImpl @Inject constructor(
    private val retrofit: Retrofit,
    @ApplicationContext private val context: Context
) :
    RemoteUserDataSources {
    private val service = retrofit.create(UserService::class.java)
    private val idlingResources = EspressoIdlingResource

    override suspend fun register(request: RegisterRequest): NetworkResult<PostResponse> =
        getResponse(context.getString(R.string.unable_request_register)) {
            service.register(request)
        }

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> =
        getResponse(context.getString(R.string.unable_request_login)) {
            service.login(request)
        }

    private suspend fun <T> getResponse(
        defaultErrorMessage: String,
        request: suspend () -> Response<T>

    ): NetworkResult<T> {
        idlingResources.increment()
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

                    error(context.getString(R.string.auth_failed))
                } else {
                    val errorResponse = ErrorUtils.parseError(result, retrofit)
                    error(errorResponse?.localizedMessage ?: defaultErrorMessage)
                }
            }
        } catch (e: Throwable) {
            NetworkResult.Error(context.getString(R.string.error_request), null)
        } finally {
            idlingResources.decrement()
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)
}