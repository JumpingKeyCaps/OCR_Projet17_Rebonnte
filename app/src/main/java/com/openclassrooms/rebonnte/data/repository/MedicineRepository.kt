package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicineRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Ajoute un médicament dans Firestore et retourne un booléen indiquant le succès.
     */
    suspend fun addMedicine(
        name: String,
        dosage: String,
        fabricant: String,
        indication: String,
        principeActif: String,
        utilisation: String,
        warning: String
    ): Boolean {
        return fireStoreService.addMedicine(
            name, dosage, fabricant, indication, principeActif, utilisation, warning
        )
    }

    /**
     * Supprime un médicament de Firestore par son ID et retourne un booléen indiquant le succès.
     */
    suspend fun deleteMedicine(medicineId: String): Boolean {
        return fireStoreService.deleteMedicine(medicineId)
    }

    /**
     * Récupère tous les médicaments sous forme de Flow.
     */
    fun fetchAllMedicines(sortDsc: Boolean): Flow<List<Medicine?>> {
        return fireStoreService.fetchAllMedicines(sortDsc)
    }

    /**
     * Récupère un médicament par son ID et retourne un objet Medicine ou null.
     */
    suspend fun fetchMedicineById(medicineId: String): Medicine? {
        return fireStoreService.fetchMedicineById(medicineId)
    }

    /**
     * Recherche les médicaments en temps réel en fonction du texte de recherche.
     */
    fun searchMedicinesRealTime(searchQuery: String): Flow<List<Medicine?>> {
        return fireStoreService.searchMedicinesRealTime(searchQuery)
    }

}