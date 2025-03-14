package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the AisleDetailScreen.
 * @param aisleRepository The repository for aisles.(hilt injected)
 *
 */
@HiltViewModel
class AisleDetailViewModel @Inject constructor(
    private val aisleRepository: AisleRepository
) : ViewModel() {

    private var _aisle = MutableStateFlow(Aisle())
    val aisle: StateFlow<Aisle> get() = _aisle


    private var _medicines = MutableStateFlow<List<MedicineWithStock>>(emptyList())
    val medicines: StateFlow<List<MedicineWithStock>> get() = _medicines



    // Fonction pour charger le rayon et les médicaments associés
    fun loadAisle(aisleId: String) {
        // Appel asynchrone pour récupérer le rayon
        viewModelScope.launch {
            val fetchedAisle = aisleRepository.fetchAisleById(aisleId)
            if (fetchedAisle != null) {
                _aisle.value = fetchedAisle
            } // Met à jour le rayon

            // Si le rayon est trouvé, récupérer les médicaments associés
            fetchedAisle?.let {
                loadMedicinesForAisle(aisleId)
            }
        }
    }

    // Fonction pour charger les médicaments associés au rayon
    private fun loadMedicinesForAisle(aisleId: String) {
        viewModelScope.launch {
            aisleRepository.fetchMedicinesForAisle(aisleId)
                .collect { medicinesWithStockList ->
                    _medicines.value = medicinesWithStockList
                }
        }
    }



}