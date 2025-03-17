package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.StockHistory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class StockHistoryUnitTest {
    @Test
    fun testStockHistoryDefaultValues() {
        val stockHistory = StockHistory()

        // Vérifie que les valeurs par défaut sont correctes
        assertEquals("", stockHistory.date)
        assertEquals("", stockHistory.time)
        assertEquals("", stockHistory.medicineId)
        assertEquals("", stockHistory.medicineName)
        assertEquals(0, stockHistory.quantity)
        assertEquals("", stockHistory.action)
        assertEquals("", stockHistory.description)
        assertEquals("", stockHistory.author)
    }

    @Test
    fun testStockHistoryCustomValues() {
        val stockHistory = StockHistory(
            date = "2025-03-17",
            time = "14:00",
            medicineId = "12345",
            medicineName = "Aspirin",
            quantity = 100,
            action = "ADD",
            description = "Stock added for new batch",
            author = "Admin"
        )

        // Vérifie que les valeurs personnalisées sont correctement assignées
        assertEquals("2025-03-17", stockHistory.date)
        assertEquals("14:00", stockHistory.time)
        assertEquals("12345", stockHistory.medicineId)
        assertEquals("Aspirin", stockHistory.medicineName)
        assertEquals(100, stockHistory.quantity)
        assertEquals("ADD", stockHistory.action)
        assertEquals("Stock added for new batch", stockHistory.description)
        assertEquals("Admin", stockHistory.author)
    }

    @Test
    fun testStockHistoryEquality() {
        val stockHistory1 = StockHistory(
            date = "2025-03-17",
            time = "14:00",
            medicineId = "12345",
            medicineName = "Aspirin",
            quantity = 100,
            action = "ADD",
            description = "Stock added for new batch",
            author = "Admin"
        )

        val stockHistory2 = StockHistory(
            date = "2025-03-17",
            time = "14:00",
            medicineId = "12345",
            medicineName = "Aspirin",
            quantity = 100,
            action = "ADD",
            description = "Stock added for new batch",
            author = "Admin"
        )

        val stockHistory3 = StockHistory(
            date = "2025-03-17",
            time = "14:00",
            medicineId = "67890",
            medicineName = "Paracetamol",
            quantity = 200,
            action = "REMOVE",
            description = "Stock removed for old batch",
            author = "Admin"
        )

        // Vérifie que deux objets avec les mêmes valeurs sont considérés comme égaux
        assertTrue(stockHistory1 == stockHistory2)

        // Vérifie que deux objets avec des valeurs différentes ne sont pas égaux
        assertFalse(stockHistory1 == stockHistory3)
    }

    @Test
    fun testStockHistoryToString() {
        val stockHistory = StockHistory(
            date = "2025-03-17",
            time = "14:00",
            medicineId = "12345",
            medicineName = "Aspirin",
            quantity = 100,
            action = "ADD",
            description = "Stock added for new batch",
            author = "Admin"
        )

        // Vérifie que la méthode toString() renvoie une représentation correcte de l'objet
        assertEquals(
            "StockHistory(date=2025-03-17, time=14:00, medicineId=12345, medicineName=Aspirin, quantity=100, action=ADD, description=Stock added for new batch, author=Admin)",
            stockHistory.toString()
        )
    }
}