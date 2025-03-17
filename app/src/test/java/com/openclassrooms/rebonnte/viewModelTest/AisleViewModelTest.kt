package com.openclassrooms.rebonnte.viewModelTest

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class AisleViewModelTest {

    private lateinit var viewModel: AisleViewModel
    private val aisleRepository: AisleRepository = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Initialisation du ViewModel avec un mock du repository
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = AisleViewModel(aisleRepository)
    }

    @Test
    fun `test fetchAllAisles updates aisles list`() = runTest {
        // Données mockées pour le test
        val mockedAisles = listOf(Aisle("Rayon 1"), Aisle("Rayon 2"))

        // On mock la méthode fetchAllAisles pour retourner un Flow avec les rayons mockés
        coEvery { aisleRepository.fetchAllAisles() } returns flowOf(mockedAisles)

        // On appelle la méthode fetchAllAisles du ViewModel
        viewModel.fetchAllAisles()

        // Vérifier que l'état de _aisles a été mis à jour
        val aislesList = viewModel.aisles.first()  // Récupère la première valeur du Flow
        assertEquals(mockedAisles, aislesList)  // Vérifie que la liste des rayons correspond
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset after the test
    }

}