package com.openclassrooms.rebonnte.ui

import androidx.compose.runtime.Composable
import com.openclassrooms.rebonnte.ui.medicine.Medicine

/**
 * Main screen composable function.
 *  - Manages the inner navigation graph for the Aisle and Medicine screens.
 *  - Manages the bottom navigation bar.
 */
@Composable
fun MainScreen(
    onLogOutAction: () -> Unit,
    onAddMedicineAction: () -> Unit,
    onMedicineClicked: (Medicine) -> Unit,
    onAisleClicked: (String) -> Unit
) {

    //todo main screen contentment for Aisle and Medicine screens



}
