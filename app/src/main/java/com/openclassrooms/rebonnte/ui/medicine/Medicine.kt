package com.openclassrooms.rebonnte.ui.medicine

import com.openclassrooms.rebonnte.ui.history.History

data class Medicine(
    val id: String,
    var name: String,
    var stock: Int,
    var nameAisle: String,
    var histories: List<History>
)
