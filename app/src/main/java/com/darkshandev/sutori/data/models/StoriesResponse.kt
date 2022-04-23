package com.darkshandev.sutori.data.models

import okhttp3.internal.http.toHttpDateString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


data class StoriesResponse(val error: Boolean, val message: String, val listStory: List<Story>)
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

fun Story.formatDate(): String {
    val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
    val date: Date = df1.parse(createdAt)
    return date.toHttpDateString()
}