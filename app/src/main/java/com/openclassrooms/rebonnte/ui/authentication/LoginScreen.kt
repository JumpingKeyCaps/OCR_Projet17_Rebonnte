package com.openclassrooms.rebonnte.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_black
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green1
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green2
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green3
import kotlinx.coroutines.launch

/**
 * Composable for the login screen.
 * @param viewModel The view model for authentication operations.
 * @param onNavigateToMainScreen The action to perform when navigating to the main screen.
 * @param onNavigateToRegisterScreen The action to perform when navigating to the register screen.
 */
@Composable
fun LoginScreen(viewModel: AuthenticationViewModel = hiltViewModel(),
                onNavigateToMainScreen: () -> Unit,
                onNavigateToRegisterScreen: () -> Unit
) {
    //todo ---  UI layout for login will go here


    //Coroutine Scope
    val coroutineScope = rememberCoroutineScope()

    // Password recovery
    var showRecoveryPasswordDialog by remember { mutableStateOf(false) }
    var recoveryPasswordEmail by remember { mutableStateOf("") }

    // Infos Feedback
    val snackBarHostState = remember { SnackbarHostState() }

    //Sign-in result from the viewmodel
    val signInResult by viewModel.signInResult.collectAsStateWithLifecycle(null)



    // Champs de connexion
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var signInButtonIsEnabled by remember { mutableStateOf(true) }

    // Détection d'erreurs (exemple simple)
    val isEmailError = false
    val isPasswordError = false

    // Check if the sign-in result indicates success or failure
    LaunchedEffect(signInResult) {
        signInResult?.onSuccess { user ->
            if (user != null) {
                onNavigateToMainScreen() // Success : Navigate to the Main Screen
            }
        }?.onFailure { exception ->
            //Failure : Show a snackbar with the error message and unlock the button to retry
            snackBarHostState.showSnackbar("Sign in failed: ${exception.message}")
            signInButtonIsEnabled = true
        }
    }



    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Rebonnte_green1,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = Rebonnte_green1,
        focusedIndicatorColor = Rebonnte_green2,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
    )




    // --- THE COMPOSITION ----
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Rebonnte_black,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 0.dp, bottom = 0.dp, start = 22.dp, end = 22.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {


            // Titre principal
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Thin,
                    color = Color.White
                ),
                textAlign = TextAlign.Start,
                fontSize = 42.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image décorative en haut
            Image(
                painter = painterResource(id = R.drawable.signindoodle),
                contentDescription = "Illustration de connexion",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Please connect you with your Rebonnte account.",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )
            Spacer(modifier = Modifier.height(15.dp))

            // Champ Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = {
                    Text(
                        "Enter your email",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                isError = isEmailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email, // Icône pour représenter l'email
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, end = 26.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(8.dp)

            )

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = {
                    Text(
                        "Enter your password",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                isError = isPasswordError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(start = 26.dp, end = 26.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Bouton "Se connecter"
            Button(
                onClick = {
                    signInButtonIsEnabled = false
                    viewModel.signInUser(email, password)
                },
                enabled = signInButtonIsEnabled,
                modifier = Modifier.fillMaxWidth()
                    .height(55.dp)
                    .padding(start = 36.dp, end = 36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rebonnte_green2, // Couleur de fond
                    contentColor = Color.White // Couleur du texte
                )
            ) {
                Text("Sign in")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                /* Mot de passe oublié */
                recoveryPasswordEmail = email
                if (email.isNotEmpty()) {
                    showRecoveryPasswordDialog = true // show dialog

                } else {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("An email is required for password recovery")
                    }
                }
            }) {
                Text(
                    "Forgotten password ?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Rebonnte_green3
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row {
                Text(
                    "No account yet ? ",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 2.dp)
                )
                TextButton(onClick = { onNavigateToRegisterScreen() }) {
                    Text("Create an Account !", fontWeight = FontWeight.Bold, color = Rebonnte_green1)
                }
            }


        }


        // Dialog for password recovery
        if (showRecoveryPasswordDialog) {
            val emailRecoveryMessage =
                "A password recovery email has been sent to $recoveryPasswordEmail."
            ForgotPasswordDialog(
                onDismiss = { showRecoveryPasswordDialog = false },
                onSendEmail = {
                    // Send recovery email
                    viewModel.sendPasswordResetEmail(recoveryPasswordEmail)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(emailRecoveryMessage)
                    }
                    showRecoveryPasswordDialog = false
                },
                email = recoveryPasswordEmail
            )


        }

    }

}
