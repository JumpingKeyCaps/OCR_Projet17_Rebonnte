package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

@Keep
data class Medicine(
    val medicineId: String = "",
    val name: String= "",
    val description: String = "",
    val dosage: String = "",
    val fabricant: String = "",
    val indication: String = "",
    val principeActif: String = "",
    val utilisation: String = "",
    val warning: String = "",
    val createdAt: Long = 0

)