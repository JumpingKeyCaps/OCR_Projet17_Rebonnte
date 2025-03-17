package com.openclassrooms.rebonnte.repositoryTest

import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {
    private lateinit var fireStoreService: FireStoreService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        // Création d'un mock du service FireStoreService
        fireStoreService = mockk()

        // Initialisation du repository avec le mock
        userRepository = UserRepository(fireStoreService)
    }

    @Test
    fun `test addUser calls fireStoreService and returns user ID`() = runBlocking {
        // Arrange
        val user = User(id = "", firstname = "John Doe", email = "john.doe@example.com")
        val expectedUserId = "user123"

        coEvery { fireStoreService.addUser(user) } returns expectedUserId

        // Act
        val result = userRepository.addUser(user)

        // Assert
        assertEquals(expectedUserId, result)
        coVerify { fireStoreService.addUser(user) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }

    @Test
    fun `test deleteUser calls fireStoreService and returns success`() = runBlocking {
        // Arrange
        val userId = "user123"
        val expectedResult = true

        coEvery { fireStoreService.deleteUser(userId) } returns expectedResult

        // Act
        val result = userRepository.deleteUser(userId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.deleteUser(userId) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }

    @Test
    fun `test getUserById calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val userId = "user123"
        val user = User(id = userId, firstname = "John Doe", email = "john.doe@example.com")

        coEvery { fireStoreService.getUserById(userId) } returns flowOf(user)

        // Act
        val result = userRepository.getUserById(userId).first()

        // Assert
        assertEquals(user, result)
        coVerify { fireStoreService.getUserById(userId) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }
}