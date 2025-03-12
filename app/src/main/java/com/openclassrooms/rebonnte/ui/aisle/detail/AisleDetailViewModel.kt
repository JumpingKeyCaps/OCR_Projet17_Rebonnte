package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AisleDetailViewModel @Inject constructor() : ViewModel() {

    private var _aisle = MutableStateFlow(Aisle())
    val aisle: StateFlow<Aisle> get() = _aisle


    private var _medicines = MutableStateFlow<List<MedicineWithStock>>(emptyList())
    val medicines: StateFlow<List<MedicineWithStock>> get() = _medicines


    init {
        _medicines.value = listOf(
            MedicineWithStock("Medicine 1", "Paracetamol", description = "", dosage = "100mg", manufacturer = "Pfizer", createdAt = 0, quantity = 23, lastUpdate = ""),
            MedicineWithStock("Medicine 2", "Aspegic", description = "", dosage = "100mg", manufacturer = "Moderna", createdAt = 0, quantity = 11, lastUpdate = ""),
            MedicineWithStock("Medicine 3", "Fervex", description = "", dosage = "100mg", manufacturer = "Zen", createdAt = 0, quantity = 54, lastUpdate = "")

        )
    }


    //todo add method

    fun loadAisle(aisleId: String) {
        //todo asynch call to get the aisle
        _aisle.value = Aisle(aisleId)

    }
}