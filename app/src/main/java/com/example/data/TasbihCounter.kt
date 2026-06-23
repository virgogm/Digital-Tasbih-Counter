package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbih_counters")
data class TasbihCounter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val currentCount: Int = 0,
    val lapCount: Int = 0,
    val targetLimit: Int = 33, // target/limit (e.g. 33, 100, 300, 1000, or arbitrary custom positive integer)
    val lapLimit: Int = 33, // user defined threshold that triggers lap increment and resets loop to 0
    val themeColorHex: String = "#B8C6C3", // Primary dial/accent color
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val totalAccumulated: Int = 0 // historical accumulated count statistic
)
