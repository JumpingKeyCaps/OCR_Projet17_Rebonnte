package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
        refreshMedicines()
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
                    // Lancer la récupération des quantités de stock en parallèle
                    val stockQuantityDeferreds = medicinesList.mapNotNull { medicine ->
                        medicine?.let {
                            async {
                                // Collecter la quantité de stock en utilisant Flow
                                stockRepository.getStockQuantityFlow(it.medicineId).first()
                            }
                        }
                    }

                    // Attendre que toutes les quantités de stock soient récupérées
                    val stockQuantities = stockQuantityDeferreds.awaitAll()

                    // Créer les objets MedicineWithStock
                    medicinesWithStockList.addAll(
                        medicinesList.zip(stockQuantities) { medicine, quantity ->
                            // Ensure that the medicine is not null before using it
                            val nonNullMedicine = medicine ?: return@zip null  // If medicine is null, skip this entry

                            // Use a default value (e.g., 0) if quantity is null
                            val nonNullQuantity = quantity ?: 0

                            // Create the MedicineWithStock object with non-null values
                            MedicineWithStock(
                                medicineId = nonNullMedicine.medicineId,
                                name = nonNullMedicine.name,
                                description = nonNullMedicine.description,
                                dosage = nonNullMedicine.dosage,
                                fabricant = nonNullMedicine.fabricant,
                                indication = nonNullMedicine.indication,
                                principeActif = nonNullMedicine.principeActif,
                                utilisation = nonNullMedicine.utilisation,
                                warning = nonNullMedicine.warning,
                                createdAt = nonNullMedicine.createdAt,
                                quantity = nonNullQuantity
                            )
                        }.filterNotNull()  // Ensure that null values are removed after the zip operation
                    )

                    // Mettre à jour le StateFlow avec la nouvelle liste de médicaments avec stock
                    _medicinesWithStock.value = medicinesWithStockList
                }
            }
        }
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

    /**
     * Refresh the list of medicines.
     */
    fun refreshMedicines(){
        fetchAllMedicinesWithStock()
    }

}

