package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Medicine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor() : ViewModel() {

    private var _medicine = MutableStateFlow(Medicine())
    val medicine: StateFlow<Medicine> get() = _medicine




    fun loadMedicine(medicineId: String) {
        //todo asynch call to get the medicine
        //_medicine.value = Medicine(aisleId)

    }


}