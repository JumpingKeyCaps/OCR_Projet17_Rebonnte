package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.Medicine
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MedicineUnitTest {
    @Test
    fun testMedicineInitialization() {
        val medicine = Medicine(
            medicineId = "12345",
            name = "Paracetamol",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "PharmaCo",
            indication = "Fever",
            principeActif = "Paracetamol",
            utilisation = "Oral",
            warning = "May cause drowsiness",
            createdAt = 1634312400000L
        )

        // Vérification que les valeurs sont correctement affectées
        assertEquals("12345", medicine.medicineId)
        assertEquals("Paracetamol", medicine.name)
        assertEquals("Pain reliever", medicine.description)
        assertEquals("500mg", medicine.dosage)
        assertEquals("PharmaCo", medicine.fabricant)
        assertEquals("Fever", medicine.indication)
        assertEquals("Paracetamol", medicine.principeActif)
        assertEquals("Oral", medicine.utilisation)
        assertEquals("May cause drowsiness", medicine.warning)
        assertEquals(1634312400000L, medicine.createdAt)
    }

    @Test
    fun testMedicineEquality() {
        val medicine1 = Medicine(
            medicineId = "12345",
            name = "Paracetamol",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "PharmaCo",
            indication = "Fever",
            principeActif = "Paracetamol",
            utilisation = "Oral",
            warning = "May cause drowsiness",
            createdAt = 1634312400000L
        )

        val medicine2 = Medicine(
            medicineId = "12345",
            name = "Paracetamol",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "PharmaCo",
            indication = "Fever",
            principeActif = "Paracetamol",
            utilisation = "Oral",
            warning = "May cause drowsiness",
            createdAt = 1634312400000L
        )

        val medicine3 = Medicine(
            medicineId = "67890",
            name = "Aspirin",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "PharmaCo",
            indication = "Pain",
            principeActif = "Aspirin",
            utilisation = "Oral",
            warning = "May cause stomach irritation",
            createdAt = 1634312400000L
        )

        // Vérification que deux objets identiques sont égaux
        assertTrue(medicine1 == medicine2)

        // Vérification que deux objets différents ne sont pas égaux
        assertFalse(medicine1 == medicine3)
    }


    @Test
    fun testMedicineToString() {
        val medicine = Medicine(
            medicineId = "12345",
            name = "Paracetamol",
            description = "Pain reliever",
            dosage = "500mg",
            fabricant = "PharmaCo",
            indication = "Fever",
            principeActif = "Paracetamol",
            utilisation = "Oral",
            warning = "May cause drowsiness",
            createdAt = 1634312400000L
        )

        val expectedString = "Medicine(medicineId=12345, name=Paracetamol, description=Pain reliever, dosage=500mg, fabricant=PharmaCo, indication=Fever, principeActif=Paracetamol, utilisation=Oral, warning=May cause drowsiness, createdAt=1634312400000)"
        assertEquals(expectedString, medicine.toString())
    }

    @Test
    fun testDefaultValues() {
        val medicine = Medicine()

        assertEquals("", medicine.medicineId)
        assertEquals("", medicine.name)
        assertEquals("", medicine.description)
        assertEquals("", medicine.dosage)
        assertEquals("", medicine.fabricant)
        assertEquals("", medicine.indication)
        assertEquals("", medicine.principeActif)
        assertEquals("", medicine.utilisation)
        assertEquals("", medicine.warning)
        assertEquals(0L, medicine.createdAt)
    }

}