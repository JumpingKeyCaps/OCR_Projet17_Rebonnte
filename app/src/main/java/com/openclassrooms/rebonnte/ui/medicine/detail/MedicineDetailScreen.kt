package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.ui.history.HistoryItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Medicine detail screen composable function.
 * @param medicineId ID of the medicine to display.
 * @param viewModel ViewModel for medicine details.(hilt injected)
 */
@Composable
fun MedicineDetailScreen(medicineId: String, viewModel: MedicineDetailViewModel = hiltViewModel()) {

    viewModel.loadMedicine(medicineId)
    viewModel.loadStockHistory(medicineId)
    viewModel.loadMedicineAisle(medicineId)

    val medicineWithStock by viewModel.medicineWithStock.collectAsState()
    val medicineAisle by viewModel.medicineAisle.collectAsState()
    val stockHistory by viewModel.stockHistory.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = medicineWithStock.name,
                onValueChange = {},
                label = { Text("Name") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = medicineAisle?:"Unknown",
                onValueChange = {},
                label = { Text("Aisle") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    //todo UPDATE THE STOCK -//need debouncing with accumulator
                    viewModel.updateStockQuantityEVO(medicineId, -1, "HXq2YyZqfq8ulzvX3HIq", "test")
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minus One"
                    )
                }
                TextField(
                    value = medicineWithStock.quantity.toString(),
                    onValueChange = {},
                    label = { Text("Stock") },
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    //todo update the stock +
                    viewModel.updateStockQuantityEVO(medicineId, 1, "HXq2YyZqfq8ulzvX3HIq", "test")
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Plus One"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            // Convertir la liste en triant par date et heure
            val sortedHistory = stockHistory.sortedByDescending {
                val dateTimeString = "${it.date} ${it.time}" // Combinaison de la date et de l'heure
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss") // Le format de la date et de l'heure
                LocalDateTime.parse(dateTimeString, formatter) // Conversion en LocalDateTime
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedHistory) { history ->
                    HistoryItem(history = history)
                }
            }
        }
    }
}