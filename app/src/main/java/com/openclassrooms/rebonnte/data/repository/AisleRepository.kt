package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Aisle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AisleRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Ajoute un rayon dans Firestore et retourne un booléen indiquant le succès.
     */
    suspend fun addAisle(name: String, description: String): Boolean {
        return fireStoreService.addAisle(name, description)
    }

    /**
     * Supprime un rayon de Firestore par son ID et retourne un booléen indiquant le succès.
     */
    suspend fun deleteAisle(aisleId: String): Boolean {
        return fireStoreService.deleteAisle(aisleId)
    }

    /**
     * Récupère tous les rayons sous forme de Flow.
     */
    fun fetchAllAisles(): Flow<List<Aisle?>> {
        return fireStoreService.fetchAllAisles()
    }

    /**
     * Récupère un rayon par son ID et retourne un objet Aisle ou null.
     */
    suspend fun fetchAisleById(aisleId: String): Aisle? {
        return fireStoreService.fetchAisleById(aisleId)
    }

}