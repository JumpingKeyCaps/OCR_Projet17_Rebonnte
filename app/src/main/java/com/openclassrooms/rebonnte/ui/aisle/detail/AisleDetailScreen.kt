package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

import com.openclassrooms.rebonnte.ui.medicine.composition.MedicineWithStockItem

/**
 * Composition of the Aisle Detail Screen.
 * @param aisleId The ID of the aisle to display.
 * @param viewModel The ViewModel associated with the screen.(hilt injected)
 * @param onMedicineClicked Callback when a medicine item is clicked.
 */
@Composable
fun AisleDetailScreen(aisleId: String, viewModel: AisleDetailViewModel = hiltViewModel(), onMedicineClicked: (String) -> Unit) {
    viewModel.loadAisle(aisleId)
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    Scaffold { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(medicines) { medicine ->
                MedicineWithStockItem(medicineWS = medicine, onClick = { medicineId ->
                    onMedicineClicked(medicineId)
                })
            }
        }
    }
}

