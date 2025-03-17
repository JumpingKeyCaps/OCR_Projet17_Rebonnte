package com.openclassrooms.rebonnte.viewModelTest

import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MedicineViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val medicineRepository: MedicineRepository = mockk()
    private val stockRepository: StockRepository = mockk()
    private lateinit var viewModel: MedicineViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllMedicinesWithStock should update medicinesWithStock`() = runTest {
        val medicines = listOf(Medicine(medicineId = "1", name = "Medicine 1"))
        every { medicineRepository.fetchAllMedicines(false) } returns flowOf(medicines)
        every { stockRepository.getStockQuantityFlow("1") } returns flowOf(10)

        viewModel = MedicineViewModel(medicineRepository, stockRepository) // Initialisation du ViewModel APRES la configuration de MockK
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMedicinesWithStock = listOf(MedicineWithStock(medicineId = "1", name = "Medicine 1", quantity = 10))
        assertEquals(expectedMedicinesWithStock, viewModel.medicinesWithStock.value)
    }
}