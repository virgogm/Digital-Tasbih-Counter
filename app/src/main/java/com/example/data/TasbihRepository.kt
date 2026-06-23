package com.example.data

import kotlinx.coroutines.flow.Flow

class TasbihRepository(private val tasbihDao: TasbihDao) {
    val allCounters: Flow<List<TasbihCounter>> = tasbihDao.getAllCounters()

    fun getCounterById(id: Int): Flow<TasbihCounter?> {
        return tasbihDao.getCounterById(id)
    }

    suspend fun insertCounter(counter: TasbihCounter): Long {
        return tasbihDao.insertCounter(counter)
    }

    suspend fun updateCounter(counter: TasbihCounter) {
        tasbihDao.updateCounter(counter)
    }

    suspend fun deleteCounterById(id: Int) {
        tasbihDao.deleteCounterById(id)
    }

    suspend fun deleteCounter(counter: TasbihCounter) {
        tasbihDao.deleteCounter(counter)
    }
}
