package com.openclassrooms.rebonnte.data.service.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.Stock
import com.openclassrooms.rebonnte.domain.StockHistory
import com.openclassrooms.rebonnte.domain.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Service to interact with FireStore (DataBase).
 */
class FireStoreService {
    private val db = FirebaseFirestore.getInstance()


    //Aisles ------------------------------------------------------------------------<
    /**
     * Adds a new aisle to the Firestore collection.
     * Automatically sets the aisleId and createdAt timestamp.
     * Checks if an aisle with the same name already exists.
     * @param name The name of the aisle.
     * @param description The description of the aisle.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun addAisle(name: String, description: String): Boolean {
        return try {
            // Vérifier si un rayon avec le même nom existe déjà
            val querySnapshot = db.collection("Aisle")
                .whereEqualTo("name", name)
                .get()
                .await()

            // Si un rayon avec le même nom existe déjà, retourner false
            if (querySnapshot.isEmpty) {
                // Si aucun rayon avec ce nom, ajouter le nouveau rayon
                val docRef = db.collection("Aisle").document()
                val aisle = Aisle(
                    aisleId = docRef.id,
                    name = name,
                    description = description,
                    createdAt = System.currentTimeMillis()
                )
                docRef.set(aisle).await()
                true
            } else {
                // Rayon avec le même nom existe déjà
                false
            }
        } catch (e: Exception) {
            false
        }
    }


    /**
     * Deletes an aisle from the Firestore collection by its aisleId.
     * @param aisleId The ID of the aisle to delete.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun deleteAisle(aisleId: String): Boolean {
        return try {
            db.collection("Aisle")
                .document(aisleId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }


    /**
     * Fetches all aisles from the Firestore collection.
     * Returns a Flow of a list of aisles that will emit new values when the data changes.
     * @return A Flow emitting a list of aisles.
     */
    fun fetchAllAisles(): Flow<List<Aisle?>> = callbackFlow {
        val listener = db.collection("Aisle")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // If there's an error, close the flow with the exception
                    return@addSnapshotListener
                }

                // If snapshot is not null, convert it to a list of Aisle objects
                val aisles = snapshot?.documents?.map { doc ->
                    doc.toObject(Aisle::class.java)?.copy(aisleId = doc.id) // Mapping to Aisle
                } ?: emptyList()

