package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class FitnessRepository(private val context: Context) {

    private val db = FitnessDatabase.getDatabase(context)
    private val dao = db.fitnessDao()

    val userStats: Flow<UserStats?> = dao.getUserStatsFlow()
    val workoutLogs: Flow<List<WorkoutLog>> = dao.getAllWorkoutLogsFlow()
    val activeWorkout: Flow<ActiveWorkout?> = dao.getActiveWorkoutFlow()
    val achievements: Flow<List<Achievement>> = dao.getAchievementsFlow()

    fun getWaterLogs(dateString: String): Flow<List<WaterLog>> = dao.getWaterLogsForDateFlow(dateString)
    fun getTotalWater(dateString: String): Flow<Int?> = dao.getTotalWaterForDateFlow(dateString)
    fun getNutritionLogs(dateString: String): Flow<List<NutritionLog>> = dao.getNutritionLogsForDateFlow(dateString)

    suspend fun initializeData() {
        // Seed user stats if not exist
        val currentStats = dao.getUserStats()
        if (currentStats == null) {
            dao.insertUserStats(UserStats())
        }

        // Seed default achievements if they don't exist in the database
        val currentAchievements = dao.getAchievementsFlow().firstOrNull() ?: emptyList()
        if (currentAchievements.isEmpty()) {
            val defaults = listOf(
                Achievement("first_steps", "First Steps", "Complete your first workout session.", false, 0L, "ic_directions_run"),
                Achievement("saitama_apprentice", "Saitama Apprentice", "Complete a workout on Easy or Medium difficulty.", false, 0L, "ic_fitness_center"),
                Achievement("hero_in_training", "Hero in Training", "Complete 5 workout sessions.", false, 0L, "ic_local_fire_department"),
                Achievement("one_punch", "One Punch Hero", "Complete the FULL Hard Saitama Challenge (100 Reps, 10km Run).", false, 0L, "ic_workspace_premium"),
                Achievement("water_fighter", "Water Fighter", "Log your hydration water intake.", false, 0L, "ic_water_drop"),
                Achievement("nutri_champ", "Nutri Champ", "Log nutrition intake for your daily meals.", false, 0L, "ic_restaurant"),
                Achievement("discipline", "Discipline Master", "Reach a 3-day workout streak.", false, 0L, "ic_calendar_today")
            )
            dao.insertAchievements(defaults)
        }
    }

    suspend fun addWater(dateString: String, amountMl: Int) {
        dao.insertWaterLog(WaterLog(dateString = dateString, amountMl = amountMl))
        
        // Update today's total water in user stats
        val stats = dao.getUserStats() ?: UserStats()
        val todayTotal = stats.waterIntakeTodayMl + amountMl
        dao.insertUserStats(stats.copy(waterIntakeTodayMl = todayTotal))

        // Check water achievement
        unlockAchievement("water_fighter")
    }

    suspend fun addNutrition(dateString: String, foodName: String, calories: Int, protein: Int, carbs: Int, fat: Int) {
        dao.insertNutritionLog(
            NutritionLog(
                dateString = dateString,
                foodName = foodName,
                calories = calories,
                proteinGrams = protein,
                carbsGrams = carbs,
                fatGrams = fat
            )
        )

        // Update stats
        val stats = dao.getUserStats() ?: UserStats()
        dao.insertUserStats(
            stats.copy(
                caloriesToday = stats.caloriesToday + calories,
                proteinToday = stats.proteinToday + protein,
                carbsToday = stats.carbsToday + carbs,
                fatToday = stats.fatToday + fat
            )
        )

        // Check nutrition achievement
        unlockAchievement("nutri_champ")
    }

    suspend fun updateProfile(weight: Float, height: Float, bodyFat: Float, calorieGoal: Int, waterGoal: Int) {
        val stats = dao.getUserStats() ?: UserStats()
        dao.insertUserStats(
            stats.copy(
                weightKg = weight,
                heightCm = height,
                bodyFatPercentage = bodyFat,
                calorieGoal = calorieGoal,
                waterGoalMl = waterGoal
            )
        )
    }

    suspend fun saveActiveWorkout(active: ActiveWorkout) {
        dao.insertActiveWorkout(active)
    }

    suspend fun clearActiveWorkout() {
        dao.deleteActiveWorkout()
    }

    suspend fun saveCompletedWorkout(
        planName: String,
        difficulty: String,
        pushups: Int,
        situps: Int,
        squats: Int,
        runKm: Float,
        durationSeconds: Int,
        caloriesBurned: Int
    ) {
        // Save workout log
        val log = WorkoutLog(
            planName = planName,
            difficulty = difficulty,
            pushupsCompleted = pushups,
            situpsCompleted = situps,
            squatsCompleted = squats,
            runDistanceCompletedKm = runKm,
            durationSeconds = durationSeconds,
            caloriesBurned = caloriesBurned,
            isCompleted = true
        )
        dao.insertWorkoutLog(log)

        // Calculate XP earned based on difficulty
        val xpEarned = when (difficulty.lowercase()) {
            "easy" -> 50
            "medium" -> 150
            "hard" -> 500  // Saitama original
            "extreme" -> 1000
            else -> 100 // Custom
        }

        // Update User Stats
        val stats = dao.getUserStats() ?: UserStats()
        
        // Handle Streak Logic
        val today = getTodayDateString()
        val lastActive = getDateStringFromTimestamp(stats.lastActiveTimestamp)
        
        var newStreak = stats.streak
        if (lastActive != today) {
            val yesterday = getYesterdayDateString()
            if (lastActive == yesterday) {
                newStreak += 1
            } else {
                newStreak = 1
            }
        }
        val maxStreak = if (newStreak > stats.longestStreak) newStreak else stats.longestStreak

        // XP & Level up calculation
        var newXp = stats.xp + xpEarned
        var newLevel = stats.level
        var nextLevelThreshold = getXpThresholdForLevel(newLevel)
        
        while (newXp >= nextLevelThreshold) {
            newXp -= nextLevelThreshold
            newLevel += 1
            nextLevelThreshold = getXpThresholdForLevel(newLevel)
        }

        val updatedStrengthScore = stats.strengthScore + (xpEarned / 10)
        val finalFitnessLevel = when {
            newLevel >= 15 -> "Hero Class S"
            newLevel >= 10 -> "Hero Class A"
            newLevel >= 6 -> "Hero Class B"
            newLevel >= 3 -> "Hero Class C"
            else -> "Beginner"
        }

        dao.insertUserStats(
            stats.copy(
                xp = newXp,
                level = newLevel,
                streak = newStreak,
                longestStreak = maxStreak,
                lastActiveTimestamp = System.currentTimeMillis(),
                strengthScore = updatedStrengthScore,
                fitnessLevel = finalFitnessLevel
            )
        )

        // Clear active workout state
        dao.deleteActiveWorkout()

        // Achievement unlocking validations
        unlockAchievement("first_steps")
        
        if (difficulty.equals("easy", true) || difficulty.equals("medium", true)) {
            unlockAchievement("saitama_apprentice")
        }
        
        if (difficulty.equals("hard", true)) {
            unlockAchievement("one_punch")
        }

        if (newStreak >= 3) {
            unlockAchievement("discipline")
        }

        // Check for 5 workouts
        val logs = dao.getAllWorkoutLogsFlow().firstOrNull() ?: emptyList()
        val completedCount = logs.count { it.isCompleted }
        if (completedCount >= 5) {
            unlockAchievement("hero_in_training")
        }
    }

    private suspend fun unlockAchievement(id: String) {
        val list = dao.getAchievementsFlow().firstOrNull() ?: return
        val achievement = list.find { it.id == id }
        if (achievement != null && !achievement.isUnlocked) {
            dao.updateAchievement(
                achievement.copy(
                    isUnlocked = true,
                    unlockedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun getXpThresholdForLevel(level: Int): Int {
        return level * 200
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getDateStringFromTimestamp(ts: Long): String {
        if (ts == 0L) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(ts))
    }
}
