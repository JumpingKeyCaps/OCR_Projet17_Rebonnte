package com.openclassrooms.rebonnte.ui.medicine

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.ui.medicine.composition.EmbeddedSearchBar
import com.openclassrooms.rebonnte.ui.medicine.composition.MedicineWithStockItem

/**
 * Medicine screen composable function.
 * @param viewModel ViewModel for medicines.(hilt injected)
 * @param onBackClick Callback to handle back button click.
 * @param onMedicineClicked Callback to handle medicine click.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onMedicineClicked: (MedicineWithStock) -> Unit
    ) {
    val medicinesWithStock by viewModel.medicinesWithStock.collectAsState(initial = emptyList())
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        EmbeddedSearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                viewModel.searchMedicinesRealTime(it)
                            },
                            onCloseSearch = {
                                isSearchActive = false
                                searchQuery = ""
                                viewModel.searchMedicinesRealTime("")
                            }
                        )
                    } else {
                        Text(text = "Medicines")
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.sortByName()
                                    expanded = false
                                },
                                text = { Text("Sort by Name") }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.sortByStock()
                                    expanded = false
                                },
                                text = { Text("Sort by Stock") }
                            )
                        }
                    }

                }
            )
        }
    ){paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(medicinesWithStock, key = { it.medicineId }) { medicinesWithStock ->
                MedicineWithStockItem(medicineWS = medicinesWithStock, onClick = {
                    onMedicineClicked(medicinesWithStock)
                })
            }
        }
    }
}



