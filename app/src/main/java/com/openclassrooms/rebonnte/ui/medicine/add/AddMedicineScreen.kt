package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine

/**
 * Add medicine screen composable function.
 * @param onBackClick Callback for when the back button is clicked.
 * @param onMedicineAdded Callback for when a medicine is added.
 * @param viewModel ViewModel for the medicine screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onBackClick: () -> Unit,
    onMedicineAdded: () -> Unit,
    viewModel: AddMedicineViewModel = hiltViewModel()
){

    val availableAisles by viewModel.aisles.collectAsState()
    val medicineCreationResult by viewModel.medicineCreationResult.collectAsState(null)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var fabricant by remember { mutableStateOf("") }
    var indication by remember { mutableStateOf("") }
    var principeActif by remember { mutableStateOf("") }
    var utilisation by remember { mutableStateOf("") }
    var warning by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = FocusRequester.Default


    // Quand le résultat de la création de médicament change
    LaunchedEffect(medicineCreationResult) {
        medicineCreationResult?.let {
            isLoading = false
            if (it.isSuccess) {
                snackbarHostState.showSnackbar("Medicine added successfully!")
                onMedicineAdded()

            } else {
                snackbarHostState.showSnackbar("Error adding medicine. Please try again.")
            }
        }
    }

    // Quand le message d'erreur change
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            isLoading = false
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->

            Box(Modifier.fillMaxSize().padding(padding)){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(start = 32.dp, end = 32.dp, top = 6.dp, bottom = 0.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(56.dp))
                    Text(
                        text = "Set all the fields of the medicine to add",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center, // Centré horizontalement
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        label = { Text("Medicine Name") },
                        placeholder = { Text("Enter the name of the medicine") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = principeActif,
                        onValueChange = { principeActif = it },
                        label = { Text("Active substance") },
                        placeholder = { Text("Enter active substance") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = fabricant,
                        onValueChange = { fabricant = it },
                        label = { Text("Manufacturer") },
                        placeholder = { Text("Enter the manufacturer") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Enter a description") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = indication,
                        onValueChange = { indication = it },
                        label = { Text("Indication") },
                        placeholder = { Text("Enter indications for use") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = utilisation,
                        onValueChange = { utilisation = it },
                        label = { Text("Usage") },
                        placeholder = { Text("Enter usage instructions") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = warning,
                        onValueChange = { warning = it },
                        label = { Text("Warning") },
                        placeholder = { Text("Enter any warnings") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        placeholder = { Text("Enter the dosage") }, // Hint text
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select an aisle to this medicine",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center, // Centré horizontalement
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Ajout du menu déroulant des allées
                    AisleDropdown(
                        aisles = availableAisles,
                        selectedAisle = selectedAisle,
                        onAisleSelected = { selectedAisle = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {

                        Button(
                            onClick = {

                                isLoading = true

                                val newMedicine = Medicine(
                                    name = medicineName,
                                    dosage = dosage,
                                    fabricant = fabricant,
                                    indication = indication,
                                    principeActif = principeActif,
                                    utilisation = utilisation,
                                    warning = warning,
                                    description = description,
                                )
                                viewModel.addMedicine(
                                    newMedicine,
                                    selectedAisle
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(64.dp)
                        ) {
                            Text("Add Medicine")
                        }
                    }
                    Spacer(modifier = Modifier.height(46.dp))
                }

                SnackbarHost(hostState = snackbarHostState,modifier = Modifier.align(Alignment.BottomCenter))
            }


        }
    )
}


@Composable
fun AisleDropdown(aisles: List<Aisle>, selectedAisle: String?, onAisleSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedAisle ?: "Sélectionner une allée") }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            Text(selectedText)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            aisles.forEach { aisle ->
                DropdownMenuItem(
                    text = { Text(aisle.name) },
                    onClick = {
                        selectedText = aisle.name
                        onAisleSelected(aisle.aisleId)
                        expanded = false
                    }
                )
            }
        }
    }
}