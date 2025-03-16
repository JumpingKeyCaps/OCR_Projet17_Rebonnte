package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for medicines.
 * @param medicineRepository Repository for medicines.
 * @param stockRepository Repository for stock.
 */
@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val stockRepository: StockRepository
) : ViewModel() {
    private var _medicines = MutableStateFlow<MutableList<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines
    private val _medicinesWithStock = MutableStateFlow<List<MedicineWithStock>>(emptyList())
    val medicinesWithStock: StateFlow<List<MedicineWithStock>> get() = _medicinesWithStock

    init {
        fetchAllMedicinesWithStock()
    }


    /**
     * Fetch all medicines with stock from the database.
     * @param sortDsc Sort medicines in descending order.
     */
    private fun fetchAllMedicinesWithStock(sortDsc: Boolean = false) {
        viewModelScope.launch {
            // Collecte tous les médicaments depuis le repository
            medicineRepository.fetchAllMedicines(sortDsc).collect { medicinesList ->
                val medicinesWithStockList = mutableListOf<MedicineWithStock>()

                if (medicinesList.isNotEmpty()) {
                    // Pour chaque médicament récupéré, on récupère la quantité de stock associée
                    for (medicine in medicinesList) {
                        if (medicine != null) {
                            // Récupérer la quantité de stock pour chaque médicament
                            val quantity = stockRepository.getStockQuantity(medicine.medicineId) ?: 0

                            // Créer l'objet MedicineWithStock et l'ajouter à la liste
                            val medicineWithStock = MedicineWithStock(
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
                            medicinesWithStockList.add(medicineWithStock)
                        }
                    }
                }

                // Mettre à jour le StateFlow avec la nouvelle liste de médicaments avec stock
                _medicinesWithStock.value = medicinesWithStockList
            }
        }
    }


    /**
     * Add a new medicine to the database.
     * @param name Name of the medicine.
     * @param dosage Dosage of the medicine.
     * @param fabricant Fabricant of the medicine.
     * @param indication Indication of the medicine.
     * @param principeActif Princípe actif of the medicine.
     * @param utilisation Utilisation of the medicine.
     * @param warning Warning of the medicine.
     * @return True if the medicine was successfully added, false otherwise.
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
        return medicineRepository.addMedicine(
            name, dosage, fabricant, indication, principeActif, utilisation, warning
        )
    }

    /**
     * Delete a medicine from the database.
     * @param medicineId Id of the medicine to delete.
     * @return True if the medicine was successfully deleted, false otherwise.
     */
    suspend fun deleteMedicine(medicineId: String): Boolean {
        return medicineRepository.deleteMedicine(medicineId)
    }

    /**
     * Search medicines in the database.
     * @param searchQuery Query to search medicines.
     */
    fun searchMedicinesRealTime(searchQuery: String) {

        if (searchQuery.isEmpty() || searchQuery.isBlank()) {
            fetchAllMedicinesWithStock()
        }else{
            viewModelScope.launch {
                // Collecte tous les médicaments depuis le repository
                val formattedQuery = searchQuery.replaceFirstChar { it.uppercase() }
                medicineRepository.searchMedicinesRealTime(formattedQuery).collect { medicinesList ->
                    val medicinesWithStockList = mutableListOf<MedicineWithStock>()

                    if (medicinesList.isNotEmpty()) {
                        // Pour chaque médicament récupéré, on récupère la quantité de stock associée
                        for (medicine in medicinesList) {
                            if (medicine != null) {
                                // Récupérer la quantité de stock pour chaque médicament
                                val quantity = stockRepository.getStockQuantity(medicine.medicineId) ?: 0

                                // Créer l'objet MedicineWithStock et l'ajouter à la liste
                                val medicineWithStock = MedicineWithStock(
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
                                medicinesWithStockList.add(medicineWithStock)
                            }
                        }
                    }

                    // Mettre à jour le StateFlow avec la nouvelle liste de médicaments avec stock
                    _medicinesWithStock.value = medicinesWithStockList
                }
            }
        }
    }

    /**
     * Sort medicines by name.
     */
    fun sortByName() {
        val sortedList = medicinesWithStock.value.sortedBy { it.name }
        _medicinesWithStock.value = sortedList.map { it.copy() } // Copie chaque élément
    }

    /**
     * Sort medicines by stock.
     */
    fun sortByStock() {
        val sortedList = medicinesWithStock.value.sortedByDescending { it.quantity }
        _medicinesWithStock.value = sortedList.map { it.copy() } // Copie chaque élément
    }
}

