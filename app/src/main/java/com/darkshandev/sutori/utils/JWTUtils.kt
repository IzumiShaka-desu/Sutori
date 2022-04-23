package com.darkshandev.sutori.utils

import android.util.Base64
import com.darkshandev.sutori.app.Config
import com.darkshandev.sutori.data.models.JWTModels
import com.google.gson.GsonBuilder
import java.io.UnsupportedEncodingException
import java.util.*


object JWTUtils {
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): JWTModels? {
        try {
            val split = JWTEncoded.split(".").toTypedArray()
            val body = GsonBuilder().create().fromJson(getJson(split[1]), Map::class.java)
            val iat = getClaimAt((body["iat"] as Double).toLong())
            val exp = getExpireAt((body["iat"] as Double).toLong())
            return JWTModels(
                body["userId"].toString(),
                iat,
                exp
            )
        } catch (e: UnsupportedEncodingException) {

        }
        return null
    }

    private fun getClaimAt(iat: Long): Date = Date(iat * 1000)
    private fun getExpireAt(iat: Long): Date = Date(((iat + Config.maxAgeTokenInSecond) * 1000))

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }
}