package com.openclassrooms.rebonnte.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.navigation.ScreensNav
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen

import com.openclassrooms.rebonnte.ui.medicine.Medecine
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen

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


@Composable
fun InnerNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    onMedicineClicked: (Medecine) -> Unit,
    onAisleClicked: (Aisle) -> Unit){

    NavHost(navController, startDestination = startDestination, Modifier.padding(0.dp)) {
        //-- Aisle Screen
        composable(route = ScreensNav.Aisles.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut() }
        ) {
            AisleScreen(onAisleClicked = {onAisleClicked(it)})
        }
        //-- medicines screen
        composable(route = ScreensNav.Medicines.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut() }
        ) {
            MedicineScreen(
                onMedicineClicked = { onMedicineClicked(it) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}