package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val fireStoreService: FireStoreService){

    /**
     * Ajoute un utilisateur dans Firestore et retourne son ID.
     */
    suspend fun addUser(user: User): String? {
        return fireStoreService.addUser(user)
    }

    /**
     * Supprime un utilisateur de Firestore et retourne un booléen indiquant le succès.
     */
    suspend fun deleteUser(userId: String): Boolean {
        return fireStoreService.deleteUser(userId)
    }

    /**
     * Récupère un utilisateur par son ID sous forme de Flow.
     */
    fun getUserById(userId: String) = fireStoreService.getUserById(userId)

}