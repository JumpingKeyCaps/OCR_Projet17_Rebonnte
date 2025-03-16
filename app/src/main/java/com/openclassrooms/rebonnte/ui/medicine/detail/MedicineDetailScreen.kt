package com.openclassrooms.rebonnte.ui.medicine.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.ui.history.HistoryItem
import com.openclassrooms.rebonnte.ui.theme.Purple40
import com.openclassrooms.rebonnte.ui.theme.Purple80
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_black
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green2
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green3
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Medicine detail screen composable function.
 * @param medicineId ID of the medicine to display.
 * @param viewModel ViewModel for medicine details.(hilt injected)
 * @param onBackClick Callback to handle back button click.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: String,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    userId: String
) {
    // Charger les données
    viewModel.loadMedicine(medicineId)
    viewModel.loadStockHistory(medicineId)
    viewModel.loadMedicineAisle(medicineId)

    // Collecter les états
    val medicineWithStock by viewModel.medicineWithStock.collectAsState()
    val medicineAisle by viewModel.medicineAisle.collectAsState()
    val stockHistory by viewModel.stockHistory.collectAsState()

    val updateSuccess by viewModel.updateSuccess.collectAsState()

    // État pour le BottomSheet
    val density = LocalDensity.current
    val bottomSheetState = remember { ModalBottomSheetState(ModalBottomSheetValue.Hidden, density) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var colorStockUpdateIndicator = remember { false }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current



    LaunchedEffect(updateSuccess) {
        if(updateSuccess != null && updateSuccess == true){
            val lastStockHistoryEntry = stockHistory.maxByOrNull {
                LocalDateTime.parse("${it.date} ${it.time}", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            }
            if(lastStockHistoryEntry != null){
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Stock updated ! : ${lastStockHistoryEntry.action} ${if(lastStockHistoryEntry.quantity < 0) (-1)*lastStockHistoryEntry.quantity else lastStockHistoryEntry.quantity}  unit(s): " +
                                "${if(lastStockHistoryEntry.quantity < 0)medicineWithStock.quantity + lastStockHistoryEntry.quantity else medicineWithStock.quantity - lastStockHistoryEntry.quantity } -> ${medicineWithStock.quantity} unit(s)")
                }
            }
            colorStockUpdateIndicator = false
            viewModel.resetUpdateSuccess()
        }
    }



    // Utilisation de ModalBottomSheetLayout
    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .height(LocalConfiguration.current.screenHeightDp.dp * 1.0f)
            ) {
                // Afficher l'historique dans le BottomSheet
                Column(modifier = Modifier.padding(6.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Stock History", style = MaterialTheme.typography.titleLarge, color = Color.White,
                        modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Trier l'historique par date
                    val sortedHistory = stockHistory.sortedByDescending {
                        val dateTimeString = "${it.date} ${it.time}"
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                        LocalDateTime.parse(dateTimeString, formatter)
                    }

                    // Afficher l'historique dans une LazyColumn
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sortedHistory) { history ->
                            HistoryItem(history = history)
                        }
                    }
                }
            }

        },
        sheetBackgroundColor = Rebonnte_black,
        sheetShape = RoundedCornerShape(5)
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = medicineWithStock.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center, // Centré horizontalement
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            // Ouvrir le BottomSheet
                            scope.launch {
                                bottomSheetState.show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Afficher l'historique"
                            )
                        }
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Supprimer le medicament"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
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
            Box(modifier= Modifier.fillMaxSize()) {
                // Contenu scrollable
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 0.dp, bottom = 0.dp, start = 26.dp, end = 26.dp)
                        .verticalScroll(rememberScrollState()) // Ajout du défilement vertical
                ) {
                    Spacer(modifier = Modifier.height(20.dp))



                    // Afficher l'aisle du médicament
                    Card(
                        modifier = Modifier.padding(2.dp).align(Alignment.Start),
                        shape = RoundedCornerShape(100),
                        colors = CardDefaults.cardColors(
                            containerColor = Rebonnte_green2
                        )
                    ) {
                        Text(
                            text = medicineAisle ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    // Afficher le nom
                    Text(
                        text = medicineWithStock.name,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontSize = 35.sp,
                        color = Purple80,
                        modifier = Modifier.padding(2.dp).align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medicineWithStock.principeActif,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Start,
                        color = Purple40,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(2.dp).align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(26.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 40.dp)
                            .align(Alignment.CenterHorizontally),
                        thickness = 0.5.dp,
                        color = Rebonnte_green3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Current quantity in stock",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Rebonnte_green2,
                        textAlign = TextAlign.Center // Centré horizontalement
                    )
                    // Quantité en stock avec boutons fléchés
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 6.dp, start = 36.dp, end = 36.dp)
                            .background(Rebonnte_black,shape = RoundedCornerShape(50))
                            .height(64.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(48.dp) // Taille du bouton flèche
                                .background(Rebonnte_green3, shape = CircleShape)
                                .align(Alignment.CenterVertically),
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Minus One",
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                // Décrémenter la quantité en stock
                                colorStockUpdateIndicator = true
                                viewModel.updateStockQuantityEVO(medicineId, -1, userId, "Stock Update")
                            }
                        )
                        // Afficher la quantité en stock
                        Text(
                            text = medicineWithStock.quantity.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(colorStockUpdateIndicator)  Purple80 else Color.White,
                            textAlign = TextAlign.Center // Centré horizontalement
                        )
                        // Bouton flèche vers le haut (augmentation de quantité)
                        IconButton(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(48.dp) // Taille du bouton flèche
                                .background(Rebonnte_green3, shape = CircleShape)
                                .align(Alignment.CenterVertically),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Plus One",
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                // Incrémenter la quantité en stock
                                colorStockUpdateIndicator = true
                                viewModel.updateStockQuantityEVO(medicineId, 1, userId, "Stock Update")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        thickness = 1.dp,
                        color = Rebonnte_green3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ){
                        //ID code ---
                        Column(modifier = Modifier.fillMaxWidth().weight(1.0f) ) {
                            Text(
                                text = "Identification code",
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                modifier = Modifier.padding(3.dp).align(Alignment.CenterHorizontally)
                            )
                            Card(
                                modifier = Modifier.padding(3.dp).align(Alignment.CenterHorizontally).fillMaxWidth(),
                                shape = RoundedCornerShape(100),
                                colors = CardDefaults.cardColors(
                                    containerColor = Rebonnte_black
                                )
                            ) {
                                Text(
                                    text = medicineWithStock.medicineId,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                )
                            }


                        }
                        Column(modifier = Modifier.fillMaxWidth().weight(1.0f) ) {
                            //Frabicant ---
                            Text(
                                text = "Manufacturer",
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Start,
                                color = Color.Gray,
                                modifier = Modifier.padding(3.dp).align(Alignment.CenterHorizontally),
                            )
                            Card(
                                modifier = Modifier.padding(3.dp).align(Alignment.CenterHorizontally).fillMaxWidth(),
                                shape = RoundedCornerShape(100),
                                colors = CardDefaults.cardColors(
                                    containerColor = Rebonnte_black
                                )
                            ) {
                                Text(
                                    text = medicineWithStock.fabricant,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        thickness = 1.dp,
                        color = Rebonnte_green3
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    //indication ---
                    Text(
                        text = "Indication",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        color = Color.Gray,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = medicineWithStock.indication,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    //utilisation ---
                    Text(
                        text = "Utilisation",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        color = Color.Gray,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = medicineWithStock.utilisation,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    //Dosage ---
                    Text(
                        text = "Dosage",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        color = Color.Gray,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = medicineWithStock.dosage,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    //warning ---
                    Text(
                        text = "Warning",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        color = Color.Gray,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = medicineWithStock.warning,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp)
                    )
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }
            // Dialog de confirmation pour la suppression de la medicine
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete this medicine ?") },
                    text = { Text("Are you sure you want to delete this medicine ?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Appel à la méthode de suppression dans le ViewModel
                                viewModel.deleteMedicine(medicineId)
                                showDeleteDialog = false
                                Toast.makeText(context, "Medicine deleted with success !", Toast.LENGTH_SHORT).show()
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
}