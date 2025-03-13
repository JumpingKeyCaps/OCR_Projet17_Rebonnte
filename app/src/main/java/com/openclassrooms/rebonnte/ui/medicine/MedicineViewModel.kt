package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor() : ViewModel() {
    var _medicines = MutableStateFlow<MutableList<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines

    init {
        _medicines.value = ArrayList() // Initialiser avec une liste vide
        _medicines.value = mutableListOf(
            Medicine( medicineId = "Medicine 1", "Paracetamol", description = "", dosage = "100mg",),
            Medicine( medicineId = "Medicine 2", "Aspegic", description = "", dosage = "100mg",),
            Medicine( medicineId = "Medicine 3", "Fervex", description = "", dosage = "100mg",)
        )
    }

    fun addRandomMedicine(aisles: List<Aisle>) {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.add(
            Medicine(
                medicineId = "Medicine " + (currentMedicines.size + 1),
                name = Random().nextInt(100).toString(),
              //  stock = Random().nextInt(100),
            //    nameAisle = aisles[Random().nextInt(aisles.size)].name,
            //    histories = emptyList()
            )
        )
        _medicines.value = currentMedicines
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

