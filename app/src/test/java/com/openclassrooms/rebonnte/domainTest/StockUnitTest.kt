package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.Stock
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class StockUnitTest {


    @Test
    fun testStockCustomValues() {
        val stock = Stock(
            medicineId = "12345",
            aisleId = "A1",
            quantity = 100,
            name = "Painkillers"
        )

        // Vérifie que les valeurs personnalisées sont correctement assignées
        assertEquals("12345", stock.medicineId)
        assertEquals("A1", stock.aisleId)
        assertEquals(100, stock.quantity)
        assertEquals("Painkillers", stock.name)
    }

    @Test
    fun testStockEquality() {
        val stock1 = Stock(
            medicineId = "12345",
            aisleId = "A1",
            quantity = 100,
            name = "Painkillers"
        )

        val stock2 = Stock(
            medicineId = "12345",
            aisleId = "A1",
            quantity = 100,
            name = "Painkillers"
        )

        val stock3 = Stock(
            medicineId = "67890",
            aisleId = "B2",
            quantity = 50,
            name = "Antibiotics"
        )

        // Vérifie que deux objets avec les mêmes valeurs sont considérés comme égaux
        assertTrue(stock1 == stock2)

        // Vérifie que deux objets avec des valeurs différentes ne sont pas égaux
        assertFalse(stock1 == stock3)
    }

    @Test
    fun testStockToString() {
        val stock = Stock(
            medicineId = "12345",
            aisleId = "A1",
            quantity = 100,
            name = "Painkillers"
        )

        // Vérifie que la méthode toString() renvoie une représentation correcte de l'objet
        assertEquals(
            "Stock(medicineId=12345, aisleId=A1, quantity=100, name=Painkillers)",
            stock.toString()
        )
    }
}