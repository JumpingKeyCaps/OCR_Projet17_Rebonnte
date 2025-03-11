package com.openclassrooms.rebonnte.domain

import androidx.annotation.Keep
import java.io.Serializable

/**
 * User data model.
 * Represents an user with his various attributes.
 * @property id the user unique identifier
 * @property firstname the user firstname
 * @property lastname the user lastname
 * @property email the user email
 */
@Keep
data class User(
    val id: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = ""
) : Serializable