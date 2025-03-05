package com.openclassrooms.rebonnte.ui.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterUserScreen(viewModel: AuthenticationViewModel = hiltViewModel(),
                       onNavigateToMainScreen: () -> Unit
) {

    //Register user result from the viewmodel
    val registerUserResult by viewModel.registerResult.collectAsStateWithLifecycle(null)
    // Infos Feedback
    val snackBarHostState = remember { SnackbarHostState() }

    var registerButtonIsEnabled by remember { mutableStateOf(true) }


    // Check if the sign-Up result indicates success or failure
    LaunchedEffect(registerUserResult) {
        registerUserResult?.onSuccess { user ->
            if (user != null) { onNavigateToMainScreen() }
        }?.onFailure { exception ->
            snackBarHostState.showSnackbar("Registering failed: ${exception.message}")
            registerButtonIsEnabled = true
        }
    }







}