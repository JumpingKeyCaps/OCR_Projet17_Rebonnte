package com.openclassrooms.rebonnte.ui.medicine.composition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.domain.MedicineWithStock

/**
 * Composable for displaying a medicine item with stock.
 * @param medicineWS The medicine with stock to display.
 * @param onClick Callback to handle item click.
 */
@Composable
fun MedicineWithStockItem(medicineWS: MedicineWithStock, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicineWS.medicineId) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicineWS.name, fontWeight = FontWeight.Bold, fontSize = 18.sp,maxLines = 1)
            Text(text = medicineWS.principeActif, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic, fontSize = 12.sp,maxLines = 1,color = Color.Gray)
            Text(text = "Stock: ${medicineWS.quantity}", color = Color.LightGray, fontSize = 12.sp ,fontWeight = FontWeight.Normal)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Arrow", modifier = Modifier.align(Alignment.CenterVertically))
    }
}