package com.openclassrooms.rebonnte.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassrooms.rebonnte.ui.MainScreen
import com.openclassrooms.rebonnte.ui.authentication.LoginScreen
import com.openclassrooms.rebonnte.ui.authentication.RegisterUserScreen
import com.openclassrooms.rebonnte.ui.noInternet.NoInternetScreen

@Composable
fun RebonnteNavHost (
    navHostController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
    ) {

        //Main screen
        composable(route = ScreensNav.Main.route){
            //todo main screen conteneur
            MainScreen(
                onLogOutAction = {
                    navHostController.navigate(ScreensNav.SignIn.route) {
                        //clean the nav backstack
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onAddMedicineAction = {
                    navHostController.navigate(ScreensNav.AddMedicine.route)
                },
                onMedicineClicked = {
                    navHostController.navigate(ScreensNav.MedicineDetails.route)
                },
                onAisleClicked = {
                    navHostController.navigate(ScreensNav.AisleDetails.route)
                }

            )
        }


        //Medicine details screen
        composable(route = ScreensNav.MedicineDetails.route,
            arguments = ScreensNav.MedicineDetails.navArguments
        ){ backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId")
                ?: throw IllegalArgumentException("Medicine ID is required")
           // MedicineDetailsScreen(medicineId = medicineId)
        }

        //Aisle Details screen
        composable(route = ScreensNav.AisleDetails.route,
            arguments = ScreensNav.AisleDetails.navArguments
        ){backStackEntry ->
            val aisleId = backStackEntry.arguments?.getString("aisleId")
                ?: throw IllegalArgumentException("Aisle ID is required")
          //  AisleDetailsScreen(aisleId = aisleId))
        }


        //Add medicine screen
        composable(route = ScreensNav.AddMedicine.route){
          //  AddMedicineScreen(onBackClick = { navHostController.popBackStack() })
        }
        //Add Aisle screen
        composable(route = ScreensNav.AddAisle.route){
            //  AddAisleScreen(onBackClick = { navHostController.popBackStack() })
        }


        //Login screen
        composable(route = ScreensNav.SignIn.route){
            LoginScreen(
                onNavigateToMainScreen = {
                    navHostController.navigate(ScreensNav.Main.route) {
                        //clean the nav backstack
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onNavigateToRegisterScreen = { navHostController.navigate(ScreensNav.SignUp.route) }
            )
        }

        //Register screen
        composable(route = ScreensNav.SignUp.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
            }


            ){
            RegisterUserScreen(
                onNavigateToMainScreen = { navHostController.navigate(ScreensNav.Main.route) },
                onNavigateToLoginScreen = { navHostController.navigate(ScreensNav.SignIn.route) }
            )
        }

        //No internet screen
        composable(route = ScreensNav.NoInternet.route){
            NoInternetScreen()
        }



    }

}