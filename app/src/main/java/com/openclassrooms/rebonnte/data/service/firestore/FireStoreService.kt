package com.openclassrooms.rebonnte.data.service.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.domain.StockHistory
import com.openclassrooms.rebonnte.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Service to interact with FireStore (DataBase).
 */
class FireStoreService {
    private val db = FirebaseFirestore.getInstance()


    //Aisle (shop) ------------------------------------------------------------------------<

    /**
     * Ajoute un magasin avec tous les médicaments initialisés à 0 en stock.
     */
    suspend fun addAisleWithStock(aisleName: String, location: String, description: String): String? {
        return try {
            val aisleRef = db.collection("Aisle").document()
            val aisleId = aisleRef.id

            // le magasin
            aisleRef.set(
                mapOf(
                    "aisleId" to aisleId,
                    "name" to aisleName,
                    "location" to location,
                    "description" to description,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()

            // Récupérer tous les médicaments
            val medicinesSnapshot = db.collection("medicines").get().await()

            // Ajouter chaque médicament dans le stock du magasin
            for (document in medicinesSnapshot.documents) {
                val medicineId = document.id
                aisleRef.collection("medicines").document(medicineId).set(
                    mapOf(
                        "medicineId" to medicineId,
                        "quantity" to 0,
                        "lastUpdate" to System.currentTimeMillis()
                    )
                ).await()
            }

            aisleId
        } catch (e: Exception) {
            null
        }
    }


    /**
     * Récupère tous les magasins (aisles) sous forme d'un Flow<List<Aisle>>.
     */
    fun fetchAllAisles(): Flow<List<Aisle>> = flow {
        try {
            // Récupérer tous les magasins de la collection "Aisle"
            val querySnapshot = db.collection("Aisle").get().await()

            // Transformer les documents en objets Aisle
            val aisles = querySnapshot.documents.mapNotNull { document ->
                val aisleId = document.getString("aisleId")
                val name = document.getString("name")
                val location = document.getString("location")
                val description = document.getString("description")
                val createdAt = document.getLong("createdAt") ?: 0L

                if (aisleId!=null && name != null && location != null && description != null) {
                    Aisle(aisleId,name, location, description, createdAt)
                } else {
                    null // Ignore les documents mal formatés
                }
            }

            // Émettre la liste des magasins
            emit(aisles)
        } catch (e: Exception) {
            emit(emptyList()) // En cas d'erreur, émettre une liste vide
        }
    }

    /**
     * Récupère la liste des magasins qui ont un médicament en stock.
     * Retourne un Flow<List<Aisle>> contenant les informations des magasins.
     */
    fun getAislesWithMedicine(medicineId: String) = flow {
        try {

            // Récupérer les documents des magasins qui ont le médicament en stock
            val querySnapshot = db.collectionGroup("medicines")
                .whereEqualTo("medicineId", medicineId)
                .whereGreaterThan("quantity", 0)
                .get()
                .await()

            // Transformer les documents récupérés en objets Aisle (magasins)
            val aisles = querySnapshot.documents.mapNotNull { document ->
                val aisleRef = document.reference.parent.parent
                aisleRef?.let {
                    // Récupérer le document du magasin
                    val aisleSnapshot = it.get().await()

                    Aisle(
                        aisleId = aisleSnapshot.id, // L'ID du magasin
                        name = aisleSnapshot.getString("name") ?: "Unknown",
                        location = aisleSnapshot.getString("location") ?: "Unknown",
                        description = aisleSnapshot.getString("description") ?: "No description",
                        createdAt = aisleSnapshot.getLong("createdAt") ?: 0L
                    )
                }
            }

            // Émettre la liste des magasins qui ont un stock du médicament
            emit(aisles)
        } catch (e: Exception) {
            // En cas d'erreur, émettre une liste vide
            emit(emptyList<Aisle>())
        }
    }

    /**
     * Récupère tous les médicaments d'un magasin sous forme d'un Flow<List<MedicineWithStock>>.
     * Les informations des médicaments et de gestion des stocks sont combinées.
     */
    fun fetchMedicinesInAisle(aisleId: String): Flow<List<MedicineWithStock>> = flow {
        try {
            // Récupérer tous les médicaments dans la sous-collection "medicines" d'un magasin
            val querySnapshot = db.collection("aisle")
                .document(aisleId)
                .collection("medicines")
                .get()
                .await()

            // Récupérer les détails des médicaments depuis la collection "medicines" avec l'ID du médicament
            val medicinesWithStock = querySnapshot.documents.mapNotNull { document ->
                val medicineId = document.getString("medicineId") // ID du médicament
                val quantity = document.getString("quantity")?.toIntOrNull() ?: 0 // Quantité du médicament
                val lastUpdate = document.getString("lastUpdate") ?: "Unknown" // Date de dernière mise à jour

                // Vérifier que l'ID du médicament est valide
                if (medicineId != null) {
                    // Récupérer les détails du médicament à partir de la collection principale "medicines"
                    val medicineSnapshot = db.collection("medicines")
                        .document(medicineId)
                        .get()
                        .await()

                    val name = medicineSnapshot.getString("name")
                    val description = medicineSnapshot.getString("description")
                    val dosage = medicineSnapshot.getString("dosage")
                    val manufacturer = medicineSnapshot.getString("manufacturer")
                    val createdAt = medicineSnapshot.getLong("createdAt") ?: 0L

                    // Créer un objet MedicineWithStock si tous les détails sont valides
                    if (name != null && description != null && dosage != null && manufacturer != null) {
                        MedicineWithStock(
                            medicineId = medicineId,
                            name = name,
                            description = description,
                            dosage = dosage,
                            manufacturer = manufacturer,
                            createdAt = createdAt,
                            quantity = quantity,
                            lastUpdate = lastUpdate
                        )
                    } else {
                        null // Ignore les médicaments sans informations complètes
                    }
                } else {
                    null // Ignore les documents mal formatés sans ID de médicament
                }
            }

            // Émettre la liste des médicaments avec leurs informations de stock
            emit(medicinesWithStock)
        } catch (e: Exception) {
            emit(emptyList()) // Si une erreur survient, émettre une liste vide
        }
    }

    //Stock/history ------------------------------------------------------------------------<

    /**
     * Met à jour le stock d'un médicament dans un magasin et ajoute une entrée dans l'historique.
     */
    suspend fun updateStock(
        aisleId: String,
        medicineId: String,
        quantityChange: Int,
        reason: String,
        userEmail: String
    ): Boolean {
        return try {
            val aisleRef = db.collection("Aisle").document(aisleId)
            val medicineRef = aisleRef.collection("medicines").document(medicineId)

            val snapshot = medicineRef.get().await()
            val currentQuantity = snapshot.getLong("quantity") ?: 0

            val newQuantity = (currentQuantity + quantityChange).coerceAtLeast(0)

            // Mettre à jour le stock
            medicineRef.update(
                mapOf(
                    "quantity" to newQuantity,
                    "lastUpdate" to System.currentTimeMillis()
                )
            ).await()

            // Ajouter une entrée dans l'historique
            aisleRef.collection("stockHistory").add(
                mapOf(
                    "date" to System.currentTimeMillis(),
                    "medicineId" to medicineId,
                    "quantity" to quantityChange,
                    "reason" to reason,
                    "type" to if (quantityChange > 0) "add" else "remove",
                    "updateBy" to userEmail
                )
            ).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Récupère le stock total d'un médicament sur tous les magasins.
     */
    suspend fun getTotalStock(medicineId: String): Long {
        return try {
            val querySnapshot = db.collectionGroup("medicines")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()

            querySnapshot.documents.sumOf { it.getLong("quantity") ?: 0 }
        } catch (e: Exception) {
            0
        }
    }


    /**
     * Récupère l'historique du stock pour un magasin spécifique.
     * L'historique est trié par date.
     */
    fun getStockHistoryByAisle(aisleId: String) = flow {
        try {
            val db = FirebaseFirestore.getInstance()

            // Récupérer l'historique du stock pour le magasin (Aisle) donné
            val querySnapshot = db.collection("Aisle")
                .document(aisleId)
                .collection("stockHistory")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            // Transformer les documents récupérés en une liste d'objets stockHistory
            val stockHistoryList = querySnapshot.documents.mapNotNull { document ->
                StockHistory(
                    date = document.getString("date") ?: "",
                    medicineId = document.getString("medicineId") ?: "",
                    medicineName = document.getString("medicineName") ?: "",
                    quantity = document.getLong("quantity") ?: 0,
                    reason = document.getString("reason") ?: "",
                    type = document.getString("type") ?: "",
                    updatedBy = document.getString("updatedBy") ?: ""
                )
            }

            // Émettre la liste des entrées d'historique
            emit(stockHistoryList)
        } catch (e: Exception) {
            emit(emptyList<StockHistory>()) // En cas d'erreur, émettre une liste vide
        }
    }



    //Medicine ------------------------------------------------------------------------<


    /**
     * Ajoute un médicament dans la collection des médicaments et met à jour tous les magasins avec une quantité initiale de 0.
     */
    suspend fun addMedicine(
        name: String,
        description: String,
        dosage: String,
        manufacturer: String
    ): String? {
        return try {
            // Ajouter le médicament dans la collection "medicines"
            val medicineRef = db.collection("medicines").document() // Crée un document avec un ID unique

            val medicineId = medicineRef.id

            // Ajouter les informations du médicament
            medicineRef.set(
                mapOf(
                    "id" to medicineId,
                    "name" to name,
                    "description" to description,
                    "dosage" to dosage,
                    "manufacturer" to manufacturer,
                    "createdAt" to System.currentTimeMillis() // Stocke la date de création en millisecondes
                )
            ).await()

            // Ajouter ce médicament dans tous les magasins avec une quantité initiale de 0
            val aisleSnapshot = db.collection("Aisle").get().await()

            for (aisleDoc in aisleSnapshot.documents) {
                val aisleRef = aisleDoc.reference

                // Ajouter le médicament au stock du magasin avec une quantité de 0
                aisleRef.collection("medicines").document(medicineId).set(
                    mapOf(
                        "medicineId" to medicineId,
                        "quantity" to 0,
                        "lastUpdate" to System.currentTimeMillis()
                    )
                ).await()
            }

            medicineId // Retourne l'ID du médicament ajouté
        } catch (e: Exception) {
            null // Si une erreur se produit, retourner null
        }
    }


    /**
     * Récupère tous les médicaments sous forme d'un Flow<List<Medicine>>.
     */
     fun fetchAllMedicines(): Flow<List<Medicine>> = flow {
        try {
            // Récupérer tous les médicaments de la collection "medicines"
            val querySnapshot = db.collection("medicines").get().await()

            // Transformer les documents en objets Medicine
            val medicines = querySnapshot.documents.mapNotNull { document ->
                val id = document.getString("id")
                val name = document.getString("name")
                val description = document.getString("description")
                val dosage = document.getString("dosage")
                val manufacturer = document.getString("manufacturer")
                val createdAt = document.getLong("createdAt") ?: 0L

                if (id!=null && name != null && description != null && dosage != null && manufacturer != null) {
                    Medicine(id,name, description, dosage, manufacturer, createdAt)
                } else {
                    null // Ignore les documents mal formatés
                }
            }

            // Émettre la liste des médicaments
            emit(medicines)
        } catch (e: Exception) {
            emit(emptyList()) // Si une erreur survient, émettre une liste vide
        }
    }


    /**
     * Supprime un médicament de la collection des médicaments et de toutes les sous-collections des magasins.
     */
    suspend fun deleteMedicine(medicineId: String): Boolean {
        return try {
            // Supprimer le médicament de la collection principale "medicines"
            val medicineRef = db.collection("medicines").document(medicineId)

            // Supprimer le médicament dans la collection "medicines"
            medicineRef.delete().await()

            // Supprimer le médicament dans la sous-collection "medicines" de chaque magasin
            val aisleSnapshot = db.collection("Aisle").get().await()

            for (aisleDoc in aisleSnapshot.documents) {
                val aisleRef = aisleDoc.reference

                // Supprimer le médicament du stock du magasin
                aisleRef.collection("medicines").document(medicineId).delete().await()
            }

            true // Retourne true si la suppression a réussi
        } catch (e: Exception) {
            false // Retourne false en cas d'erreur
        }
    }



    /**
     * Trie tous les médicaments par quantité en stock sur tous les magasins.
     */
    suspend fun getMedicinesSortedByStock(): List<Pair<String, Long>> {
        return try {
            val querySnapshot = db.collectionGroup("medicines")
                .orderBy("quantity", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                val medicineId = doc.getString("medicineId")
                val quantity = doc.getLong("quantity")
                if (medicineId != null && quantity != null) medicineId to quantity else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    /**
     * Trie tous les médicaments par nom (ordre alphabétique).
     */
    suspend fun getMedicinesSortedByName(): List<Map<String, Any>> {
        return try {
            val querySnapshot = db.collection("medicines")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }


    /**
     * Recherche des médicaments par nom (partiel ou complet).
     */
    suspend fun searchMedicinesByName(query: String): List<Map<String, Any>> {
        return try {
            val querySnapshot = db.collection("medicines")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff") // Cela permet de chercher des correspondances partielles
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }





    //User profile ------------------------------------------------------------------------<

    /**
     * Ajoute un utilisateur à la collection "users" dans Firestore.
     * L'ID est auto-généré par Firestore et ajouté à l'objet User.
     */
    suspend fun addUser(user: User): String? {
        return try {

            // Ajouter un utilisateur à la collection "users"
            val userRef = db.collection("users").document() // Génère un ID unique
            val userId = userRef.id // L'ID auto-généré par Firestore

            // Créer un map avec les données de l'utilisateur
            val userMap = mapOf(
                "email" to user.email,
                "firstname" to user.firstname,
                "lastname" to user.lastname,
                "id" to userId,
            )

            // Ajouter les données à la base de données
            userRef.set(userMap).await()

            userId // Retourner l'ID de l'utilisateur
        } catch (e: Exception) {
            null // En cas d'erreur, retourner null
        }
    }


    /**
     * Supprime un utilisateur de la collection "users" dans Firestore.
     */
    suspend fun deleteUser(userId: String): Boolean {
        return try {
            // Récupérer la référence de l'utilisateur à partir de son ID
            val userRef = db.collection("users").document(userId)

            // Supprimer l'utilisateur de la collection "users"
            userRef.delete().await()

            true // Retourne true si la suppression a réussi
        } catch (e: Exception) {
            false // Retourne false en cas d'erreur
        }
    }



    /**
     * Récupère un utilisateur à partir de son email depuis la collection "users".
     */
    fun getUserById(userId: String) = flow {
        try {

            // Récupérer l'utilisateur avec l'email donné
            val querySnapshot = db.collection("users")
                .whereEqualTo("id", userId)
                .get()
                .await()

            // Vérifier si un utilisateur est trouvé et retourner les données
            val user = querySnapshot.documents.firstOrNull()?.let { document ->
                User(
                    id = document.getString("id") ?: "",
                    firstname = document.getString("firstname") ?: "",
                    lastname = document.getString("lastname") ?: "",
                    email = document.getString("email") ?: "",
                )
            }

            // Émettre l'utilisateur ou null si non trouvé
            emit(user)
        } catch (e: Exception) {
            emit(null) // En cas d'erreur, émettre null
        }
    }



}