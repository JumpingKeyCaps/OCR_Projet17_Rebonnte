package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.domain.Aisle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the AisleScreen.
 * @param aisleRepository The repository for aisles.(hilt injected)
 */
@HiltViewModel
class AisleViewModel @Inject constructor(
    private val aisleRepository: AisleRepository
) : ViewModel() {

    private var _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles

    private val _aisleActionResult = MutableSharedFlow<Result<Boolean>>()
    val aisleActionResult: SharedFlow<Result<Boolean>> get() = _aisleActionResult

    init {
        // Récupérer tous les rayons au lancement du ViewModel
        fetchAllAisles()
    }

    /**
     * Fetch all aisles from the database.
     */
    private fun fetchAllAisles() {
        viewModelScope.launch {
            aisleRepository.fetchAllAisles()
                .collect { aislesList ->
                    _aisles.value = aislesList.filterNotNull() // Ignorer les éléments nuls
                }
        }
    }

    /**
     * Add a new aisle to the database.
     * @param name The name of the aisle.
     * @param description The description of the aisle.
     */
    fun addAisle(name: String, description: String) {
        viewModelScope.launch {
            try {
                val isSuccess = aisleRepository.addAisle(name, description)
                if (isSuccess) {
                    _aisleActionResult.emit(Result.success(true))
                } else {
                    _aisleActionResult.emit(Result.failure(Exception("Failed to add aisle")))
                }
            } catch (e: Exception) {
                _aisleActionResult.emit(Result.failure(e))
            }
        }
    }

    /**
     * Delete an aisle from the database.
     * @param aisleId The ID of the aisle to delete.
     */
    fun deleteAisle(aisleId: String) {
        viewModelScope.launch {
            try {
                val isSuccess = aisleRepository.deleteAisle(aisleId)
                if (isSuccess) {
                    _aisleActionResult.emit(Result.success(true))
                } else {
                    _aisleActionResult.emit(Result.failure(Exception("Failed to delete aisle")))
                }
            } catch (e: Exception) {
                _aisleActionResult.emit(Result.failure(e))
            }
        }
    }


}

