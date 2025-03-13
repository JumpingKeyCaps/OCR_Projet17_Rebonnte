package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

@Keep
data class Aisle(
    val aisleId: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Long = 0
)