package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.domain.StockHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for medicine details.
 * @param medicineRepository Repository for medicines.
 * @param stockRepository Repository for stock.
 * @param historyRepository Repository for stock history.
 * @param userRepository Repository for users.
 * @param aisleRepository Repository for aisles.
 */
@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val stockRepository: StockRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository,
    private val aisleRepository: AisleRepository
) : ViewModel() {

    private var _medicine = MutableStateFlow(Medicine())
    val medicine: StateFlow<Medicine> get() = _medicine


    private val _medicineWithStock = MutableStateFlow<MedicineWithStock>(MedicineWithStock())
    val medicineWithStock: StateFlow<MedicineWithStock> get() = _medicineWithStock

    private val _stockHistory = MutableStateFlow<List<StockHistory>>(emptyList())
    val stockHistory: StateFlow<List<StockHistory>> get() = _stockHistory

    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess: StateFlow<Boolean?> get() = _updateSuccess


    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> get() = _deleteSuccess


    private val _medicineAisle = MutableStateFlow<String?>(null)
    val medicineAisle: StateFlow<String?> get() = _medicineAisle


    /**
     * Load medicine details.
     * @param medicineId ID of the medicine to load.
     */
    fun loadMedicine(medicineId: String) {
        viewModelScope.launch {
            val medicine = medicineRepository.fetchMedicineById(medicineId)
            if (medicine != null) {
                // Récupérer la quantité du stock de manière suspendue
                val quantity = stockRepository.getStockQuantity(medicineId) ?: 0

                // Mettre à jour l'état avec les données récupérées
                _medicineWithStock.value = MedicineWithStock(
                    medicineId = medicine.medicineId,
                    name = medicine.name,
                    description = medicine.description,
                    dosage = medicine.dosage,
                    fabricant = medicine.fabricant,
                    indication = medicine.indication,
                    principeActif = medicine.principeActif,
                    utilisation = medicine.utilisation,
                    warning = medicine.warning,
                    createdAt = medicine.createdAt,
                    quantity = quantity
                )
            }
        }
    }

    /**
     * Load stock history for a medicine.
     * @param medicineId ID of the medicine to load stock history for.
     */
    fun loadStockHistory(medicineId: String) {
        viewModelScope.launch {
            historyRepository.getHistoryForMedicine(medicineId).collect { historyList ->
                _stockHistory.value = historyList
            }
        }
    }


    /**
     * Load the aisle of a medicine.
     * @param medicineId ID of the medicine to load the aisle for.
     */
    fun loadMedicineAisle(medicineId: String) {
        viewModelScope.launch {
            val aisleId = medicineRepository.getMedicineAisle(medicineId).firstOrNull()
            if (aisleId != null) {
                val aisle = aisleRepository.fetchAisleById(aisleId)
                _medicineAisle.value = aisle?.name ?: "Unknown"
            }
        }
    }


    /**
     * Update the aisle of a medicine.
     * @param medicineId ID of the medicine to update the aisle for.
     * @param newAisleId New aisle ID to assign to the medicine.
     */
    fun updateMedicineAisle(medicineId: String, newAisleId: String) {
        viewModelScope.launch {
            val success = stockRepository.updateMedicineAisle(medicineId, newAisleId)
            _updateSuccess.value = success
            if (success) {
                loadMedicine(medicineId) // Recharger les données après mise à jour
            }
        }
    }



    // Variables pour le debouncing et l'accumulation
    private var quantityDelta = 0 // Accumulateur de changements de quantité
    private var debounceJob: Job? = null // Job pour le délai
    private val DEBOUNCE_TIME = 1000L // Délai de 1 seconde

    /**
     * Update the stock quantity of a medicine with debouncing.
     * @param medicineId ID of the medicine to update the stock quantity for.
     * @param delta Delta to add to the current stock quantity.
     * @param userId ID of the user making the update.
     * @param description Description of the update.
     */
    fun updateStockQuantityEVO(medicineId: String, delta: Int, userId: String, description: String) {
        quantityDelta += delta // Accumuler les changements
        _medicineWithStock.value = _medicineWithStock.value.copy(
            quantity = _medicineWithStock.value.quantity + delta // Mettre à jour l'UI immédiatement
        )
        scheduleUpdate(medicineId, userId, description) // Planifier la mise à jour
    }

    /**
     * Schedule the update of the stock quantity.
     * @param medicineId ID of the medicine to update the stock quantity for.
     * @param userId ID of the user making the update.
     * @param description Description of the update.
     */
    private fun scheduleUpdate(medicineId: String, userId: String, description: String) {
        debounceJob?.cancel() // Annuler le job précédent
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_TIME) // Attendre le délai
            performUpdate(quantityDelta,medicineId, userId, description) // Mettre à jour Firestore

        }
    }

    /**
     * Perform the update of the stock quantity.
     * @param totalQuantityDelta Total delta to add to the current stock quantity.
     * @param medicineId ID of the medicine to update the stock quantity for.
     * @param userId ID of the user making the update.
     * @param description Description of the update.
     */
    private fun performUpdate(totalQuantityDelta:Int ,medicineId: String, userId: String, description: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId).collect { user ->
                val authorEmail = user?.email ?: "Unknown"
                val success = stockRepository.updateQuantityInStock(medicineId, totalQuantityDelta, authorEmail, description)
                _updateSuccess.value = success

                if (success) {
                    loadMedicine(medicineId) // Recharger les données après mise à jour
                    loadStockHistory(medicineId) // Recharger l'historique
                }

                quantityDelta = 0 // Réinitialiser l'accumulateur

            }
        }
    }

    /**
     * Delete a medicine.
     * @param medicineId ID of the medicine to delete.
     */
    fun deleteMedicine(medicineId: String) {
        viewModelScope.launch {
            val success = medicineRepository.deleteMedicine(medicineId)
            _deleteSuccess.value = success
        }
    }

    /**
     * Reset the update success state.
     */
    fun resetUpdateSuccess() {
        _updateSuccess.value = null
    }
}