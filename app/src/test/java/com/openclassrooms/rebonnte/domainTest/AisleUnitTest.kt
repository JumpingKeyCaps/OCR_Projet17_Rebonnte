package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.Aisle
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AisleUnitTest {

    @Test
    fun testAisleInitialization() {
        val aisle = Aisle(aisleId = "1", name = "Aisle 1", description = "Description 1", createdAt = 123456789L)

        assertEquals("1", aisle.aisleId)
        assertEquals("Aisle 1", aisle.name)
        assertEquals("Description 1", aisle.description)
        assertEquals(123456789L, aisle.createdAt)
    }

    @Test
    fun testAisleDefaultValues() {
        val aisle = Aisle()

        assertEquals("", aisle.aisleId)
        assertEquals("", aisle.name)
        assertEquals("", aisle.description)
        assertEquals(0L, aisle.createdAt)
    }
}