package com.openclassrooms.rebonnte.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.data.repository.AuthenticationRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication-related operations.
 * @param authRepository The repository for authentication.(hilt injected)
 * @param userRepository The repository for user operations.(hilt injected)
 */
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : ViewModel()  {

    private val _registerResult = MutableSharedFlow<Result<FirebaseUser?>>()
    val registerResult: SharedFlow<Result<FirebaseUser?>> get() = _registerResult

    private val _signInResult = MutableSharedFlow<Result<FirebaseUser?>>()
    val signInResult: SharedFlow<Result<FirebaseUser?>> get() = _signInResult

    /**
     * Registers a new user with the provided email and password.
     * @param email The email address of the new user.
     * @param password The password for the new user.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     */
    fun registerUser(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            authRepository.registerUser(email, password)
                .collect { result ->
                    //Check is the user is successfully added
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        if (user != null) {
                            // Une fois l'utilisateur enregistré, appeler la méthode pour l'ajouter dans Firestore
                            val newUser = User(
                                id = user.uid,
                                email = user.email?:email,
                                firstname = firstName,
                                lastname = lastName
                            )
                            // Appel de la méthode pour ajouter l'utilisateur à Firestore
                            addUserToFireStore(newUser)
                        }
                            // Si l'utilisateur est nul, renvoyer un résultat d'échec
                            _registerResult.emit(result)
                    }
                }
        }
    }

    /**
     * Adds a user to Firestore.
     * @param user The user to add to Firestore.
     */
    private fun addUserToFireStore(user: User) {
        val newUser = user.copy(
            id = user.id,
            email = user.email,
            firstname = user.firstname.capitalizeFirstLetter(),
            lastname = user.lastname.capitalizeFirstLetter()
        )
        viewModelScope.launch {
            try {
                userRepository.addUser(newUser)
            } catch (_: Exception) {}
        }
    }

    /**
     * Signs in a user with the provided email and password.
     * @param email The email address of the user.
     * @param password The password for the user.
     */
    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signInUser(email, password)
                .collect { result ->
                    _signInResult.emit(result)
                }
        }
    }

    /**
     * Sends a password reset email to the provided email address.
     * @param email The email address to send the reset email to.
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(email)
                .collect {}
        }
    }

    /**
     * Capitalizes the first letter of a string.
     * @return The capitalized string.
     */
    private fun String.capitalizeFirstLetter(): String {
        return this.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

}