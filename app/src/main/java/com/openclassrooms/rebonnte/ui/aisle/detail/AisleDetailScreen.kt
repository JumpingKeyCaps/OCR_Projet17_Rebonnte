package com.openclassrooms.rebonnte.ui.aisle.detail

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailActivity
import com.openclassrooms.rebonnte.ui.medicine.MedicineWithStockItem


@Composable
fun AisleDetailScreen(aisleId: String, viewModel: AisleDetailViewModel = hiltViewModel()) {
    viewModel.loadAisle(aisleId)
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val context = LocalContext.current
    Log.d("AisleDetailScreen","Aisle ID : $aisleId")

    Scaffold { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(medicines) { medicine ->
                MedicineWithStockItem(medicine = medicine, onClick = { name ->
                    val intent = Intent(context, MedicineDetailActivity::class.java).apply {
                        putExtra("nameMedicine", name)
                    }
                    context.startActivity(intent)
                })
            }
        }
    }
}

