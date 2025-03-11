package com.openclassrooms.rebonnte.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import com.openclassrooms.rebonnte.ui.medicine.Medecine

/**
 * Main screen composable function.
 *  - Manages the inner navigation graph for the Aisle and Medicine screens.
 *  - Manages the bottom navigation bar.
 */
@Composable
fun MainScreen(
    onLogOutAction: () -> Unit,
    onAddMedicineAction: () -> Unit,
    onMedicineClicked: (Medecine) -> Unit,
    onAisleClicked: (String) -> Unit
) {

    //todo main screen contentment for Aisle and Medicine screens

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Aisle") },
                    selected = true,
                    onClick = {  }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Medicine") },
                    selected = false,
                    onClick = {  }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
               // if (route == "medicine") {
                    //                   medicineViewModel.addRandomMedicine(aisleViewModel.aisles.value)
             //   } else if (route == "aisle") {
                    //                   aisleViewModel.addRandomAisle()
             //   }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        Scaffold(modifier = Modifier.padding(it)) {
            Box(modifier = Modifier.fillMaxWidth().padding(it)) {

            }
        }



    }


}
