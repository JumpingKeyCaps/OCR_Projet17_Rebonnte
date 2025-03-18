package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel for the add medicine screen.
 * @param medicineRepository Repository for medicines.
 * @param aisleRepository Repository for aisles.
 */
@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val aisleRepository: AisleRepository
) : ViewModel(){

    private val _medicineCreationResult = MutableStateFlow<Result<Boolean>?>(null)
    val medicineCreationResult: StateFlow<Result<Boolean>?> get() = _medicineCreationResult

    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        fetchAisles()
    }

    /**
     * Fetch all aisles from the repository.
     */
    private fun fetchAisles() {
        viewModelScope.launch {
            aisleRepository.fetchAllAisles()
                .catch { e ->
                    // Gérer les erreurs de récupération des rayons
                    _aisles.value = emptyList()
                }
                .collect { aisleList ->
                    _aisles.value = aisleList.filterNotNull()
                }
        }
    }

    /**
     * Add a new medicine to the database.
     * @param medicine The medicine to add.
     */
    fun addMedicine(medicine: Medicine, aisleId: String?){
        viewModelScope.launch {
            if (validateMedicine(medicine,aisleId.orEmpty())){
                try {
                    val isSuccess = medicineRepository.addMedicine(medicine,aisleId)
                    if (isSuccess) {
                        _medicineCreationResult.emit(Result.success(true))
                    } else {
                        _medicineCreationResult.emit(Result.failure(Exception("Failed to add medicine")))
                    }
                }catch (e:Exception){
                    _medicineCreationResult.emit(Result.failure(e))
                }
            }
        }
    }

    /**
     * Validate input fields before adding a medicine.
     * @param medicine The medicine to validate.
     */
    private fun validateMedicine(medicine: Medicine,aisleId: String): Boolean {
           return when {
                medicine.name.isBlank() -> {
                    emitError("Medicine name is required.")
                    false
                }
               medicine.principeActif.isBlank() -> {
                   emitError("Active substance is required.")
                   false
               }
                medicine.fabricant.isBlank() -> {
                    emitError("Manufacturer is required.")
                    false
                }
               medicine.description.isBlank() -> {
                   emitError("Description is required.")
                   false
               }
                medicine.indication.isBlank() -> {
                    emitError("Indication is required.")
                    false
                }

                medicine.utilisation.isBlank() -> {
                    emitError("Usage is required.")
                    false
                }
                medicine.warning.isBlank() -> {
                    emitError("Warning is required.")
                    false
                }
               medicine.dosage.isBlank() -> {
                   emitError("Dosage is required.")
                   false
               }
                aisleId.isBlank() -> {
                    emitError("Set the aisle of the medicine.")
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