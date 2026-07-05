package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class GeminiRepository {
    suspend fun getCoachResponse(userStats: UserStats?, prompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Hi there! I am Saitama's AI Assistant Coach. To enable fully customized AI plans and advice, please configure your Gemini API key in the AI Studio Secrets panel. Let me give you standard advice for now: Push through! Do 100 pushups, 100 situps, 100 squats, and run 10km every single day! (Or scale down if you are a beginner!)"
        }

        val systemPrompt = """
            You are Saitama's Personal Fitness Coach, a supportive, highly disciplined, slightly humorous personal trainer inspired by the Saitama workout. 
            You must suggest workouts, adjust difficulty, detect overtraining, recommend recovery, and give motivational advice.
            Here are the current stats of the athlete you are coaching:
            - Level: ${userStats?.level ?: 1}
            - Streak: ${userStats?.streak ?: 0} days (Longest: ${userStats?.longestStreak ?: 0} days)
            - Current Classification: ${userStats?.fitnessLevel ?: "Beginner"}
            - Weight: ${userStats?.weightKg ?: 70f} kg, Height: ${userStats?.heightCm ?: 175f} cm, Body Fat: ${userStats?.bodyFatPercentage ?: 15f}%
            - Strength Score: ${userStats?.strengthScore ?: 100}
            
            Always keep your answers encouraging, clear, and aligned with calisthenics (pushups, situps, squats, pullups, plank) and endurance running. Scalability and safety for beginners is paramount! Never advise them to hurt themselves. Do not suggest weight lifting, focus purely on calisthenics and running.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I couldn't process that request. Keep pushing forward anyway!"
        } catch (e: Exception) {
            "Coach Error: ${e.localizedMessage}. Keep training hard offline!"
        }
    }
}
