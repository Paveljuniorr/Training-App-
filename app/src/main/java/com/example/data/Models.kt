package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveTimestamp: Long = 0L,
    val weightKg: Float = 70.0f,
    val heightCm: Float = 175.0f,
    val bodyFatPercentage: Float = 15.0f,
    val waterGoalMl: Int = 2500,
    val waterIntakeTodayMl: Int = 0,
    val calorieGoal: Int = 2500,
    val caloriesToday: Int = 0,
    val proteinToday: Int = 0,
    val carbsToday: Int = 0,
    val fatToday: Int = 0,
    val fitnessLevel: String = "Beginner",
    val strengthScore: Int = 100
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val planName: String,
    val difficulty: String,
    val pushupsCompleted: Int,
    val situpsCompleted: Int,
    val squatsCompleted: Int,
    val runDistanceCompletedKm: Float,
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val isCompleted: Boolean = true
)

@Entity(tableName = "active_workout")
data class ActiveWorkout(
    @PrimaryKey val id: Int = 1,
    val planName: String = "Saitama Challenge",
    val difficulty: String = "Easy",
    val pushupsCurrent: Int = 0,
    val pushupsTarget: Int = 10,
    val situpsCurrent: Int = 0,
    val situpsTarget: Int = 10,
    val squatsCurrent: Int = 0,
    val squatsTarget: Int = 10,
    val runCurrentKm: Float = 0.0f,
    val runTargetKm: Float = 1.0f,
    val durationSeconds: Int = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val amountMl: Int
)

@Entity(tableName = "nutrition_logs")
data class NutritionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val foodName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long = 0L,
    val iconName: String
)
