package com.openclassrooms.rebonnte.data.repository

import com.openclassrooms.rebonnte.data.service.firestore.FireStoreService
import com.openclassrooms.rebonnte.domain.StockHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val fireStoreService: FireStoreService){

    fun getHistoryForMedicine(medicineId: String): Flow<List<StockHistory>> =
        fireStoreService.getHistoryForMedicine(medicineId)

    fun getAllHistory(): Flow<List<StockHistory>> =
        fireStoreService.getAllHistory()

}