package com.openclassrooms.rebonnte

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 *  This application class is the entry point into the application process used for global application-level
 *  initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class RebonnteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)


    }
}