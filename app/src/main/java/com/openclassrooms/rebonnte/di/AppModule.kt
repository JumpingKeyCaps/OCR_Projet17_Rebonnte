package com.openclassrooms.rebonnte.di

import com.openclassrooms.rebonnte.data.repository.AuthenticationRepository
import com.openclassrooms.rebonnte.data.service.authentication.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    /**
     * Provides a Singleton instance of FirebaseAuthService.
     * @return A Singleton instance of FirebaseAuthService.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }

    /**
     * Provide a singleton instance of Authentication Repository
     * @param authService The Authentication Service
     * @return The Authentication Repository
     */
    @Provides
    @Singleton
    fun provideAuthRepository(authService: FirebaseAuthService): AuthenticationRepository {
        return AuthenticationRepository(authService)
    }


}