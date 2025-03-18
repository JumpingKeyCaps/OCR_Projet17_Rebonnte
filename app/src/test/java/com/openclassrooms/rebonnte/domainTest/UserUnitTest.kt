package com.openclassrooms.rebonnte.domainTest

import com.openclassrooms.rebonnte.domain.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class UserUnitTest {

    @Test
    fun testUserDefaultValues() {
        val user = User()

        // Vérifie que les valeurs par défaut sont correctes
        assertEquals("", user.id)
        assertEquals("", user.firstname)
        assertEquals("", user.lastname)
        assertEquals("", user.email)
    }

    @Test
    fun testUserCustomValues() {
        val user = User(id = "1", firstname = "John", lastname = "Doe", email = "john.doe@example.com")

        // Vérifie que les valeurs personnalisées sont correctement assignées
        assertEquals("1", user.id)
        assertEquals("John", user.firstname)
        assertEquals("Doe", user.lastname)
        assertEquals("john.doe@example.com", user.email)
    }

    @Test
    fun testUserEquality() {
        val user1 = User(id = "1", firstname = "John", lastname = "Doe", email = "john.doe@example.com")
        val user2 = User(id = "1", firstname = "John", lastname = "Doe", email = "john.doe@example.com")
        val user3 = User(id = "2", firstname = "Jane", lastname = "Doe", email = "jane.doe@example.com")

        // Vérifie que deux objets avec les mêmes valeurs sont considérés comme égaux
        assertTrue(user1 == user2)

        // Vérifie que deux objets avec des valeurs différentes ne sont pas égaux
        assertFalse(user1 == user3)
    }

    @Test
    fun testUserToString() {
        val user = User(id = "1", firstname = "John", lastname = "Doe", email = "john.doe@example.com")

        // Vérifie que la méthode toString() renvoie une représentation correcte de l'objet
        assertEquals("User(id=1, firstname=John, lastname=Doe, email=john.doe@example.com)", user.toString())
    }
}