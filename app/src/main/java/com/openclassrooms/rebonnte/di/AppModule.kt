package com.openclassrooms.rebonnte.di

import com.openclassrooms.rebonnte.data.repository.AisleRepository
import com.openclassrooms.rebonnte.data.repository.AuthenticationRepository
import com.openclassrooms.rebonnte.data.repository.HistoryRepository
import com.openclassrooms.rebonnte.data.repository.MedicineRepository
import com.openclassrooms.rebonnte.data.repository.StockRepository
import com.openclassrooms.rebonnte.data.repository.UserRepository
import com.openclassrooms.rebonnte.data.service.authentication.FirebaseAuthService
import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
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


    /**
     * Provides a Singleton instance of FireStoreService.
     * @return A Singleton instance of FireStoreService.
     */
    @Provides
    @Singleton
    fun provideFireStoreService(): FireStoreService {
        return FireStoreService()

    }

    /**
     * Provides a Singleton instance of AisleRepository.
     * @param fireStoreService The FireStoreService instance.
     * @return A Singleton instance of AisleRepository.
     */
    @Provides
    @Singleton
    fun provideAisleRepository(fireStoreService: FireStoreService): AisleRepository {
        return AisleRepository(fireStoreService)
    }

    /**
     * Provides a Singleton instance of MedicineRepository.
     * @param fireStoreService The FireStoreService instance.
     * @return A Singleton instance of MedicineRepository.
     */
    @Provides
    @Singleton
    fun provideMedicineRepository(fireStoreService: FireStoreService): MedicineRepository {
        return MedicineRepository(fireStoreService)
    }

    /**
     * Provides a Singleton instance of StockRepository.
     * @param fireStoreService The FireStoreService instance.
     * @return A Singleton instance of StockRepository.
     */
    @Provides
    @Singleton
    fun provideStockRepository(fireStoreService: FireStoreService): StockRepository {
        return StockRepository(fireStoreService)
    }

    /**
     * Provides a Singleton instance of HistoryRepository.
     * @param fireStoreService The FireStoreService instance.
     * @return A Singleton instance of HistoryRepository.
     */
    @Provides
    @Singleton
    fun provideHistoryRepository(fireStoreService: FireStoreService): HistoryRepository {
        return HistoryRepository(fireStoreService)
    }

    /**
     * Provides a Singleton instance of UserRepository.
     * @param fireStoreService The FireStoreService instance.
     * @return A Singleton instance of UserRepository.
     */
    @Provides
    @Singleton
    fun provideUserRepository(fireStoreService: FireStoreService): UserRepository {
        return UserRepository(fireStoreService)
    }






}