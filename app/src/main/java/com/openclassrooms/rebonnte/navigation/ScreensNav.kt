package com.openclassrooms.rebonnte.navigation

import androidx.navigation.NamedNavArgument

sealed class ScreensNav( val route: String,
val navArguments: List<NamedNavArgument> = emptyList()
) {

    data object Main : ScreensNav("main")

    data object SignIn : ScreensNav("signIn")

    data object SignUp : ScreensNav("signUp")

    data object NoInternet : ScreensNav("noInternet")


    data object MedicineDetails : ScreensNav("medicineDetails/{medicineId}"){
        fun createRoute(medicineId: String) = "medicineDetails/$medicineId"
    }

    data object AisleDetails : ScreensNav("aisleDetails/{aisleId}"){
        fun createRoute(aisleId: String) = "aisleDetails/$aisleId"
    }

    data object AddMedicine : ScreensNav("addMedicine")

    data object AddAisle : ScreensNav("addAisle")

    data object Medicines : ScreensNav("medicines")

    data object Aisles : ScreensNav("aisles")




}