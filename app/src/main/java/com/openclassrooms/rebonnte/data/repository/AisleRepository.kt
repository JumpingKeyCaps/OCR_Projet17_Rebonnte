package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class for managing aisle-related operations.
 * @param fireStoreService The FireStoreService instance for database access.(Hilt injected)
 */
class AisleRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Add a new aisle to Firestore and return a boolean indicating success.
     * @param name The name of the aisle.
     * @param description The description of the aisle.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun addAisle(name: String, description: String): Boolean {
        return fireStoreService.addAisle(name, description)
    }

    /**
     * Delete an aisle from Firestore by its aisleId and return a boolean indicating success.
     * @param aisleId The ID of the aisle to delete.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun deleteAisle(aisleId: String): Boolean {
        return fireStoreService.deleteAisle(aisleId)
    }

    /**
     * Fetch all aisles from Firestore and return a Flow of a list of aisles.
     * @return A Flow emitting a list of aisles.
     */
    fun fetchAllAisles(): Flow<List<Aisle?>> {
        return fireStoreService.fetchAllAisles()
    }

    /**
     * Get an aisle by its aisleId and return an Aisle object or null.
     * @param aisleId The ID of the aisle to fetch.
     * @return The Aisle object if found, or null if not found.
     */
    suspend fun fetchAisleById(aisleId: String): Aisle? {
        return fireStoreService.fetchAisleById(aisleId)
    }

    /**
     * Fetch medicines for a specific aisle and return a Flow of a list of MedicineWithStock.
     * @param aisleId The ID of the aisle to fetch medicines for.
     * @return A Flow emitting a list of MedicineWithStock.
     */
    fun fetchMedicinesForAisle(aisleId: String): Flow<List<MedicineWithStock>> {
        return fireStoreService.fetchMedicinesForAisle(aisleId)
    }

}