package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Data model representing a stock history entry.
 * @param date The date of the stock history entry.
 * @param time The time of the stock history entry.
 * @param medicineId The ID of the medicine associated with the stock history entry.
 * @param medicineName The name of the medicine associated with the stock history entry.
 * @param quantity The quantity of the medicine at the time of the stock history entry.
 * @param action The action that was performed on the medicine ("ADD" or "REMOVE").
 * @param description A description of the stock history entry.
 * @param author The author of the stock history entry.
 */
@Keep
data class StockHistory(
    val date: String,
    val time: String,
    val medicineId: String,
    val medicineName: String,
    val quantity: Int,
    val action: String,// "ADD" ou "REMOVE"
    val description: String,
    val author: String
) : Serializable{
    constructor() : this("", "", "", "", 0, "", "", "")
}