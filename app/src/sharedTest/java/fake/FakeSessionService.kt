package com.darkshandev.sutori.fake

import com.darkshandev.sutori.data.datasources.SessionService
import com.darkshandev.sutori.data.models.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSessionService : SessionService {
    companion object {
        val DUMMY_LOGIN_RESULT = LoginResult("1", "name", "someToken")
    }

    var state: State = State.Error
    override fun getUser(): Flow<LoginResult?> = flow {
        if (state == State.Sucess) {
            emit(DUMMY_LOGIN_RESULT)
        } else {
            emit(null)
        }
    }

    override suspend fun saveUser(user: LoginResult) {

    }

    override suspend fun logout() {
    }
}