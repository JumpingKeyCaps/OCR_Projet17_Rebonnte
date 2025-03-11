package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor() : ViewModel() {
    var _medicines = MutableStateFlow<MutableList<Medecine>>(mutableListOf())
    val medicines: StateFlow<List<Medecine>> get() = _medicines

    init {
        _medicines.value = ArrayList() // Initialiser avec une liste vide
    }

    fun addRandomMedicine(aisles: List<Aisle>) {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.add(
            Medecine(
                id = "Medicine " + (currentMedicines.size + 1),
                name = Random().nextInt(100).toString(),
                stock = Random().nextInt(100),
                nameAisle = aisles[Random().nextInt(aisles.size)].name,
                histories = emptyList()
            )
        )
        _medicines.value = currentMedicines
    }

    fun filterByName(name: String) {
        val currentMedicines: List<Medecine> = medicines.value
        val filteredMedicines: MutableList<Medecine> = ArrayList()
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
        currentMedicines.sortWith(Comparator.comparing(Medecine::name))
        _medicines.value = currentMedicines
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparingInt(Medecine::stock))
        _medicines.value = currentMedicines
    }
}

