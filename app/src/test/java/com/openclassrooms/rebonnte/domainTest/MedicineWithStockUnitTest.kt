package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.MedicineWithStock
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MedicineWithStockUnitTest {
    @Test
    fun testMedicineWithStockDefaultValues() {
        val medicineWithStock = MedicineWithStock()

        // Vérifie que les valeurs par défaut sont correctes
        assertEquals("", medicineWithStock.medicineId)
        assertEquals("", medicineWithStock.name)
        assertEquals("", medicineWithStock.description)
        assertEquals("", medicineWithStock.dosage)
        assertEquals("", medicineWithStock.fabricant)
        assertEquals("", medicineWithStock.indication)
        assertEquals("", medicineWithStock.principeActif)
        assertEquals("", medicineWithStock.utilisation)
        assertEquals("", medicineWithStock.warning)
        assertEquals(0L, medicineWithStock.createdAt)
        assertEquals(0, medicineWithStock.quantity)
    }

    @Test
    fun testMedicineWithStockCustomValues() {
        val medicineWithStock = MedicineWithStock(
            medicineId = "12345",
            name = "Aspirin",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "XYZ Pharma",
            indication = "Pain relief",
            principeActif = "Acetylsalicylic acid",
            utilisation = "Oral",
            warning = "May cause stomach irritation",
            createdAt = 1616161616161L,
            quantity = 100
        )

        // Vérifie que les valeurs personnalisées sont correctement assignées
        assertEquals("12345", medicineWithStock.medicineId)
        assertEquals("Aspirin", medicineWithStock.name)
        assertEquals("Pain reliever", medicineWithStock.description)
        assertEquals("500mg", medicineWithStock.dosage)
        assertEquals("XYZ Pharma", medicineWithStock.fabricant)
        assertEquals("Pain relief", medicineWithStock.indication)
        assertEquals("Acetylsalicylic acid", medicineWithStock.principeActif)
        assertEquals("Oral", medicineWithStock.utilisation)
        assertEquals("May cause stomach irritation", medicineWithStock.warning)
        assertEquals(1616161616161L, medicineWithStock.createdAt)
        assertEquals(100, medicineWithStock.quantity)
    }

    @Test
    fun testMedicineWithStockEquality() {
        val medicineWithStock1 = MedicineWithStock(
            medicineId = "12345",
            name = "Aspirin",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "XYZ Pharma",
            indication = "Pain relief",
            principeActif = "Acetylsalicylic acid",
            utilisation = "Oral",
            warning = "May cause stomach irritation",
            createdAt = 1616161616161L,
            quantity = 100
        )

        val medicineWithStock2 = MedicineWithStock(
            medicineId = "12345",
            name = "Aspirin",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "XYZ Pharma",
            indication = "Pain relief",
            principeActif = "Acetylsalicylic acid",
            utilisation = "Oral",
            warning = "May cause stomach irritation",
            createdAt = 1616161616161L,
            quantity = 100
        )

        val medicineWithStock3 = MedicineWithStock(
            medicineId = "67890",
            name = "Paracetamol",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "ABC Pharma",
            indication = "Pain relief",
            principeActif = "Paracetamol",
            utilisation = "Oral",
            warning = "May cause liver damage",
            createdAt = 1616161616162L,
            quantity = 50
        )

        // Vérifie que deux objets avec les mêmes valeurs sont considérés comme égaux
        assertTrue(medicineWithStock1 == medicineWithStock2)

        // Vérifie que deux objets avec des valeurs différentes ne sont pas égaux
        assertFalse(medicineWithStock1 == medicineWithStock3)
    }

    @Test
    fun testMedicineWithStockToString() {
        val medicineWithStock = MedicineWithStock(
            medicineId = "12345",
            name = "Aspirin",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "XYZ Pharma",
            indication = "Pain relief",
            principeActif = "Acetylsalicylic acid",
            utilisation = "Oral",
            warning = "May cause stomach irritation",
            createdAt = 1616161616161L,
            quantity = 100
        )

        // Vérifie que la méthode toString() renvoie une représentation correcte de l'objet
        assertEquals(
            "MedicineWithStock(medicineId=12345, name=Aspirin, description=Pain reliever, dosage=500mg, fabricant=XYZ Pharma, indication=Pain relief, principeActif=Acetylsalicylic acid, utilisation=Oral, warning=May cause stomach irritation, createdAt=1616161616161, quantity=100)",
            medicineWithStock.toString()
        )
    }
}