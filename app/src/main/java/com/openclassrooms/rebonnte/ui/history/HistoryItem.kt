package com.openclassrooms.rebonnte.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.domain.StockHistory

/**
 * Composable function to display a history item in the UI.
 * @param history The history item to display.
 */
@Composable
fun HistoryItem(history: StockHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "Date: ${history.date}")
            Text(text = "Time: ${history.time}")
            Text(text = "Details: ${history.description}")
            Text(text = "Quantity: ${history.quantity}")
            Text(text = "Action: ${history.action}")
            Text(text = "User: ${history.author}")

        }
    }
}