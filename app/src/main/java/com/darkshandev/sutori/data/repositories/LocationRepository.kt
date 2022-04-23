package com.darkshandev.sutori.data.repositories

import com.darkshandev.sutori.data.datasources.SharedLocationManager
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) {
    fun getLocations() = sharedLocationManager.fetchUpdates()
}