package com.darkshandev.sutori.fake

import com.darkshandev.sutori.data.datasources.RemoteUserDataSources
import com.darkshandev.sutori.data.models.LoginResponse
import com.darkshandev.sutori.data.models.LoginResult
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.request.LoginRequest
import com.darkshandev.sutori.data.models.request.RegisterRequest

class FakeUserDatasources : RemoteUserDataSources {
    companion object {
        val DUMMY_SUCCESS_POST_RESPONSE = PostResponse(false, "success")
        val DUMMY_FAIL_POST_RESPONSE = PostResponse(true, "fail")
        val DUMMY_SUCCESS_LOGIN_RESPONSE =
            LoginResponse(false, "success", LoginResult("1", "name", "token"))
        val DUMMY_FAIL_LOGIN_RESPONSE = LoginResponse(true, "fail", null)
    }

    var state = State.Error
    override suspend fun register(request: RegisterRequest): NetworkResult<PostResponse> =
        if (state == State.Sucess) NetworkResult.Success(DUMMY_SUCCESS_POST_RESPONSE) else NetworkResult.Error(
            "fail",
            DUMMY_FAIL_POST_RESPONSE
        )


    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> =
        if (state == State.Sucess) NetworkResult.Success(DUMMY_SUCCESS_LOGIN_RESPONSE) else NetworkResult.Error(
            "fail",
            DUMMY_FAIL_LOGIN_RESPONSE
        )

}