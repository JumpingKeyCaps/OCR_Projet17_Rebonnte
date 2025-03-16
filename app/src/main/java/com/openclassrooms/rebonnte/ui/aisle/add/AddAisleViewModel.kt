package com.openclassrooms.rebonnte.ui.aisle.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the add aisle screen.
 */
@HiltViewModel
class AddAisleViewModel @Inject constructor(
    private val aisleRepository: AisleRepository
) : ViewModel() {


    private val _aisleCreationResult = MutableStateFlow<Result<Boolean>?>(null)
    val aisleCreationResult: StateFlow<Result<Boolean>?> get() = _aisleCreationResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage




    /**
     * Add a new aisle to the database.
     * @param name The name of the aisle.
     * @param description The description of the aisle.
     */
    fun addAisle(name: String, description: String) {
        viewModelScope.launch {
            if(validateAisle(name,description)){
                try {
                    val isSuccess = aisleRepository.addAisle(name, description)
                    if (isSuccess) {
                        _aisleCreationResult.emit(Result.success(true))
                    } else {
                        _aisleCreationResult.emit(Result.failure(Exception("Failed to add aisle")))
                    }
                } catch (e: Exception) {
                    _aisleCreationResult.emit(Result.failure(e))
                }
            }
        }
    }

    /**
     * Validate input fields before adding an aisle.
     * @param name The name of the aisle.
     * @param description The description of the aisle.
     */
    private fun validateAisle(name: String, description: String): Boolean {
        return when {
            name.isBlank() -> {
                emitError("Aisle name is required.")
                false
            }
            description.isBlank() -> {
                emitError("Aisle description is required.")
                false
            }
            else -> true
        }
    }



    /**
     * Emit an error message.
     * @param message The error message to emit.
     */
    private fun emitError(message: String) {
        viewModelScope.launch {
            _errorMessage.emit(message)
        }
    }
}