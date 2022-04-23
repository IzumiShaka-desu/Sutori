package com.darkshandev.sutori.data.models.request

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


data class PostPhotoRequest(val description: String, val lat: Double?, val lon: Double?)

fun PostPhotoRequest.toPartMap(): Map<String, RequestBody> {
    val map: HashMap<String, RequestBody> = HashMap()
    map["description"] = description.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    if (lat != null && lon != null) {
        map["lat"] = lat.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["lon"] = lon.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }
    return map
}