package com.openclassrooms.rebonnte.repositoryTest

import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.Medicine
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MedicineRepositoryTest {
    private lateinit var fireStoreService: FireStoreService
    private lateinit var medicineRepository: MedicineRepository

    @Before
    fun setup() {
        // Création d'un mock du service FireStoreService
        fireStoreService = mockk()

        // Initialisation du repository avec le mock
        medicineRepository = MedicineRepository(fireStoreService)
    }

    @Test
    fun `test addMedicine calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicine = mockk<Medicine>()
        val aisleId = "aisle123"
        val expectedResult = true

        coEvery { fireStoreService.addMedicine(medicine, aisleId) } returns expectedResult

        // Act
        val result = medicineRepository.addMedicine(medicine, aisleId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.addMedicine(medicine, aisleId) }
    }

    @Test
    fun `test deleteMedicine calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedResult = true

        coEvery { fireStoreService.deleteMedicine(medicineId) } returns expectedResult

        // Act
        val result = medicineRepository.deleteMedicine(medicineId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.deleteMedicine(medicineId) }
    }

    @Test
    fun `test fetchAllMedicines calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val sortDsc = true
        val expectedResult = listOf(mockk<Medicine>())

        coEvery { fireStoreService.fetchAllMedicines(sortDsc) } returns flowOf(expectedResult)

        // Act
        val result = medicineRepository.fetchAllMedicines(sortDsc).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.fetchAllMedicines(sortDsc) }
    }

    @Test
    fun `test fetchMedicineById calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedResult = mockk<Medicine>()

        coEvery { fireStoreService.fetchMedicineById(medicineId) } returns expectedResult

        // Act
        val result = medicineRepository.fetchMedicineById(medicineId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.fetchMedicineById(medicineId) }
    }

    @Test
    fun `test searchMedicinesRealTime calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val searchQuery = "Aspirin"
        val expectedResult = listOf(mockk<Medicine>())

        coEvery { fireStoreService.searchMedicinesRealTime(searchQuery) } returns flowOf(expectedResult)

        // Act
        val result = medicineRepository.searchMedicinesRealTime(searchQuery).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.searchMedicinesRealTime(searchQuery) }
    }

    @Test
    fun `test getMedicineAisle calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedResult = "aisle123"

        coEvery { fireStoreService.getMedicineAisle(medicineId) } returns flowOf(expectedResult)

        // Act
        val result = medicineRepository.getMedicineAisle(medicineId).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.getMedicineAisle(medicineId) }
    }
}