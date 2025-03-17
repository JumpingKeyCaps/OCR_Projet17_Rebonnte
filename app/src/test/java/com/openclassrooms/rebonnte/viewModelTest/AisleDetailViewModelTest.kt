package com.openclassrooms.rebonnte.viewModelTest

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class AisleDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val aisleRepository: AisleRepository = mockk()
    private val stockRepository: StockRepository = mockk()
    private lateinit var viewModel: AisleDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AisleDetailViewModel(aisleRepository, stockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAisle should update aisle and medicines when aisle exists`() = runTest {
        val aisleId = "testAisleId"
        val expectedAisle = Aisle(aisleId = aisleId, name = "Test Aisle")
        val expectedMedicines = listOf(
            MedicineWithStock(medicineId = "med1"),
            MedicineWithStock(medicineId = "med2")
        )

        coEvery { aisleRepository.fetchAisleById(aisleId) } returns expectedAisle
        every { aisleRepository.fetchMedicinesForAisle(aisleId) } returns flowOf(expectedMedicines)

        viewModel.loadAisle(aisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(expectedAisle, viewModel.aisle.value)
        assertEquals(expectedMedicines, viewModel.medicines.value)
    }

    @Test
    fun `loadAisle should only update aisle when aisle exists and medicines are empty`() = runTest {
        val aisleId = "testAisleId"
        val expectedAisle = Aisle(aisleId = aisleId, name = "Test Aisle")
        val expectedMedicines = emptyList<MedicineWithStock>()

        coEvery { aisleRepository.fetchAisleById(aisleId) } returns expectedAisle
        every { aisleRepository.fetchMedicinesForAisle(aisleId) } returns flowOf(expectedMedicines)

        viewModel.loadAisle(aisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(expectedAisle, viewModel.aisle.value)
        assertEquals(expectedMedicines, viewModel.medicines.value)
    }

    @Test
    fun `loadAisle should not update aisle or medicines when aisle does not exist`() = runTest {
        val aisleId = "testAisleId"
        coEvery { aisleRepository.fetchAisleById(aisleId) } returns null

        viewModel.loadAisle(aisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Aisle(), viewModel.aisle.value)
        assertEquals(emptyList<MedicineWithStock>(), viewModel.medicines.value)
    }

    @Test
    fun `deleteAisle should emit success when deletion is successful`() = runTest {
        val aisleId = "testAisleId"
        val medicines = listOf(
            MedicineWithStock(medicineId = "med1"),
            MedicineWithStock(medicineId = "med2")
        )

        every { aisleRepository.fetchMedicinesForAisle(aisleId) } returns flowOf(medicines)
        coEvery { stockRepository.updateMedicineAisle(any(), viewModel.DEFAULT_AISLE_ID) } returns true
        coEvery { aisleRepository.deleteAisle(aisleId) } returns true

        val results = mutableListOf<Result<Boolean>>()
        val job = launch {
            viewModel.aisleActionResult.toList(results)
        }

        viewModel.deleteAisle(aisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Result.success(true), results.first())
        job.cancel()
    }



    @Test
    fun `deleteAisle should emit failure when exception occurs`() = runTest {
        val aisleId = "testAisleId"
        val exception = Exception("Test Exception")

        every { aisleRepository.fetchMedicinesForAisle(aisleId) } throws exception

        val results = mutableListOf<Result<Boolean>>()
        val job = launch {
            viewModel.aisleActionResult.toList(results)
        }

        viewModel.deleteAisle(aisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, results.first().isFailure)
        assertEquals(exception, results.first().exceptionOrNull())
        job.cancel()
    }
}