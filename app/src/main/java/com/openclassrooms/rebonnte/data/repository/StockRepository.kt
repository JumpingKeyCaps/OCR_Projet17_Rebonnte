package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import javax.inject.Inject

class StockRepository @Inject constructor(private val fireStoreService: FireStoreService){

    suspend fun updateMedicineAisle(medicineId: String, newAisleId: String): Boolean {
        return fireStoreService.updateMedicineAisle(medicineId, newAisleId)
    }

    suspend fun updateQuantityInStock(medicineId: String, quantityDelta: Int, author: String, description: String): Boolean {
        return fireStoreService.updateQuantityInStock(medicineId, quantityDelta, author, description)
    }
}