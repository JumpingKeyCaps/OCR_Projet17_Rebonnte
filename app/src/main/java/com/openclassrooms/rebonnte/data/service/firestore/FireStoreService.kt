package com.openclassrooms.rebonnte.data.service.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.domain.Stock
import com.openclassrooms.rebonnte.domain.StockHistory
import com.openclassrooms.rebonnte.domain.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Service to interact with FireStore (DataBase).
 */
class FireStoreService {
    private val db = FirebaseFirestore.getInstance("rebonnte")


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
     * @param medicine The Medicine object to add.
     * @param aisleId The ID of the aisle to add the medicine to.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun addMedicine(medicine: Medicine, aisleId: String?): Boolean {
        return try {
            // Vérifier si un médicament avec le même nom existe déjà
            val querySnapshot = db.collection("medicines")
                .whereEqualTo("name", medicine.name)
                .get()
                .await()

            // Si un médicament avec le même nom existe déjà, retourner false
            if (querySnapshot.isEmpty) {
                // Si aucun médicament avec ce nom, ajouter le nouveau médicament
                val docRef = db.collection("medicines").document()
                val formattedMedicine = medicine.copy(createdAt = System.currentTimeMillis())
                docRef.set(formattedMedicine).await()

                // Créer une entrée dans la collection 'stock' avec un modèle dédié
                val stock = Stock(
                    medicineId = docRef.id,
                    aisleId = if (aisleId.isNullOrEmpty()) "0phZ52jwfLfhd7ri8PqH" else aisleId, // Aisle ID par défaut
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
    fun fetchAllMedicines(sortDsc: Boolean): Flow<List<Medicine?>> = flow {
        // Fetch all medicines from Firestore
        val snapshot = db.collection("medicines")
            .orderBy("name", if (sortDsc) Query.Direction.DESCENDING else Query.Direction.ASCENDING)
            .get()
            .await() // Await the initial data fetch.

        // Map the snapshot to a list of Medicine objects
        val medicines = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Medicine::class.java)?.copy(medicineId = doc.id)
        }

        // Emit the list of medicines
        emit(medicines)
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
     * Get all medicines for a specific aisle.
     * @param aisleId The ID of the aisle.
     * @return A Flow emitting a list of medicines that will emit new values when the data changes.
     */
    fun fetchMedicinesForAisle(aisleId: String): Flow<List<MedicineWithStock>> = flow {
        // 1. Récupérer tous les stocks pour ce rayon
        val stockSnapshot = db.collection("stock")
            .whereEqualTo("aisleId", aisleId)
            .get()
            .await() // Utilise `await` pour attendre le résultat de la requête

        // 2. Pour chaque stock, récupérer le médicament correspondant
        val medicinesWithStock = stockSnapshot.documents.mapNotNull { stockDoc ->
            val stock = stockDoc.toObject(Stock::class.java)
            stock?.let {
                // 3. Récupérer le médicament associé au stock
                val medicine = fetchMedicineById(it.medicineId)
                medicine?.let {it->
                    // 4. Créer un objet `MedicineWithStock`
                    MedicineWithStock(
                        medicineId = it.medicineId,
                        name = it.name,
                        description = it.description,
                        dosage = it.dosage,
                        fabricant = it.fabricant,
                        indication = it.indication,
                        principeActif = it.principeActif,
                        utilisation = it.utilisation,
                        warning = it.warning,
                        createdAt = it.createdAt,
                        quantity = stock.quantity)


                }
            }
        }

        // 5. Retourner la liste des `MedicineWithStock`
        emit(medicinesWithStock)
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
            .orderBy("name")
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

    /**
     * Updates the quantity of a stock entry for a given medicineId.
     * @param medicineId The ID of the medicine to update.
     * @param quantityDelta The change in quantity to apply to the stock entry.
     * @param author The author of the update.
     * @param description A description of the update.
     * @return True if the operation is successful, false otherwise.
     */
    suspend fun updateQuantityInStock(
        medicineId: String,
        quantityDelta: Int,
        author: String,
        description: String
    ): Boolean {
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

            // Récupération du nom du médicament depuis la collection "medicine"
            val medicineDoc = db.collection("medicines").document(medicineId).get().await()
            val medicineName = medicineDoc.getString("name") ?: "Unknown"  // Utilise "name" pour récupérer le nom du médicament

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
                    quantity = quantityDelta,
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

    /**
     * Retrieves the stock quantity for a given medicineId.
     * @param medicineId The ID of the medicine to retrieve the stock for.
     * @return The stock quantity for the given medicineId, or null if no stock entry is found or an error occurs.
     */
    suspend fun getStockQuantity(medicineId: String): Int? {
        return try {
            // Recherche de l'entrée de stock correspondante au medicineId
            val stockDocs = db.collection("stock")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()

            if (stockDocs.isEmpty) {
                // Si aucun stock n'est trouvé pour ce medicineId, retourner null
                null
            } else {
                // Récupérer la quantité du premier document trouvé
                stockDocs.documents.firstOrNull()?.getLong("quantity")?.toInt() ?: 0
            }
        } catch (e: Exception) {
            // En cas d'erreur, retourner null
            null
        }
    }

    fun getStockQuantityFlow(medicineId: String): Flow<Int> = flow {
        // Recherche de l'entrée de stock correspondante au medicineId
        val stockDocs = try {
            db.collection("stock")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .await()
        } catch (e: Exception) {
            // Handle the error here instead of emitting from catch
            emit(0)  // Fallback when there's an error
            return@flow  // Exit flow after emitting error fallback value
        }

        if (stockDocs.isEmpty) {
            // Si aucun stock n'est trouvé pour ce medicineId, émettre 0
            emit(0)
        } else {
            // Récupérer la quantité du premier document trouvé et émettre la quantité
            val quantity = stockDocs.documents.firstOrNull()?.getLong("quantity")?.toInt() ?: 0
            emit(quantity)
        }
    }






    /**
     * Retrieves the aisleId for a given medicineId.
     * @param medicineId The ID of the medicine to retrieve the aisleId for.
     * @return The aisleId for the given medicineId, or null if no aisleId is found.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun getMedicineAisle(medicineId: String): Flow<String?> = callbackFlow {
        val query = db.collection("stock")
            .whereEqualTo("medicineId", medicineId)
            .limit(1)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Fermeture propre en cas d'erreur
                return@addSnapshotListener
            }

            val aisleId = snapshot?.documents?.firstOrNull()?.getString("aisleId")

            if (!isClosedForSend) { // Vérifie si le Flow est encore actif
                trySend(aisleId).isSuccess
            }
        }

        awaitClose { listener.remove() }
    }.catch { emit(null) } // Gestion des erreurs sans casser le Flow


    /**
     * Get the current date in the format "dd-MM-yyyy".
     * @return The current date as a string.
     */
    private fun getCurrentDate(): String {
        // Retourne la date actuelle au format souhaité (par exemple : "20-04-2025")
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormatter.format(Date())
    }

    /**
     * Get the current time in the format "HH:mm:ss".
     * @return The current time as a string.
     */
    private fun getCurrentTime(): String {
        // Retourne l'heure actuelle au format souhaité (par exemple : "12:34:03")
        val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormatter.format(Date())
    }

    //History ------------------------------------------------------------------------<

    /**
     * Get the history for a specific medicine.
     * @param medicineId The ID of the medicine.
     * @return A Flow emitting a list of stock history entries.
     */
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

        // Nettoyage de l'écouteur lorsque le Flow est terminé
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Get all history.
     * @return A Flow emitting a list of stock history entries.
     */
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
     * Add a new user to the Firestore collection.
     * @param user The user to add.
     * @return The ID of the added user, or null if an error occurred.
     */
    suspend fun addUser(user: User): String? {
        return try {

            // Ajouter un utilisateur à la collection "users"
            val userRef = db.collection("users").document(user.id) // Génère un ID unique

            // Créer un map avec les données de l'utilisateur
            val userMap = mapOf(
                "email" to user.email,
                "firstname" to user.firstname,
                "lastname" to user.lastname,
                "id" to user.id,
            )

            // Ajouter les données à la base de données
            userRef.set(userMap).await()

            user.id // Retourner l'ID de l'utilisateur
        } catch (e: Exception) {
            null // En cas d'erreur, retourner null
        }
    }

    /**
     * Delete a user from the Firestore collection.
     * @param userId The ID of the user to delete.
     * @return True if the deletion was successful, false otherwise.
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
     * Get a user by their ID.
     * @param userId The ID of the user to retrieve.
     * @return A Flow emitting the user object or null if not found.
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