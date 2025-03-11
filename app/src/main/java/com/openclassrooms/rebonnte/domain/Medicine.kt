package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

@Keep
data class Medicine(
    val id: String,
    val name: String,
    val description: String,
    val dosage: String,
    val manufacturer: String,
    val createdAt: Long

)