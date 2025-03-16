package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

/**
 * Data class representing an aisle.
 */
@Keep
data class Aisle(
    val aisleId: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Long = 0
)