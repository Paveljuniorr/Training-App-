package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    // Workout Logs
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogsFlow(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog): Long

    // Active Workout (Saitama Routine state / Continue Workout)
    @Query("SELECT * FROM active_workout WHERE id = 1")
    fun getActiveWorkoutFlow(): Flow<ActiveWorkout?>

    @Query("SELECT * FROM active_workout WHERE id = 1")
    suspend fun getActiveWorkout(): ActiveWorkout?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveWorkout(workout: ActiveWorkout)

    @Query("DELETE FROM active_workout WHERE id = 1")
    suspend fun deleteActiveWorkout()

    // Hydration Tracker
    @Query("SELECT * FROM water_logs WHERE dateString = :dateString ORDER BY timestamp ASC")
    fun getWaterLogsForDateFlow(dateString: String): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE dateString = :dateString")
    fun getTotalWaterForDateFlow(dateString: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLog)

    // Nutrition Tracker
    @Query("SELECT * FROM nutrition_logs WHERE dateString = :dateString ORDER BY timestamp ASC")
    fun getNutritionLogsForDateFlow(dateString: String): Flow<List<NutritionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionLog(log: NutritionLog)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAchievementsFlow(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)
}
