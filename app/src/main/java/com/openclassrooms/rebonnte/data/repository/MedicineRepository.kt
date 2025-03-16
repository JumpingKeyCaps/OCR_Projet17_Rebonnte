package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class for managing medicine-related operations.
 * @param fireStoreService The FireStoreService instance for database access.(Hilt injected)
 */
class MedicineRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Add a new medicine to Firestore and return a boolean indicating success.
     * @param medicine The Medicine object to add.
     * @param aisleId The ID of the aisle to add the medicine to.
     *
     */
    suspend fun addMedicine(
        medicine: Medicine,
        aisleId: String?
    ): Boolean {
        return fireStoreService.addMedicine(medicine,aisleId)
    }

    /**
     * Delete a medicine from Firestore by its medicineId and return a boolean indicating success.
     * @param medicineId The ID of the medicine to delete.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun deleteMedicine(medicineId: String): Boolean {
        return fireStoreService.deleteMedicine(medicineId)
    }

    /**
     * Get all medicines from Firestore and return a Flow of a list of medicines.
     * @return A Flow emitting a list of medicines.
     * @param sortDsc True to sort in descending order, false to sort in ascending order.
     */
    fun fetchAllMedicines(sortDsc: Boolean): Flow<List<Medicine?>> {
        return fireStoreService.fetchAllMedicines(sortDsc)
    }

    /**
     * Fetch a medicine by its medicineId and return a Medicine object or null.
     * @param medicineId The ID of the medicine to fetch.
     * @return The Medicine object if found, or null if not found.
     */
    suspend fun fetchMedicineById(medicineId: String): Medicine? {
        return fireStoreService.fetchMedicineById(medicineId)
    }

    /**
     * Search for medicines based on a query and return a Flow of a list of medicines.
     * @param searchQuery The query to search for.
     * @return A Flow emitting a list of medicines.
     */
    fun searchMedicinesRealTime(searchQuery: String): Flow<List<Medicine?>> {
        return fireStoreService.searchMedicinesRealTime(searchQuery)
    }

    /**
     * Get the aisle of a medicine by its medicineId and return a Flow of the aisle name.
     * @param medicineId The ID of the medicine to get the aisle for.
     * @return A Flow emitting the aisle id.
     */
    fun getMedicineAisle(medicineId: String): Flow<String?> {
        return fireStoreService.getMedicineAisle(medicineId)
    }

}