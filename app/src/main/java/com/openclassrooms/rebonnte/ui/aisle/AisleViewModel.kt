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




}

