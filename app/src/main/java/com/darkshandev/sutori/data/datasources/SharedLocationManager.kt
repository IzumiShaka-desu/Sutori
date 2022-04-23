package com.darkshandev.sutori.data.datasources

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import com.darkshandev.sutori.utils.EspressoIdlingResource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface SharedLocationManager {
    fun fetchUpdates(): Flow<Location>
}

class SharedLocationManagerImpl @Inject constructor(
    private val client: FusedLocationProviderClient
) : SharedLocationManager {
    private val idlingResources = EspressoIdlingResource


    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchUpdates(): Flow<Location> = callbackFlow {
        idlingResources.increment()
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation

                trySend(location)
            }
        }


        client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
            .addOnFailureListener {
                Log.d("error", it.toString())
            }
        awaitClose { client.removeLocationUpdates(callBack) }
        idlingResources.decrement()
    }

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2L

    }
}