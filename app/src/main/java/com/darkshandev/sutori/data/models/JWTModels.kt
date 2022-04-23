package com.darkshandev.sutori.data.models

import java.util.*

data class JWTModels(val userId: String, val issuedAt: Date, val expireAt: Date)

fun JWTModels.isExpire(): Boolean = Date(System.currentTimeMillis()).after(expireAt)
