package com.openclassrooms.rebonnte.repositoryTest

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.data.repository.AuthenticationRepository
import com.openclassrooms.rebonnte.data.service.authentication.FirebaseAuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AuthenticationRepositoryTest {
    private lateinit var authService: FirebaseAuthService
    private lateinit var authenticationRepository: AuthenticationRepository

    @Before
    fun setup() {
        // Création d'un mock du service FirebaseAuthService
        authService = mockk()

        // Initialisation du repository avec le mock
        authenticationRepository = AuthenticationRepository(authService)
    }

    @Test
    fun `test registerUser calls authService and emits result`() = runBlocking {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val expectedResult = Result.success(mockk<FirebaseUser>()) // Simule un succès avec un utilisateur fictif
        coEvery { authService.registerUser(email, password) } returns expectedResult

        // Act
        val result = authenticationRepository.registerUser(email, password).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { authService.registerUser(email, password) } // Vérifie que la méthode a été appelée avec les bons paramètres
    }

    @Test
    fun `test signInUser calls authService and emits result`() = runBlocking {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val expectedResult = Result.success(mockk<FirebaseUser>()) // Simule un succès avec un utilisateur fictif
        coEvery { authService.signInUser(email, password) } returns expectedResult

        // Act
        val result = authenticationRepository.signInUser(email, password).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { authService.signInUser(email, password) } // Vérifie que la méthode a été appelée avec les bons paramètres
    }

    @Test
    fun `test signOutUser calls authService and emits result`() = runBlocking {
        // Arrange
        val expectedResult = Result.success(Unit) // Simule un succès
        coEvery { authService.signOutUser() } returns expectedResult

        // Act
        val result = authenticationRepository.signOutUser().first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { authService.signOutUser() } // Vérifie que la méthode a été appelée
    }

    @Test
    fun `test deleteUserAccount calls authService and emits result`() = runBlocking {
        // Arrange
        val user = mockk<FirebaseUser>()
        val expectedResult = Result.success(Unit) // Simule un succès
        coEvery { authService.deleteUserAccount(user) } returns expectedResult

        // Act
        val result = authenticationRepository.deleteUserAccount(user).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { authService.deleteUserAccount(user) } // Vérifie que la méthode a été appelée avec l'utilisateur correct
    }

    @Test
    fun `test sendPasswordResetEmail calls authService and emits result`() = runBlocking {
        // Arrange
        val email = "test@example.com"
        val expectedResult = Result.success(Unit) // Simule un succès
        coEvery { authService.sendPasswordResetEmail(email) } returns expectedResult

        // Act
        val result = authenticationRepository.sendPasswordResetEmail(email).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { authService.sendPasswordResetEmail(email) } // Vérifie que la méthode a été appelée avec l'email correct
    }
}