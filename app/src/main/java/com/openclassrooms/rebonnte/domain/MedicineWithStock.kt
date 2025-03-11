package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

@Keep
data class MedicineWithStock(
    val medicineId: String,
    val name: String,
    val description: String,
    val dosage: String,
    val manufacturer: String,
    val createdAt: Long,
    val quantity: Int,
    val lastUpdate: String
)