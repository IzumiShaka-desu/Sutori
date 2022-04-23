package com.darkshandev.sutori.data.repositories

import android.location.Location
import com.darkshandev.sutori.data.datasources.SharedLocationManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocationRepository {
    fun getLocations(): Flow<Location>
}

class LocationRepositoryImpl @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) : LocationRepository {
    override fun getLocations() = sharedLocationManager.fetchUpdates()
}