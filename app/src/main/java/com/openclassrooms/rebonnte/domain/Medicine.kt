package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep

/**
 * Data model representing a medicine.
 * @param medicineId The ID of the medicine.
 * @param name The name of the medicine.
 * @param description A description of the medicine.
 * @param dosage The dosage of the medicine.
 * @param fabricant The manufacturer of the medicine.
 * @param indication The indication of the medicine.
 * @param principeActif The principal active ingredient of the medicine.
 * @param utilisation The utilisation of the medicine.
 * @param warning The warning of the medicine.
 * @param createdAt The timestamp when the medicine was created.
 */
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