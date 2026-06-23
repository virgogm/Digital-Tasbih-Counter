package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_counters ORDER BY lastUpdatedAt DESC")
    fun getAllCounters(): Flow<List<TasbihCounter>>

    @Query("SELECT * FROM tasbih_counters WHERE id = :id")
    fun getCounterById(id: Int): Flow<TasbihCounter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: TasbihCounter): Long

    @Update
    suspend fun updateCounter(counter: TasbihCounter)

    @Query("DELETE FROM tasbih_counters WHERE id = :id")
    suspend fun deleteCounterById(id: Int)

    @Delete
    suspend fun deleteCounter(counter: TasbihCounter)
}
