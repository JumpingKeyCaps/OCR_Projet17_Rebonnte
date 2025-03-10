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
/**
 * Composable for the registering screen.
 **/
@Composable
fun RegisterUserScreen(viewModel: AuthenticationViewModel = hiltViewModel(),
                       onNavigateToSignInScreen: () -> Unit,
                       onNavigateToMainScreen: () -> Unit
) {

    //Register user result from the viewmodel
    val registerUserResult by viewModel.registerResult.collectAsStateWithLifecycle(null)
    // Infos Feedback
    val snackBarHostState = remember { SnackbarHostState() }

    var registerButtonIsEnabled by remember { mutableStateOf(true) }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isEmailError = false
    val isPasswordError = false
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // Check if the sign-Up result indicates success or failure
    LaunchedEffect(registerUserResult) {
        registerUserResult?.onSuccess { user ->
            if (user != null) { onNavigateToMainScreen() }
        }?.onFailure { exception ->
            snackBarHostState.showSnackbar("Registering failed: ${exception.message}")
            registerButtonIsEnabled = true
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Rebonnte_black,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Thin,
                    color = Color.White
                ),
                fontSize = 42.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.signupdoodleb),
                contentDescription = "Illustration d'inscription",
                modifier = Modifier.size(220.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your Rebonnte account.",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Enter your email") },
                isError = isEmailError,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
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

            Spacer(modifier = Modifier.height(28.dp))


            Button(
                onClick = {
                    registerButtonIsEnabled = true
                    viewModel.registerUser(email, password, firstName, lastName)
                },
                enabled = registerButtonIsEnabled && !isPasswordError,
                modifier = Modifier.fillMaxWidth().height(55.dp).padding(horizontal = 36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rebonnte_green2,
                    contentColor = Color.White
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text("Already have an account? ", color = Color.Gray)
                TextButton(onClick = { onNavigateToSignInScreen() }) {
                    Text("Sign In!", fontWeight = FontWeight.Bold, color = Rebonnte_green1)
                }
            }
        }
    }
}
