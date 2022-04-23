package com.darkshandev.sutori.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.request.RegisterRequest
import com.darkshandev.sutori.fake.FakeSessionService
import com.darkshandev.sutori.fake.FakeUserDatasources
import com.darkshandev.sutori.fake.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserRepositoryImplTest {
    private lateinit var datasources: FakeUserDatasources
    private lateinit var sessionService: FakeSessionService
    private lateinit var repo: UserRepositoryImpl

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        datasources = FakeUserDatasources()
        sessionService = FakeSessionService()
        repo = UserRepositoryImpl(datasources, sessionService)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitNullWhenGetSessionEror`() = runTest {
        sessionService.state = State.Error
        datasources.state = State.Error
        repo.getSession().test {
            assertEquals(awaitItem(), null)
            awaitComplete()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitLoginResultWhenGetSessionSuccess`() = runTest {
        sessionService.state = State.Sucess
        datasources.state = State.Error
        val results = repo.getSession().test {
            assertEquals(awaitItem(), FakeSessionService.DUMMY_LOGIN_RESULT)
            awaitComplete()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitErrorWhenLoginError`() = runBlocking {
        sessionService.state = State.Error
        datasources.state = State.Error
        val results = repo.loginBy("someMail", "somePass").test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            assertEquals(awaitItem().message, "fail")
            awaitComplete()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitLoginResultWhenLoginSuccess`() = runBlocking {
        sessionService.state = State.Sucess
        datasources.state = State.Sucess
        val results = repo.loginBy("someMail", "somePass").test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            assertTrue(awaitItem() is NetworkResult.Success)
            awaitComplete()
        }
    }

    private val dummyRegReq = RegisterRequest("name", "mail", "pass")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitErrorWhenRegisterError`() = runBlocking {
        sessionService.state = State.Error
        datasources.state = State.Error
        val results = repo.registerBy(dummyRegReq).test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            assertEquals(awaitItem().message, "fail")
            awaitComplete()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitLoginResultWhenRegisterSuccess`() = runBlocking {
        sessionService.state = State.Sucess
        datasources.state = State.Sucess
        val results = repo.registerBy(dummyRegReq).test {
            assertTrue(awaitItem() is NetworkResult.Loading)
            assertTrue(awaitItem() is NetworkResult.Success)
            awaitComplete()
        }
    }
}