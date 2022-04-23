package com.darkshandev.sutori.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.darkshandev.sutori.data.models.LoginResult
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.request.RegisterRequest
import com.darkshandev.sutori.data.repositories.UserRepository
import com.darkshandev.sutori.fake.FakeSessionService
import com.darkshandev.sutori.fake.FakeUserDatasources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewModel: UserViewModel

    @Mock
    private lateinit var repo: UserRepository
    private lateinit var unDirectViewModel: UserViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        unDirectViewModel = UserViewModel(repo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun down() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitNullWhenGetSessionEror`() = runBlocking {
        val session = MutableStateFlow<LoginResult?>(null)
        Mockito.`when`(viewModel.sessionUser).thenReturn(session.asStateFlow())
        val results = viewModel.sessionUser.test {
            assertEquals(awaitItem(), null)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNEmitLoginResultWhenGetSessionSuccess`() = runBlocking {

        val session = MutableStateFlow<LoginResult?>(FakeSessionService.DUMMY_LOGIN_RESULT)
        Mockito.`when`(viewModel.sessionUser).thenReturn(session)
        val results = viewModel.sessionUser.test {
            assertNotNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldEmitErrorWhenGetLoginError`() = runBlocking {
        val dummy = NetworkResult.Error("fail", FakeUserDatasources.DUMMY_FAIL_LOGIN_RESPONSE)
        val expected = flow {
            emit(dummy)
        }
        Mockito.`when`(repo.loginBy("Email", "password")).thenReturn(expected)
        unDirectViewModel.loginBy("Email", "password")
        val results = unDirectViewModel.loginResponse.test {
            val emission = awaitItem()
            assertEquals(emission, dummy)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldEmitSuccessWhenGetLoginSuccess`() = runBlocking {
        val dummy = NetworkResult.Success(FakeUserDatasources.DUMMY_SUCCESS_LOGIN_RESPONSE)
        val expected = flow {
            emit(dummy)
        }
        Mockito.`when`(repo.loginBy("Email", "password")).thenReturn(expected)
        unDirectViewModel.loginBy("Email", "password")

        val results = unDirectViewModel.loginResponse.test {
            val secondEmission = awaitItem()
            assertEquals(secondEmission, dummy)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldEmitErrorWhenGetRegisterError`() = runBlocking {
        val dummy = NetworkResult.Error("fail", FakeUserDatasources.DUMMY_FAIL_POST_RESPONSE)
        val expected = flow {
            emit(dummy)
        }
        val request = RegisterRequest("name", "email", "password")
        Mockito.`when`(repo.registerBy(request)).thenReturn(expected)
        unDirectViewModel.registerBy("name", "email", "password")
        val results = unDirectViewModel.registerResponse.test {
            val secondEmission = awaitItem()
            assertEquals(secondEmission, dummy)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldEmitSuccessWhenGetRegisterSuccess`() = runBlocking {
        val dummy = NetworkResult.Success(FakeUserDatasources.DUMMY_SUCCESS_POST_RESPONSE)
        val expected = flow {
            emit(dummy)
        }
        val request = RegisterRequest("name", "email", "password")
        Mockito.`when`(repo.registerBy(request)).thenReturn(expected)
        unDirectViewModel.registerBy("name", "email", "password")
        val results = unDirectViewModel.registerResponse.test {
            val secondEmission = awaitItem()
            assertEquals(secondEmission, dummy)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verifyLogout`() = runTest {
        unDirectViewModel.logout()
        launch {
            Mockito.verify(repo).logout()
        }
    }

}

