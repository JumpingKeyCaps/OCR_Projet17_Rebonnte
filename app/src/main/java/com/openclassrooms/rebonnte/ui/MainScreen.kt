package com.openclassrooms.rebonnte.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.navigation.ScreensNav
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen

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
    onMedicineClicked: (MedicineWithStock) -> Unit,
    onAisleClicked: (Aisle) -> Unit
) {

    //todo main screen contentment for Aisle and Medicine screens
    val currentUser = FirebaseAuth.getInstance().currentUser
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route


    Scaffold(
        topBar = {}, // let empty, TopBar is managed by screens inside the inner-navigation graph
        bottomBar = {  BottomNavigationBar(currentRoute = currentRoute, navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (currentRoute == ScreensNav.Aisles.route) {

                  //  navController.navigate(ScreensNav.AddAisle.route)

               } else if (currentRoute == ScreensNav.Medicines.route) {
                  //  navController.navigate(ScreensNav.AddMedicine.route)
               }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
            InnerNavigationGraph(
                navController = navController,
                startDestination = ScreensNav.Aisles.route,
                onMedicineClicked = onMedicineClicked,
                onAisleClicked = onAisleClicked,
                onLogOutAction = onLogOutAction)

        }
    }


}

/**
 * Inner navigation graph composable function.
 * @param navController Navigation controller for the inner graph.
 * @param startDestination Start destination route for the inner graph.
 * @param onMedicineClicked Callback for when a medicine is clicked.
 * @param onAisleClicked Callback for when an aisle is clicked.
 * @param onLogOutAction Callback for when the user logs out.
 */
@Composable
fun InnerNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    onMedicineClicked: (MedicineWithStock) -> Unit,
    onAisleClicked: (Aisle) -> Unit,
    onLogOutAction: () -> Unit ){

    NavHost(navController, startDestination = startDestination, Modifier.padding(0.dp)) {
        //-- Aisle Screen
        composable(route = ScreensNav.Aisles.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut() }
        ) {
            AisleScreen(
                onAisleClicked = {onAisleClicked(it)},
                onLogOutAction = { onLogOutAction() },
            )
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


/**
 * Bottom navigation bar composable function.
 * @param currentRoute Current route of the navigation graph.
 * @param navController Navigation controller for the bottom navigation bar.
 */
@Composable
fun BottomNavigationBar(currentRoute: String?, navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Aisle") },
            selected = currentRoute == ScreensNav.Aisles.route,
            onClick = {
                navController.navigate(ScreensNav.Aisles.route){
                    // Avoid building up a large stack of destinations
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true  }
                    // Avoid multiple copies of the same destination
                    launchSingleTop = true
                    // Restore state when re selecting a previously selected item
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Medicine") },
            selected = currentRoute == ScreensNav.Medicines.route,
            onClick = {
                navController.navigate(ScreensNav.Medicines.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}