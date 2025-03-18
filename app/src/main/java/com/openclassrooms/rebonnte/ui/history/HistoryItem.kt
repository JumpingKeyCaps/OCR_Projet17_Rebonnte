package com.openclassrooms.rebonnte.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.domain.StockHistory
import com.openclassrooms.rebonnte.ui.theme.Purple40
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_black
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_black_alt
import com.openclassrooms.rebonnte.ui.theme.Rebonnte_green1

/**
 * Composable function to display a history item in the UI.
 * @param history The history item to display.
 */
@Composable
fun HistoryItem(history: StockHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(5),
        colors = CardDefaults.cardColors(
            containerColor = Rebonnte_black_alt,
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title: Medicine name with bold and larger font
            Text(
                text = history.medicineName,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Purple40
            )
            // Divider for better visual separation
            Spacer(modifier = Modifier.height(8.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Rebonnte_black)
            Spacer(modifier = Modifier.height(8.dp))
            // Date and Time in a row with icons
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Date: ${history.date}", style = MaterialTheme.typography.body2, color = Color.White,textAlign = TextAlign.Center)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Time", modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Time: ${history.time}", style = MaterialTheme.typography.body2, color = Color.White,textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "User", modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = history.author, style = MaterialTheme.typography.body2, color = Color.White,textAlign = TextAlign.Center,)
                }
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Action: ${history.action}",
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                    color = if (history.action == "ADD") Rebonnte_green1 else MaterialTheme.colors.error
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Quantity: ${history.quantity}", style = MaterialTheme.typography.body2, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Details: ${history.description}", style = MaterialTheme.typography.body2, color = Color.White)
        }
    }
}