package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import javax.inject.Inject

/**
 * Repository class for managing stock-related operations.
 * @param fireStoreService The FireStoreService instance for database access.(Hilt injected)
 */
class StockRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Update the aisleId of a medicine in database.
     * @param medicineId The ID of the medicine to update.
     * @param newAisleId The new aisleId to assign to the medicine.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun updateMedicineAisle(medicineId: String, newAisleId: String): Boolean {
        return fireStoreService.updateMedicineAisle(medicineId, newAisleId)
    }

    /**
     * Updates the quantity in stock for a given medicineId.
     * @param medicineId The ID of the medicine to update the quantity for.
     * @param quantityDelta The change in quantity to apply.
     * @param author The author of the transaction.
     * @param description A description of the transaction.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun updateQuantityInStock(medicineId: String, quantityDelta: Int, author: String, description: String): Boolean {
        return fireStoreService.updateQuantityInStock(medicineId, quantityDelta, author, description)
    }

    /**
     * Gets the stock quantity for a given medicineId.
     * @param medicineId The ID of the medicine to retrieve the stock for.
     * @return The stock quantity or null if not found.
     */
    suspend fun getStockQuantity(medicineId: String): Int? {
        return fireStoreService.getStockQuantity(medicineId)
    }
}