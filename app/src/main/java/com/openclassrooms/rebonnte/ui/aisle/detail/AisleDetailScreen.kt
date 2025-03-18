package com.openclassrooms.rebonnte.ui.aisle.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.openclassrooms.rebonnte.ui.medicine.composition.MedicineWithStockItem
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green3

/**
 * Composition of the Aisle Detail Screen.
 * @param aisleId The ID of the aisle to display.
 * @param viewModel The ViewModel associated with the screen.(hilt injected)
 * @param onMedicineClicked Callback when a medicine item is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(aisleId: String, viewModel: AisleDetailViewModel = hiltViewModel(), onMedicineClicked: (String) -> Unit, onBackClick: () -> Unit) {
    viewModel.loadAisle(aisleId)
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())

    val aisle by viewModel.aisle.collectAsState()

    // Gérer l'affichage du dialog pour la suppression
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = aisle.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = aisleId != viewModel.DEFAULT_AISLE_ID
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Description
            item {
                Text(
                    text = aisle.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color.LightGray,
                    modifier = Modifier.padding(30.dp)
                )
            }

            // Info Section (Total Stock & Reference ID)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(5.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).align(Alignment.CenterHorizontally),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Aisle Stock", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text("${medicines.sumOf { it.quantity }}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(5.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).align(Alignment.CenterHorizontally),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Aisle Reference ID", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(aisle.aisleId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                }
            }

            // Ligne de séparation
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("${medicines.size} ${if (medicines.size <= 1) "medicine reference" else "medicines references"}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                            .align(Alignment.CenterVertically),
                        thickness = 1.dp,
                        color = Rebonnte_green3
                    )
                }

            }
            if (medicines.isEmpty()) {
                item {

                    Spacer(modifier = Modifier.height(56.dp))
                    Text(
                        text = "No medicine associated to this aisle yet!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }

            } else {
                // Liste des médicaments
                items(medicines, key = { it.medicineId }) { medicine ->
                    MedicineWithStockItem(
                        medicineWS = medicine,
                        onClick = { onMedicineClicked(medicine.medicineId) }
                    )
                }
            }
        }

        // Dialog de confirmation pour la suppression de l'aisle
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmation of deletion") },
                text = { Text("Are you sure you want to delete this aisle ?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Appel à la méthode de suppression dans le ViewModel
                            viewModel.deleteAisle(aisleId)
                            showDeleteDialog = false
                            Toast.makeText(context, "Aisle deleted with success !", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
