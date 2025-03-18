package com.openclassrooms.rebonnte.domain

/**
 * Data model representing a stock.
 * @param medicineId The ID of the medicine.
 * @param aisleId The ID of the aisle.
 * @param quantity The quantity of the medicine in stock.
 * @param name The name of the aisle.
 */
data class Stock(
    val medicineId: String="",
    val aisleId: String ="",
    val quantity: Int = 0,
    val name: String = "Unassigned medications"
){
    constructor() : this("", "", 0, "")

}