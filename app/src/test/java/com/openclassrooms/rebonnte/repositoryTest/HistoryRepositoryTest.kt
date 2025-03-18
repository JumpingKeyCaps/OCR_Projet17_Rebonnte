package com.openclassrooms.rebonnte.repositoryTest

import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.StockHistory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {
    private lateinit var fireStoreService: FireStoreService
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setup() {
        // Création d'un mock du service FireStoreService
        fireStoreService = mockk()

        // Initialisation du repository avec le mock
        historyRepository = HistoryRepository(fireStoreService)
    }

    @Test
    fun `test getHistoryForMedicine calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedResult = listOf(mockk<StockHistory>()) // Simule une liste d'historique

        coEvery { fireStoreService.getHistoryForMedicine(medicineId) } returns flowOf(expectedResult)

        // Act
        val result = historyRepository.getHistoryForMedicine(medicineId).first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.getHistoryForMedicine(medicineId) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }

    @Test
    fun `test getAllHistory calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val expectedResult = listOf(mockk<StockHistory>()) // Simule une liste d'historique
        coEvery { fireStoreService.getAllHistory() } returns flowOf(expectedResult)

        // Act
        val result = historyRepository.getAllHistory().first()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.getAllHistory() } // Vérifie que la méthode a été appelée
    }
}