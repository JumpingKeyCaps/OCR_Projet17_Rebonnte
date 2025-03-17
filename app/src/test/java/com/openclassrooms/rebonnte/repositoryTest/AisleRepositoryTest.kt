package com.openclassrooms.rebonnte.repositoryTest

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AisleRepositoryTest {
    private lateinit var fireStoreService: FireStoreService
    private lateinit var aisleRepository: AisleRepository

    @Before
    fun setUp() {
        fireStoreService = mockk()
        aisleRepository = AisleRepository(fireStoreService)
    }

    @Test
    fun testAddAisle() = runBlocking {
        // Arrange
        val name = "Aisle 1"
        val description = "First aisle"
        coEvery { fireStoreService.addAisle(name, description) } returns true

        // Act
        val result = aisleRepository.addAisle(name, description)

        // Assert
        assertEquals(true, result)
        coVerify { fireStoreService.addAisle(name, description) }
    }

    @Test
    fun testDeleteAisle() = runBlocking {
        // Arrange
        val aisleId = "aisle123"
        coEvery { fireStoreService.deleteAisle(aisleId) } returns true

        // Act
        val result = aisleRepository.deleteAisle(aisleId)

        // Assert
        assertEquals(true, result)
        coVerify { fireStoreService.deleteAisle(aisleId) }
    }

    @Test
    fun testFetchAllAisles() = runBlocking {
        // Arrange
        val aisles = listOf(Aisle("aisle1", "Aisle 1"), Aisle("aisle2", "Aisle 2"))
        coEvery { fireStoreService.fetchAllAisles() } returns flowOf(aisles)

        // Act
        val result = aisleRepository.fetchAllAisles()

        // Assert
        result.collect { fetchedAisles ->
            assertEquals(2, fetchedAisles.size)
            assertEquals("Aisle 1", fetchedAisles[0]?.name)
        }
    }

    @Test
    fun testFetchAisleById() = runBlocking {
        // Arrange
        val aisleId = "aisle123"
        val aisle = Aisle(aisleId, "Aisle 1")
        coEvery { fireStoreService.fetchAisleById(aisleId) } returns aisle

        // Act
        val result = aisleRepository.fetchAisleById(aisleId)

        // Assert
        assertEquals("Aisle 1", result?.name)
        coVerify { fireStoreService.fetchAisleById(aisleId) }
    }

    @Test
    fun testFetchAisleByIdNotFound() = runBlocking {
        // Arrange
        val aisleId = "aisle123"
        coEvery { fireStoreService.fetchAisleById(aisleId) } returns null

        // Act
        val result = aisleRepository.fetchAisleById(aisleId)

        // Assert
        assertNull(result)
        coVerify { fireStoreService.fetchAisleById(aisleId) }
    }

    @Test
    fun testFetchMedicinesForAisle() = runBlocking {
        // Arrange
        val aisleId = "aisle123"
        val medicines = listOf(MedicineWithStock("123", "Aspirin", quantity = 10))
        coEvery { fireStoreService.fetchMedicinesForAisle(aisleId) } returns flowOf(medicines)

        // Act
        val result = aisleRepository.fetchMedicinesForAisle(aisleId)

        // Assert
        result.collect { fetchedMedicines ->
            assertEquals(1, fetchedMedicines.size)
            assertEquals("Aspirin", fetchedMedicines[0].name)
        }
    }
}