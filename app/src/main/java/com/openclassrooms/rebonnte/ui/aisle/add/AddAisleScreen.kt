package com.openclassrooms.rebonnte.ui.aisle.add

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

/**
 * Add aisle screen composable function.
 * @param onBackClick Callback for when the back button is clicked.
 * @param onAisleAdded Callback for when an aisle is added.
 * @param viewModel ViewModel for the aisle screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAisleScreen(
    onBackClick: () -> Unit,
    onAisleAdded: () -> Unit,
    viewModel: AddAisleViewModel = hiltViewModel()
) {
    val aisleCreationResult by viewModel.aisleCreationResult.collectAsState(null)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    var aisleName by remember { mutableStateOf("") }
    var aisleDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // FocusRequester for handling keyboard navigation
    val focusRequester = FocusRequester.Default

    // When aisle creation result changes
    LaunchedEffect(aisleCreationResult) {
        aisleCreationResult?.let {
            isLoading = false
            if (it.isSuccess) {
                snackbarHostState.showSnackbar("Aisle added successfully!")
                onAisleAdded()
            } else {
                snackbarHostState.showSnackbar("Error adding aisle. Please try again.")
            }
        }
    }

    // When error message changes
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            isLoading = false
            snackbarHostState.showSnackbar(it)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Aisle") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->

            Box(Modifier.fillMaxSize().padding(padding)){

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter)
                        .padding(start = 32.dp, end = 32.dp, top = 0.dp, bottom = 0.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(56.dp))
                    Text(
                        text = "Set the fields to add a new aisle",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = aisleName,
                        onValueChange = { aisleName = it },
                        label = { Text("Aisle Name") },
                        placeholder = { Text("Enter the aisle name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = aisleDescription,
                        onValueChange = { aisleDescription = it },
                        label = { Text("Aisle Description") },
                        placeholder = { Text("Enter aisle description") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.addAisle(aisleName, aisleDescription)
                            },
                            modifier = Modifier.fillMaxWidth().height(64.dp)
                        ) {
                            Text("Add Aisle")
                        }
                    }

                    Spacer(modifier = Modifier.height(46.dp))
                }

                SnackbarHost(hostState = snackbarHostState,modifier = Modifier.align(Alignment.BottomCenter))
            }


        }
    )






}