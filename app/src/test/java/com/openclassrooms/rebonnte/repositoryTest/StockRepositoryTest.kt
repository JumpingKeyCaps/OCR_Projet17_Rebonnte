package com.openclassrooms.rebonnte.repositoryTest

import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StockRepositoryTest {
    private lateinit var fireStoreService: FireStoreService
    private lateinit var stockRepository: StockRepository

    @Before
    fun setup() {
        // Création d'un mock du service FireStoreService
        fireStoreService = mockk()

        // Initialisation du repository avec le mock
        stockRepository = StockRepository(fireStoreService)
    }

    @Test
    fun `test updateMedicineAisle calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val newAisleId = "aisle456"
        val expectedResult = true

        coEvery { fireStoreService.updateMedicineAisle(medicineId, newAisleId) } returns expectedResult

        // Act
        val result = stockRepository.updateMedicineAisle(medicineId, newAisleId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.updateMedicineAisle(medicineId, newAisleId) } // Vérifie que la méthode a été appelée avec les bons arguments
    }

    @Test
    fun `test updateQuantityInStock calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val quantityDelta = 10
        val author = "user"
        val description = "Stock update"
        val expectedResult = true

        coEvery { fireStoreService.updateQuantityInStock(medicineId, quantityDelta, author, description) } returns expectedResult

        // Act
        val result = stockRepository.updateQuantityInStock(medicineId, quantityDelta, author, description)

        // Assert
        assertEquals(expectedResult, result)
        coVerify { fireStoreService.updateQuantityInStock(medicineId, quantityDelta, author, description) } // Vérifie que la méthode a été appelée avec les bons paramètres
    }

    @Test
    fun `test getStockQuantity calls fireStoreService and returns result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedQuantity = 50

        coEvery { fireStoreService.getStockQuantity(medicineId) } returns expectedQuantity

        // Act
        val result = stockRepository.getStockQuantity(medicineId)

        // Assert
        assertEquals(expectedQuantity, result)
        coVerify { fireStoreService.getStockQuantity(medicineId) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }

    @Test
    fun `test getStockQuantityFlow calls fireStoreService and emits result`() = runBlocking {
        // Arrange
        val medicineId = "med123"
        val expectedQuantity = 50

        coEvery { fireStoreService.getStockQuantityFlow(medicineId) } returns flowOf(expectedQuantity)

        // Act
        val result = stockRepository.getStockQuantityFlow(medicineId).first()

        // Assert
        assertEquals(expectedQuantity, result)
        coVerify { fireStoreService.getStockQuantityFlow(medicineId) } // Vérifie que la méthode a été appelée avec le bon paramètre
    }
}