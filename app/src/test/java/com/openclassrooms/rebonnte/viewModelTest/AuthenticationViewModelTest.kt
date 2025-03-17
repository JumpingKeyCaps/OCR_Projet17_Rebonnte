package com.openclassrooms.rebonnte.viewModelTest

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.data.repository.AuthenticationRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.domain.User
import com.openclassrooms.rebonnte.ui.authentication.AuthenticationViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class AuthenticationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthenticationRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: AuthenticationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthenticationViewModel(authRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun `signInUser should emit success when sign in is successful`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val firebaseUser: FirebaseUser = mockk()

        every { authRepository.signInUser(email, password) } returns flowOf(Result.success(firebaseUser))

        val results = mutableListOf<Result<FirebaseUser?>>()
        val job = launch {
            viewModel.signInResult.toList(results)
        }

        viewModel.signInUser(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Result.success(firebaseUser), results.last())
        job.cancel()
    }

    @Test
    fun `signInUser should emit failure when sign in fails`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val exception = Exception("Sign in failed")

        every { authRepository.signInUser(email, password) } returns flowOf(Result.failure(exception))

        val results = mutableListOf<Result<FirebaseUser?>>()
        val job = launch {
            viewModel.signInResult.toList(results)
        }

        viewModel.signInUser(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, results.last().isFailure)
        assertEquals(exception, results.last().exceptionOrNull())
        job.cancel()
    }



    @Test
    fun `capitalizeFirstLetter should capitalize first letter`() {
        val method = AuthenticationViewModel::class.java.getDeclaredMethod("capitalizeFirstLetter", String::class.java)
        method.isAccessible = true

        val result1 = method.invoke(viewModel, "john") as String
        val result2 = method.invoke(viewModel, "JOHN") as String
        val result3 = method.invoke(viewModel, "jOhN") as String
        val result4 = method.invoke(viewModel, "1john") as String

        assertEquals("John", result1)
        assertEquals("John", result2)
        assertEquals("John", result3)
        assertEquals("1john", result4)

    }
}