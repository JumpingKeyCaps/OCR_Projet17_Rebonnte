package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class StockHistory(
    val date: String,
    val medicineId: String,
    val medicineName: String,
    val quantity: Long,
    val reason: String,
    val type: String, // "ADD" ou "REMOVE"
    val updatedBy: String
) : Serializable