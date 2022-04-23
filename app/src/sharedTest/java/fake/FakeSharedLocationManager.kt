package com.darkshandev.sutori.fake


import android.location.Location
import android.location.LocationManager
import com.darkshandev.sutori.data.datasources.SharedLocationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeSharedLocationManager : SharedLocationManager {
    var state: State = State.Error
    override fun fetchUpdates(): Flow<Location> = flow {
        if (state == State.Sucess) {
            val fakeLocation = Location(LocationManager.NETWORK_PROVIDER)
            fakeLocation.longitude = 100.0
            fakeLocation.latitude = -80.0
            emit(fakeLocation)
        }
    }
}