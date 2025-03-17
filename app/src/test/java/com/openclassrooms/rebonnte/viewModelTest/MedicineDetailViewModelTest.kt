package com.openclassrooms.rebonnte.viewModelTest

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.MedicineWithStock
import com.openclassrooms.rebonnte.domain.StockHistory
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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
class MedicineDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val medicineRepository: MedicineRepository = mockk()
    private val stockRepository: StockRepository = mockk()
    private val historyRepository: HistoryRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val aisleRepository: AisleRepository = mockk()
    private lateinit var viewModel: MedicineDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MedicineDetailViewModel(medicineRepository, stockRepository, historyRepository, userRepository, aisleRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMedicine should update medicineWithStock`() = runTest {
        val medicineId = "1"
        val medicine = Medicine(medicineId = medicineId, name = "Medicine 1")
        val quantity = 10
        coEvery { medicineRepository.fetchMedicineById(medicineId) } returns medicine
        coEvery { stockRepository.getStockQuantity(medicineId) } returns quantity

        viewModel.loadMedicine(medicineId)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMedicineWithStock = MedicineWithStock(medicineId = medicineId, name = "Medicine 1", quantity = quantity)
        assertEquals(expectedMedicineWithStock, viewModel.medicineWithStock.value)
    }



    @Test
    fun `loadMedicineAisle should update medicineAisle`() = runTest {
        val medicineId = "1"
        val aisleId = "aisle1"
        val aisleName = "Aisle 1"
        coEvery { medicineRepository.getMedicineAisle(medicineId) } returns flowOf(aisleId)
        coEvery { aisleRepository.fetchAisleById(aisleId) } returns Aisle(aisleId = aisleId, name = aisleName)

        viewModel.loadMedicineAisle(medicineId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(aisleName, viewModel.medicineAisle.value)
    }

    @Test
    fun `updateMedicineAisle should update aisle and reload medicine`() = runTest {
        val medicineId = "1"
        val newAisleId = "aisle2"
        val medicine = Medicine(medicineId = medicineId, name = "Medicine 1")
        val quantity = 10
        coEvery { stockRepository.updateMedicineAisle(medicineId, newAisleId) } returns true
        coEvery { medicineRepository.fetchMedicineById(medicineId) } returns medicine
        coEvery { stockRepository.getStockQuantity(medicineId) } returns quantity

        viewModel.updateMedicineAisle(medicineId, newAisleId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.updateSuccess.value)
        assertEquals(MedicineWithStock(medicineId = medicineId, name = "Medicine 1", quantity = quantity), viewModel.medicineWithStock.value)
    }

    @Test
    fun `deleteMedicine should update deleteSuccess`() = runTest {
        val medicineId = "1"
        coEvery { medicineRepository.deleteMedicine(medicineId) } returns true

        viewModel.deleteMedicine(medicineId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.deleteSuccess.value)
    }

    @Test
    fun `resetUpdateSuccess should reset updateSuccess`() = runTest {
        viewModel.resetUpdateSuccess()
        assertNull(viewModel.updateSuccess.value)
    }

}