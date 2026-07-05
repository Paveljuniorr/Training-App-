package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FitnessRepository(application.applicationContext)
    private val geminiRepository = GeminiRepository()

    // Database Flows
    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.workoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWorkout: StateFlow<ActiveWorkout?> = repository.activeWorkout
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Date-specific trackers (flows updated on date selection, defaulting to today)
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val todayWaterLogs: StateFlow<List<WaterLog>> = _selectedDate
        .flatMapLatest { date -> repository.getWaterLogs(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWaterTotal: StateFlow<Int> = _selectedDate
        .flatMapLatest { date -> repository.getTotalWater(date) }
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayNutritionLogs: StateFlow<List<NutritionLog>> = _selectedDate
        .flatMapLatest { date -> repository.getNutritionLogs(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Coach State
    private val _aiCoachResponse = MutableStateFlow<String>("")
    val aiCoachResponse: StateFlow<String> = _aiCoachResponse.asStateFlow()

    private val _aiCoachLoading = MutableStateFlow(false)
    val aiCoachLoading: StateFlow<Boolean> = _aiCoachLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeData()
        }
    }

    // Hydration
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(_selectedDate.value, amountMl)
        }
    }

    // Nutrition
    fun addNutrition(foodName: String, calories: Int, protein: Int, carbs: Int, fat: Int) {
        viewModelScope.launch {
            repository.addNutrition(_selectedDate.value, foodName, calories, protein, carbs, fat)
        }
    }

    // User Profile
    fun updateProfile(weight: Float, height: Float, bodyFat: Float, calorieGoal: Int, waterGoal: Int) {
        viewModelScope.launch {
            repository.updateProfile(weight, height, bodyFat, calorieGoal, waterGoal)
        }
    }

    // Active Workout Operations (Saitama Challenge feature)
    fun startActiveWorkout(planName: String, difficulty: String, pushups: Int, situps: Int, squats: Int, runKm: Float) {
        viewModelScope.launch {
            val workout = ActiveWorkout(
                planName = planName,
                difficulty = difficulty,
                pushupsCurrent = 0,
                pushupsTarget = pushups,
                situpsCurrent = 0,
                situpsTarget = situps,
                squatsCurrent = 0,
                squatsTarget = squats,
                runCurrentKm = 0.0f,
                runTargetKm = runKm,
                durationSeconds = 0,
                lastActiveTimestamp = System.currentTimeMillis(),
                isActive = true
            )
            repository.saveActiveWorkout(workout)
        }
    }

    fun updateActiveWorkoutProgress(pushups: Int, situps: Int, squats: Int, runKm: Float, durationSeconds: Int) {
        viewModelScope.launch {
            val current = repository.activeWorkout.firstOrNull() ?: return@launch
            val updated = current.copy(
                pushupsCurrent = pushups,
                situpsCurrent = situps,
                squatsCurrent = squats,
                runCurrentKm = runKm,
                durationSeconds = durationSeconds,
                lastActiveTimestamp = System.currentTimeMillis()
            )
            repository.saveActiveWorkout(updated)
        }
    }

    fun pauseActiveWorkout() {
        viewModelScope.launch {
            val current = repository.activeWorkout.firstOrNull() ?: return@launch
            val updated = current.copy(isActive = false)
            repository.saveActiveWorkout(updated)
        }
    }

    fun resumeActiveWorkout() {
        viewModelScope.launch {
            val current = repository.activeWorkout.firstOrNull() ?: return@launch
            val updated = current.copy(isActive = true)
            repository.saveActiveWorkout(updated)
        }
    }

    fun cancelActiveWorkout() {
        viewModelScope.launch {
            repository.clearActiveWorkout()
        }
    }

    fun completeActiveWorkout() {
        viewModelScope.launch {
            val current = repository.activeWorkout.firstOrNull() ?: return@launch
            
            // Calculate dynamic calories burned (rough approximation for calisthenics)
            val durationMin = current.durationSeconds / 60.0f
            val baseCal = durationMin * 6.5f // avg 6.5 cal/min for calisthenics
            val pushupCal = current.pushupsCurrent * 0.4f
            val situpCal = current.situpsCurrent * 0.3f
            val squatCal = current.squatsCurrent * 0.5f
            val runCal = current.runCurrentKm * 65.0f // ~65 cal per km run
            
            val totalCal = (baseCal + pushupCal + situpCal + squatCal + runCal).toInt().coerceAtLeast(15)

            repository.saveCompletedWorkout(
                planName = current.planName,
                difficulty = current.difficulty,
                pushups = current.pushupsCurrent,
                situps = current.situpsCurrent,
                squats = current.squatsCurrent,
                runKm = current.runCurrentKm,
                durationSeconds = current.durationSeconds,
                caloriesBurned = totalCal
            )
        }
    }

    // AI Trainer Advice Flow
    fun askCoach(prompt: String) {
        viewModelScope.launch {
            _aiCoachLoading.value = true
            val stats = userStats.value
            val response = geminiRepository.getCoachResponse(stats, prompt)
            _aiCoachResponse.value = response
            _aiCoachLoading.value = false
        }
    }

    fun clearCoachChat() {
        _aiCoachResponse.value = ""
    }

    // Helper utilities
    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