                trySend(aisles) // Emit the list of aisles to the flow
            }

        // Clean up the listener when the flow is cancelled
        awaitClose { listener.remove() }
    }


    /**
     * Fetches a single aisle from the Firestore collection by its aisleId.
     * @param aisleId The ID of the aisle to fetch.
     * @return The Aisle object if found, or null if not found.
     */
    suspend fun fetchAisleById(aisleId: String): Aisle? {
        return try {
            val documentSnapshot = db.collection("Aisle")
                .document(aisleId)
                .get()
                .await()

            // Check if document exists, if yes, return the Aisle object
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Aisle::class.java)?.copy(aisleId = documentSnapshot.id)
            } else {
                null // Return null if the aisle doesn't exist
            }
        } catch (e: Exception) {
            null // Return null in case of any error (can be handled in the ViewModel)
        }
    }



    //Medicines ------------------------------------------------------------------------<

    /**
     * Adds a new medicine to the Firestore collection.
     * Automatically sets the medicineID and createdAt timestamp.
     * Checks if a medicine with the same name already exists.
     * Creates a default entry in the 'stock' collection with a default aisleId and quantity = 0.
     * @param name The name of the medicine.
     * @param dosage The dosage instructions for the medicine.
     * @param fabricant The manufacturer of the medicine.
     * @param indication The indication for the use of the medicine.
     * @param principeActif The active ingredient of the medicine.
     * @param utilisation The usage instructions for the medicine.
     * @param warning Any warning for the use of the medicine.
     * @return True if the operation is successful, false otherwise.
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
        return try {
            // Vérifier si un médicament avec le même nom existe déjà
            val querySnapshot = db.collection("medicines")
                .whereEqualTo("name", name)
                .get()
                .await()

            // Si un médicament avec le même nom existe déjà, retourner false
            if (querySnapshot.isEmpty) {
                // Si aucun médicament avec ce nom, ajouter le nouveau médicament
                val docRef = db.collection("medicines").document()
                val medicine = Medicine(
                    medicineId = docRef.id,
                    name = name,
                    dosage = dosage,
                    fabricant = fabricant,
                    indication = indication,
                    principeActif = principeActif,
                    utilisation = utilisation,
                    warning = warning,
                    createdAt = System.currentTimeMillis()
                )
                docRef.set(medicine).await()

                // Créer une entrée dans la collection 'stock' avec un modèle dédié
                val stock = Stock(
                    medicineId = docRef.id,
                    aisleId = "0phZ52jwfLfhd7ri8PqH" // Aisle ID par défaut
                )
                db.collection("stock").add(stock).await()

                true
            } else {
                // Médicament avec le même nom existe déjà
                false
            }
        } catch (e: Exception) {
            false
        }
    }


    /**
     * Deletes a medicine from the Firestore collection by its medicineID.
     * Also deletes the corresponding entry in the 'stock' collection.
     * @param medicineId The ID of the medicine to delete.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun deleteMedicine(medicineId: String): Boolean {
        return try {
            // Supprimer le médicament de la collection "medicines"
            db.collection("medicines").document(medicineId).delete().await()

            // Supprimer l'entrée correspondante dans la collection "stock"
            db.collection("stock")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()
                .documents
                .forEach { stockDoc ->
                    db.collection("stock").document(stockDoc.id).delete().await()
                }

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Fetches all medicines from the Firestore collection.
     * Returns a Flow of a list of medicines that will emit new values when the data changes.
     * @return A Flow emitting a list of medicines.
     */
    fun fetchAllMedicines(sortDsc: Boolean): Flow<List<Medicine?>> = callbackFlow {
        val listener = db.collection("medicines")
            .orderBy("name", if (sortDsc) Query.Direction.DESCENDING else Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // If there's an error, close the flow with the exception
                    return@addSnapshotListener
                }

                // If snapshot is not null, convert it to a list of Medicine objects
                val medicines = snapshot?.documents?.map { doc ->
                    doc.toObject(Medicine::class.java)?.copy(medicineId = doc.id) // Mapping to Medicine
                } ?: emptyList()

                trySend(medicines) // Emit the list of medicines to the flow
            }

        // Clean up the listener when the flow is cancelled
        awaitClose { listener.remove() }
    }


    /**
     * Fetches a single medicine from the Firestore collection by its medicineID.
     * @param medicineId The ID of the medicine to fetch.
     * @return The Medicine object if found, or null if not found.
     */
    suspend fun fetchMedicineById(medicineId: String): Medicine? {
        return try {
            val documentSnapshot = db.collection("medicines")
                .document(medicineId)
                .get()
                .await()

            // Check if document exists, if yes, return the Medicine object
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Medicine::class.java)?.copy(medicineId = documentSnapshot.id)
            } else {
                null // Return null if the medicine doesn't exist
            }
        } catch (e: Exception) {
            null // Return null in case of any error (can be handled in the ViewModel)
        }
    }


    /**
     * Fetches medicines based on the search query in real-time.
     * Returns a Flow of a list of medicines that will emit new values when the data changes.
     * @param searchQuery The search text entered by the user.
     * @return A Flow emitting a list of medicines that match the search query.
     */
    fun searchMedicinesRealTime(searchQuery: String): Flow<List<Medicine?>> = callbackFlow {
        // Si le texte est vide, on retourne une liste vide
        if (searchQuery.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("medicines")
            .orderBy("name") // Assurez-vous que le champ "name" est indexé dans Firestore
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff") // Utilisation de "\uf8ff" pour filtrer tous les noms qui commencent par searchQuery
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // Si une erreur se produit, on ferme le flux
                    return@addSnapshotListener
                }

                // Si le snapshot est valide, on mappe les documents en objets Medicine
                val medicines = snapshot?.documents?.map { doc ->
                    doc.toObject(Medicine::class.java)?.copy(medicineId = doc.id) // Mapping to Medicine
                } ?: emptyList()

                trySend(medicines) // On envoie la liste de médicaments dans le flux
            }

        // Nettoyer le listener lorsque le flux est annulé
        awaitClose { listener.remove() }
    }


    //Stock ------------------------------------------------------------------------<

    /**
     * Updates the aisleId of a stock entry for a given medicineId.
     * @param medicineId The ID of the medicine to update.
     * @param newAisleId The new aisleId to set for the stock entry.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun updateMedicineAisle(medicineId: String, newAisleId: String): Boolean {
        return try {
            // Recherche de l'entrée de stock correspondante au medicineId
            val stockDocs = db.collection("stock")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()

            if (stockDocs.isEmpty) {
                // Si aucun stock n'est trouvé pour ce medicineId, retour false
                return false
            }

            // Mettre à jour l'aisleId pour chaque document trouvé dans "stock"
            stockDocs.documents.forEach { stockDoc ->
                val updatedStock = mapOf(
                    "aisleId" to newAisleId // Mise à jour du aisleId
                )

                // Mise à jour du document stock
                db.collection("stock").document(stockDoc.id).update(updatedStock).await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }


    suspend fun updateQuantityInStock(medicineId: String, quantityDelta: Int, author: String, description: String): Boolean {
        return try {
            // Recherche de l'entrée de stock correspondante au medicineId
            val stockDocs = db.collection("stock")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()

            if (stockDocs.isEmpty) {
                // Si aucun stock n'est trouvé pour ce medicineId, retourner false
                return false
            }

            // Récupération des informations du médicament pour l'historique
            val medicineName = stockDocs.documents.firstOrNull()?.getString("medicineName") ?: "Unknown"

            // Mise à jour de la quantité pour chaque document trouvé dans "stock"
            stockDocs.documents.forEach { stockDoc ->
                // Récupération de la quantité actuelle
                val currentQuantity = stockDoc.getLong("quantity")?.toInt() ?: 0

                // Calcul de la nouvelle quantité
                var newQuantity = currentQuantity + quantityDelta

                // Si la nouvelle quantité est inférieure à 0, empêcher la mise à jour
                if (newQuantity < 0) newQuantity = 0

                // Mise à jour de la quantité dans le document stock
                db.collection("stock").document(stockDoc.id)
                    .update("quantity", newQuantity)
                    .await()

                // Ajout dans l'historique
                val history = StockHistory(
                    date = getCurrentDate(),
                    time = getCurrentTime(),
                    medicineId = medicineId,
                    medicineName = medicineName,
                    quantity = quantityDelta.toLong(),
                    action = if (quantityDelta > 0) "ADD" else "REMOVE",  // Action selon le delta
                    description = description,
                    author = author
                )

                // Ajouter l'historique dans la collection "history"
                db.collection("history").add(history)
                    .await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getCurrentDate(): String {
        // Retourne la date actuelle au format souhaité (par exemple : "20-04-2025")
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormatter.format(Date())
    }

    private fun getCurrentTime(): String {
        // Retourne l'heure actuelle au format souhaité (par exemple : "12:34:03")
        val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormatter.format(Date())
    }

    //History ------------------------------------------------------------------------<


    fun getHistoryForMedicine(medicineId: String): Flow<List<StockHistory>> = callbackFlow {
        val listenerRegistration = db.collection("history")
            .whereEqualTo("medicineId", medicineId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    // En cas d'erreur, nous envoyons une exception dans le Flow
                    trySend(emptyList()) // Ou envoyer une erreur si nécessaire
                    return@addSnapshotListener
                }

                // Conversion des documents en objets StockHistory
                val historyList = querySnapshot?.documents?.mapNotNull { document ->
                    document.toObject(StockHistory::class.java)
                } ?: emptyList()

                // Envoi de la nouvelle liste d'historique dans le Flow
                trySend(historyList)
            }

        // Nettoyage de l'écouteur lorsque le Flow est terminé (par exemple, lors de la fermeture de la view ou du fragment)
        awaitClose {
            listenerRegistration.remove()
        }
    }


    fun getAllHistory(): Flow<List<StockHistory>> = callbackFlow {
        val listenerRegistration = db.collection("history")
            .orderBy("date", Query.Direction.DESCENDING)  // Trie par date, du plus récent au plus ancien
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    // En cas d'erreur, nous envoyons une exception dans le Flow
                    trySend(emptyList()) // Ou envoyer une erreur si nécessaire
                    return@addSnapshotListener
                }

                // Conversion des documents en objets StockHistory
                val historyList = querySnapshot?.documents?.mapNotNull { document ->
                    document.toObject(StockHistory::class.java)
                } ?: emptyList()

                // Envoi de la nouvelle liste d'historique dans le Flow
                trySend(historyList)
            }

        // Nettoyage de l'écouteur lorsque le Flow est terminé
        awaitClose {
            listenerRegistration.remove()
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