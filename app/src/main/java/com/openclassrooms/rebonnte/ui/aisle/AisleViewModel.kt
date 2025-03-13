package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Aisle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AisleViewModel @Inject constructor() : ViewModel() {
    private var _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles

    init {
        _aisles.value = listOf(
            Aisle("Main_Aisle", name = "Main Aisle"),
            Aisle("Second_Aisle", name = "Second Aisle"),
            Aisle("Third_Aisle", name = "Third Aisle"),
            Aisle("Fourth_Aisle", name = "Fourth Aisle"))
    }

    fun addRandomAisle() {
        val currentAisles: MutableList<Aisle> = ArrayList(aisles.value)
        currentAisles.add(Aisle("Aisle " + (currentAisles.size + 1)))
        _aisles.value = currentAisles
    }
}

