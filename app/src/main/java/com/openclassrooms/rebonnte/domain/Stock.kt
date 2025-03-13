package com.openclassrooms.rebonnte.domain

/**
 * Data model representing an entry in the 'stock' collection.
 */
data class Stock(
    val medicineId: String,
    val aisleId: String,
    val quantity: Int = 0,
    val name: String = "Unassigned medications"
)