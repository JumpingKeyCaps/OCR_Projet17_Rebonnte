package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun MedicineDetailScreen(medicineId: String, viewModel: MedicineDetailViewModel = hiltViewModel()) {

   viewModel.loadMedicine(medicineId)
    val medicine by viewModel.medicine.collectAsState()
    //  var stock by remember { mutableStateOf(medicine.stock) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = medicine.name,
                onValueChange = {},
                label = { Text("Name") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = "",//medicine.nameAisle
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
                    //todo UPDATE THE STOCK -
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minus One"
                    )
                }
                TextField(
                    value = "stock.toString()",
                    onValueChange = {},
                    label = { Text("Stock") },
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    //todo update the stock +
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
            //   LazyColumn(modifier = Modifier.fillMaxSize()) {
            //      items(medicine.histories) { history ->
            //          HistoryItem(history = history)
            //      }
            //  }
        }
    }
}