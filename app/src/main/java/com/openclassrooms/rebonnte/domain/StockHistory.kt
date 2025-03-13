package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class StockHistory(
    val date: String,
    val time: String,
    val medicineId: String,
    val medicineName: String,
    val quantity: Long,
    val action: String,// "ADD" ou "REMOVE"
    val description: String,
    val author: String
) : Serializable