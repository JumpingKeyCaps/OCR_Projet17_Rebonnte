package com.openclassrooms.rebonnte.viewModelTest

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.ui.aisle.add.AddAisleViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class AddAisleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val aisleRepository: AisleRepository = mockk()
    private lateinit var viewModel: AddAisleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddAisleViewModel(aisleRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addAisle should emit success when aisle is added successfully`() = runTest {
        val name = "Test Aisle"
        val description = "Test Description"
        coEvery { aisleRepository.addAisle(name, description) } returns true

        val results = mutableListOf<Result<Boolean>?>()
        val job = launch {
            viewModel.aisleCreationResult.toList(results)
        }

        viewModel.addAisle(name, description)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Result.success(true), results.last())
        job.cancel()
    }

    @Test
    fun `addAisle should emit failure when aisle addition fails`() = runTest {
        val name = "Test Aisle"
        val description = "Test Description"
        coEvery { aisleRepository.addAisle(name, description) } returns false

        val results = mutableListOf<Result<Boolean>?>()
        val job = launch {
            viewModel.aisleCreationResult.toList(results)
        }

        viewModel.addAisle(name, description)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, results.last()?.isFailure)
        job.cancel()
    }

    @Test
    fun `addAisle should emit failure when exception occurs`() = runTest {
        val name = "Test Aisle"
        val description = "Test Description"
        val exception = Exception("Test Exception")
        coEvery { aisleRepository.addAisle(name, description) } throws exception

        val results = mutableListOf<Result<Boolean>?>()
        val job = launch {
            viewModel.aisleCreationResult.toList(results)
        }

        viewModel.addAisle(name, description)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, results.last()?.isFailure)
        assertEquals(exception, results.last()?.exceptionOrNull())
        job.cancel()
    }

    @Test
    fun `addAisle should emit error message when name is blank`() = runTest {
        val name = ""
        val description = "Test Description"

        val errors = mutableListOf<String?>()
        val job = launch {
            viewModel.errorMessage.toList(errors)
        }

        viewModel.addAisle(name, description)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Aisle name is required.", errors.last())
        assertNull(viewModel.aisleCreationResult.value)
        job.cancel()
    }

    @Test
    fun `addAisle should emit error message when description is blank`() = runTest {
        val name = "Test Aisle"
        val description = ""

        val errors = mutableListOf<String?>()
        val job = launch {
            viewModel.errorMessage.toList(errors)
        }

        viewModel.addAisle(name, description)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Aisle description is required.", errors.last())
        assertNull(viewModel.aisleCreationResult.value)
        job.cancel()
    }


}