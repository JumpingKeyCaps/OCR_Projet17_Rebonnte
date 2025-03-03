package com.openclassrooms.rebonnte

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 *  This application class is the entry point into the application process used for global application-level
 *  initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class RebonnteApplication : Application() {
}