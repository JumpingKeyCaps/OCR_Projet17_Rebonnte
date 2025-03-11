package com.openclassrooms.rebonnte.navigation

import androidx.navigation.NamedNavArgument

sealed class ScreensNav( val route: String,
val navArguments: List<NamedNavArgument> = emptyList()
) {

    data object Main : ScreensNav("main")

    data object SignIn : ScreensNav("signIn")

    data object SignUp : ScreensNav("signUp")

    data object NoInternet : ScreensNav("noInternet")

}