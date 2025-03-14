package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random
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


    // _medicinesWithStock est un MutableLiveData pour exposer les données à l'UI
    private val _medicinesWithStock = MutableStateFlow<List<MedicineWithStock>>(emptyList())
    val medicinesWithStock: StateFlow<List<MedicineWithStock>> get() = _medicinesWithStock


    init {
        // Récupérer les médicaments au démarrage
      //  fetchAllMedicines()
        fetchAllMedicinesWithStock()
    }


    /**
     * Fetch all medicines from the database.
     * @param sortDsc Sort medicines in descending order.
     */
    private fun fetchAllMedicines(sortDsc: Boolean = true) {
        viewModelScope.launch {
            medicineRepository.fetchAllMedicines(sortDsc).collect { medicinesList ->
                // Mettre à jour le StateFlow avec la nouvelle liste de médicaments
                _medicines.value = medicinesList.filterNotNull().toMutableList() // Exclut les éléments null
            }
        }
    }


    /**
     * Fetch all medicines with stock from the database.
     * @param sortDsc Sort medicines in descending order.
     */
    private fun fetchAllMedicinesWithStock(sortDsc: Boolean = true) {
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
        viewModelScope.launch {
            medicineRepository.searchMedicinesRealTime(searchQuery).collect { medicinesList ->
                _medicines.value = medicinesList.filterNotNull().toMutableList() // Mise à jour des médicaments filtrés
            }
        }
    }


    fun filterByName(name: String) {
        val currentMedicines: List<Medicine> = medicines.value
        val filteredMedicines: MutableList<Medicine> = ArrayList()
        for (medicine in currentMedicines) {
            if (medicine.name.lowercase(Locale.getDefault())
                    .contains(name.lowercase(Locale.getDefault()))
            ) {
                filteredMedicines.add(medicine)
            }
        }
        _medicines.value = filteredMedicines
    }

    fun sortByNone() {
        _medicines.value = medicines.value.toMutableList() // Pas de tri
    }

    fun sortByName() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparing(Medicine::name))
        _medicines.value = currentMedicines
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
       // currentMedicines.sortWith(Comparator.comparingInt(Medicine::stock))
        _medicines.value = currentMedicines
    }
}

