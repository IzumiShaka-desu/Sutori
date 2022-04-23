package com.darkshandev.sutori.data.repositories


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.darkshandev.sutori.fake.FakeSharedLocationManager
import com.darkshandev.sutori.fake.State

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationRepositoryImplTest {
    private lateinit var manager: FakeSharedLocationManager
    private lateinit var repo: LocationRepositoryImpl

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        manager = FakeSharedLocationManager()
        repo = LocationRepositoryImpl(manager)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNotEmitAnythingWhenGetError`() = runTest {
        manager.state = State.Error
        val results = repo.getLocations().test {
            awaitComplete()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `shouldNotEmitAnythingWhenGetSuccess`() = runTest {
        manager.state = State.Sucess
        val results = repo.getLocations().test {
            assertNotEquals(awaitItem(), null)
            awaitComplete()
        }
    }
}