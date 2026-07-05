package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// DATA CLASSES & HELPERS
// ==========================================
data class QuadrupleTier(
    val title: String,
    val description: String,
    val diffName: String,
    val pushups: Int,
    val situps: Int,
    val squats: Int,
    val runKm: Float,
    val runDesc: String,
    val glowColor: Color
)

@Composable
fun RepItem(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier
            .background(CosmicSurfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun ExerciseTabItem(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) HeroBlue else CosmicSurface
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (selected) HeroBlue else CosmicBorder),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) CosmicBg else TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

fun updateSpecificCount(type: String, count: Any, viewModel: FitnessViewModel, active: ActiveWorkout) {
    val pu = if (type == "pushup") count as Int else active.pushupsCurrent
    val su = if (type == "situp") count as Int else active.situpsCurrent
    val sq = if (type == "squat") count as Int else active.squatsCurrent
    val run = if (type == "run") count as Float else active.runCurrentKm
    viewModel.updateActiveWorkoutProgress(pu, su, sq, run, active.durationSeconds)
}

// ==========================================
// CENTRAL NAV TAB DEFINITION
// ==========================================
enum class AppTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Home),
    CHALLENGE("Train", Icons.Default.FitnessCenter),
    LIBRARY("Library", Icons.Default.MenuBook),
    NUTRITION("Hydration", Icons.Default.Restaurant),
    COACH("AI Coach", Icons.Default.ChatBubble),
    PROFILE("Profile", Icons.Default.Person)
}

// ==========================================
// CUSTOM VECTOR STICK-FIGURE CANVAS ANIMATIONS
// ==========================================
@Composable
fun AnimatedStickFigure(type: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "stick_figure_anim")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        when (type.lowercase()) {
            "pushup" -> {
                // Floor line
                drawLine(
                    color = CosmicBorder,
                    start = Offset(centerX - 80.dp.toPx(), centerY + 30.dp.toPx()),
                    end = Offset(centerX + 80.dp.toPx(), centerY + 30.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )
                val pushupHeightOffset = progress * 15.dp.toPx()
                val feetX = centerX - 60.dp.toPx()
                val hipsX = centerX - 20.dp.toPx()
                val chestX = centerX + 25.dp.toPx()
                val headX = centerX + 50.dp.toPx()

                val feetY = centerY + 25.dp.toPx()
                val hipsY = centerY + 20.dp.toPx() - (pushupHeightOffset * 0.5f)
                val chestY = centerY + 18.dp.toPx() - pushupHeightOffset
                val headY = chestY - 8.dp.toPx()

                // Draw Back leg-body-neck line
                drawLine(color = HeroBlue, start = Offset(feetX, feetY), end = Offset(hipsX, hipsY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlue, start = Offset(hipsX, hipsY), end = Offset(chestX, chestY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlue, start = Offset(hipsX, hipsY), end = Offset(headX, headY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)

                // Head
                drawCircle(color = HeroBlue, radius = 9.dp.toPx(), center = Offset(headX + 5.dp.toPx(), headY - 5.dp.toPx()))

                // Supporting Arms
                drawLine(color = HeroBlueLight, start = Offset(chestX - 5.dp.toPx(), chestY), end = Offset(chestX - 5.dp.toPx(), centerY + 30.dp.toPx()), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
            }
            "squat" -> {
                val squatFactor = progress
                val headY = centerY - 40.dp.toPx() + (squatFactor * 25.dp.toPx())
                val hipsY = centerY - 5.dp.toPx() + (squatFactor * 25.dp.toPx())
                val kneesY = centerY + 15.dp.toPx()
                val feetY = centerY + 40.dp.toPx()

                val hipsX = centerX - (squatFactor * 14.dp.toPx())
                val kneesX = centerX + (squatFactor * 12.dp.toPx())

                // Floor
                drawLine(color = CosmicBorder, start = Offset(centerX - 40.dp.toPx(), feetY), end = Offset(centerX + 40.dp.toPx(), feetY), strokeWidth = 3.dp.toPx())
                // Head
                drawCircle(color = HeroBlue, radius = 10.dp.toPx(), center = Offset(centerX, headY - 12.dp.toPx()))
                // Torso
                drawLine(color = HeroBlue, start = Offset(centerX, headY), end = Offset(hipsX, hipsY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
                // Thigh
                drawLine(color = HeroBlue, start = Offset(hipsX, hipsY), end = Offset(kneesX, kneesY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                // Shin
                drawLine(color = HeroBlue, start = Offset(kneesX, kneesY), end = Offset(centerX, feetY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
            }
            "situp" -> {
                val angle = progress * 65f
                val feetX = centerX + 35.dp.toPx()
                val hipsX = centerX - 25.dp.toPx()
                val kneesX = centerX + 18.dp.toPx()

                val radians = Math.toRadians((180 - angle).toDouble())
                val torsoLength = 45.dp.toPx()
                val headX = hipsX + (torsoLength * Math.cos(radians)).toFloat()
                val headY = (centerY + 20.dp.toPx()) - (torsoLength * Math.sin(radians)).toFloat()

                // Floor
                drawLine(color = CosmicBorder, start = Offset(centerX - 60.dp.toPx(), centerY + 22.dp.toPx()), end = Offset(centerX + 60.dp.toPx(), centerY + 22.dp.toPx()), strokeWidth = 3.dp.toPx())
                // Legs
                drawLine(color = HeroBlue, start = Offset(hipsX, centerY + 20.dp.toPx()), end = Offset(kneesX, centerY - 5.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlue, start = Offset(kneesX, centerY - 5.dp.toPx()), end = Offset(feetX, centerY + 20.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                // Torso
                drawLine(color = HeroBlue, start = Offset(hipsX, centerY + 20.dp.toPx()), end = Offset(headX, headY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                // Head
                drawCircle(color = HeroBlue, radius = 9.dp.toPx(), center = Offset(headX, headY - 12.dp.toPx()))
            }
            "pullup" -> {
                val hangFactor = progress
                val barY = centerY - 45.dp.toPx()
                val headY = barY + 20.dp.toPx() - (hangFactor * 25.dp.toPx())
                val hipsY = headY + 38.dp.toPx()
                val feetY = hipsY + 30.dp.toPx()

                // Bar
                drawLine(color = CosmicBorder, start = Offset(centerX - 50.dp.toPx(), barY), end = Offset(centerX + 50.dp.toPx(), barY), strokeWidth = 5.dp.toPx())
                // Head
                drawCircle(color = HeroBlue, radius = 9.dp.toPx(), center = Offset(centerX, headY - 12.dp.toPx()))
                // Torso
                drawLine(color = HeroBlue, start = Offset(centerX, headY), end = Offset(centerX, hipsY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
                // Legs hanging down
                drawLine(color = HeroBlue, start = Offset(centerX, hipsY), end = Offset(centerX - 5.dp.toPx(), feetY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                // Arms
                drawLine(color = HeroBlueLight, start = Offset(centerX, headY), end = Offset(centerX - 25.dp.toPx(), barY), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlueLight, start = Offset(centerX, headY), end = Offset(centerX + 25.dp.toPx(), barY), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
            }
            "plank" -> {
                val breathingOffset = progress * 4.dp.toPx()
                // Floor
                drawLine(color = CosmicBorder, start = Offset(centerX - 70.dp.toPx(), centerY + 25.dp.toPx()), end = Offset(centerX + 70.dp.toPx(), centerY + 25.dp.toPx()), strokeWidth = 3.dp.toPx())
                // Horizontal Body
                val feetX = centerX - 60.dp.toPx()
                val hipsX = centerX - 10.dp.toPx()
                val shoulderX = centerX + 40.dp.toPx()
                val headX = centerX + 55.dp.toPx()

                val lineY = centerY + 12.dp.toPx() + breathingOffset

                // Body
                drawLine(color = HeroBlue, start = Offset(feetX, centerY + 20.dp.toPx()), end = Offset(hipsX, lineY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlue, start = Offset(hipsX, lineY), end = Offset(shoulderX, lineY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                // Head
                drawCircle(color = HeroBlue, radius = 9.dp.toPx(), center = Offset(headX, lineY - 8.dp.toPx()))
                // Forearm
                drawLine(color = HeroBlueLight, start = Offset(shoulderX, lineY), end = Offset(shoulderX, centerY + 25.dp.toPx()), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
            }
            else -> {
                // Running/Endurance tracker animation
                val runFactor = progress
                val bounce = if (runFactor > 0.5f) 6.dp.toPx() else 0.dp.toPx()
                val headY = centerY - 30.dp.toPx() - bounce
                val hipsY = centerY + 10.dp.toPx() - bounce

                // Head
                drawCircle(color = HeroBlue, radius = 10.dp.toPx(), center = Offset(centerX, headY - 12.dp.toPx()))
                // Torso
                drawLine(color = HeroBlue, start = Offset(centerX, headY), end = Offset(centerX, hipsY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Legs moving back and forth
                val offsetLeg = (runFactor - 0.5f) * 35.dp.toPx()
                drawLine(color = HeroBlue, start = Offset(centerX, hipsY), end = Offset(centerX + offsetLeg, centerY + 35.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = HeroBlueLight, start = Offset(centerX, hipsY), end = Offset(centerX - offsetLeg, centerY + 35.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)

                // Ground motion lines
                val speedLineX = ((1f - runFactor) * 120.dp.toPx()) - 60.dp.toPx()
                drawLine(color = CosmicBorder, start = Offset(centerX + speedLineX, centerY + 35.dp.toPx()), end = Offset(centerX + speedLineX + 25.dp.toPx(), centerY + 35.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
        }
    }
}

// ==========================================
// TELEMETRY WEEKLY PROGRESS BAR CHART
// ==========================================
@Composable
fun TelemetryWeeklyChart(logs: List<WorkoutLog>, modifier: Modifier = Modifier) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val caloriesByDay = remember(logs) {
        val calendar = Calendar.getInstance()
        val currentWeekCal = FloatArray(7) { 0f }
        val baseMocks = floatArrayOf(240f, 480f, 0f, 320f, 600f, 150f, 0f)

        for (log in logs) {
            calendar.timeInMillis = log.timestamp
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayIndex = when (dayOfWeek) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                Calendar.SUNDAY -> 6
                else -> -1
            }
            if (dayIndex in 0..6) {
                currentWeekCal[dayIndex] += log.caloriesBurned.toFloat()
            }
        }

        // Merge actual calories with nice visualization defaults if empty
        for (i in 0..6) {
            if (currentWeekCal[i] == 0f) {
                currentWeekCal[i] = baseMocks[i]
            }
        }
        currentWeekCal
    }

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val width = size.width
            val height = size.height
            val paddingLeft = 32.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val maxVal = (caloriesByDay.maxOrNull() ?: 500f).coerceAtLeast(300f) * 1.15f
            val stepX = chartWidth / 7f

            // Grid Lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = chartHeight - (chartHeight / gridLines) * i
                drawLine(
                    color = CosmicBorder.copy(alpha = 0.3f),
                    start = Offset(paddingLeft, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Bars
            for (i in 0..6) {
                val x = paddingLeft + i * stepX + (stepX * 0.15f)
                val barW = stepX * 0.7f
                val valToday = caloriesByDay[i]
                val barH = (valToday / maxVal) * chartHeight
                val barY = chartHeight - barH

                if (barH > 2.dp.toPx()) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(HeroBlue, HeroBlueDark),
                            startY = barY,
                            endY = chartHeight
                        ),
                        topLeft = Offset(x, barY),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    drawLine(
                        color = HeroBlueLight,
                        start = Offset(x, barY),
                        end = Offset(x + barW, barY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==========================================
// WATER PROGRESS WAVE COMPONENT
// ==========================================
@Composable
fun WaterWaveProgress(progress: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "water_wave_infinite")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(CosmicSurfaceVariant)
            .border(2.dp, HeroBlue.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val fillHeight = height * (1f - progress.coerceIn(0f, 1f))

            val path = Path()
            path.moveTo(0f, height)
            path.lineTo(0f, fillHeight)

            val waveAmplitude = 10.dp.toPx()
            val waveFrequency = 0.012f

            for (x in 0..width.toInt()) {
                val y = fillHeight + waveAmplitude * Math.sin((x * waveFrequency + waveOffset).toDouble()).toFloat()
                path.lineTo(x.toFloat(), y)
            }
            path.lineTo(width, height)
            path.close()

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(HeroBlueLight, HeroBlue),
                    startY = fillHeight,
                    endY = height
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), blurRadius = 8f))
            )
            Text(
                text = "HYDRATED",
                color = TextPrimary.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                style = TextStyle(shadow = Shadow(color = Color.Black.copy(alpha = 0.6f), blurRadius = 4f))
            )
        }
    }
}

// ==========================================
// TAB 1: DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    onNavigateToTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsState()
    val logs by viewModel.workoutLogs.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()

    val currentStats = stats ?: UserStats()
    val currentLevel = currentStats.level
    val currentXp = currentStats.xp
    val xpNeeded = currentLevel * 200
    val xpProgress = (currentXp.toFloat() / xpNeeded).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Hero Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Saitama Training",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeroBlue,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "WELCOME BACK, HERO!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CosmicSurfaceVariant)
                        .border(1.5.dp, HeroBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Hero avatar",
                        tint = HeroGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // XP & Level Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentStats.fitnessLevel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeroBlueLight
                            )
                            Text(
                                text = "LEVEL $currentLevel HERO",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Score: ${currentStats.strengthScore}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeroGold,
                            modifier = Modifier
                                .background(HeroGold.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = HeroBlue,
                        trackColor = CosmicSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$currentXp / $xpNeeded XP",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(xpProgress * 100).toInt()}% to next rank",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active workout banner if active
        if (activeWorkout != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HeroBlue.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, HeroBlue),
                    modifier = Modifier.clickable { onNavigateToTab(AppTab.CHALLENGE) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE WORKOUT IN PROGRESS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeroBlueLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${activeWorkout?.planName} (${activeWorkout?.difficulty})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Pick up right where you left off!",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Button(
                            onClick = { onNavigateToTab(AppTab.CHALLENGE) },
                            colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Resume", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Stats Grid Rows
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${currentStats.streak} Days", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Record: ${currentStats.longestStreak}d", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Calories Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = HeroGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CALORIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${currentStats.caloriesToday} kcal", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Goal: ${currentStats.calorieGoal}", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Water Tracker Today
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CosmicBorder),
                    onClick = { onNavigateToTab(AppTab.NUTRITION) }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = HeroBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WATER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${currentStats.waterIntakeTodayMl} ml", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Goal: ${currentStats.waterGoalMl}ml", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Suggested Next Training
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CosmicBorder),
                    onClick = { onNavigateToTab(AppTab.CHALLENGE) }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = HeroBlueLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SUGGESTED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Saitama", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Challenge (Easy)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Weekly progress telemetry chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WEEKLY TELEMETRY (BURN)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeroBlueLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Workout Frequency & Burn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    TelemetryWeeklyChart(logs = logs, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Recent Activity log list
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITIES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "View history",
                    fontSize = 11.sp,
                    color = HeroBlue,
                    modifier = Modifier.clickable { onNavigateToTab(AppTab.PROFILE) }
                )
            }
        }

        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicSurface, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No workouts recorded yet.", color = TextSecondary, fontSize = 13.sp)
                        Text("Go to the Train tab and start your first training!", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(logs.take(3)) { log ->
                val dateStr = remember(log.timestamp) {
                    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    sdf.format(Date(log.timestamp))
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = log.planName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Difficulty: ${log.difficulty} • $dateStr", fontSize = 11.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "+${log.caloriesBurned} kcal", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                            Text(
                                text = "${log.durationSeconds / 60} min ${log.durationSeconds % 60}s",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: CHALLENGE SCREEN (ACTIVE TRAINING)
// ==========================================
@Composable
fun ChallengeScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val activeWorkout by viewModel.activeWorkout.collectAsState()

    var showCustomConfig by remember { mutableStateOf(false) }
    var customPushups by remember { mutableStateOf("30") }
    var customSitups by remember { mutableStateOf("30") }
    var customSquats by remember { mutableStateOf("30") }
    var customRun by remember { mutableStateOf("3.0") }

    Box(modifier = modifier.fillMaxSize().background(CosmicBg)) {
        if (activeWorkout != null) {
            ActiveTrainingSuite(
                active = activeWorkout!!,
                viewModel = viewModel
            )
        } else {
            if (showCustomConfig) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "CUSTOM TRAINING REGIMEN",
                        fontSize = 11.sp,
                        color = HeroBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Configure Your Goals",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customPushups,
                        onValueChange = { customPushups = it },
                        label = { Text("Push-ups Target") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeroBlue,
                            unfocusedBorderColor = CosmicBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("custom_pushups_input")
                    )

                    OutlinedTextField(
                        value = customSitups,
                        onValueChange = { customSitups = it },
                        label = { Text("Sit-ups Target") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeroBlue,
                            unfocusedBorderColor = CosmicBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("custom_situps_input")
                    )

                    OutlinedTextField(
                        value = customSquats,
                        onValueChange = { customSquats = it },
                        label = { Text("Squats Target") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeroBlue,
                            unfocusedBorderColor = CosmicBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("custom_squats_input")
                    )

                    OutlinedTextField(
                        value = customRun,
                        onValueChange = { customRun = it },
                        label = { Text("Running Distance (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeroBlue,
                            unfocusedBorderColor = CosmicBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("custom_run_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCustomConfig = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                val pu = customPushups.toIntOrNull() ?: 30
                                val su = customSitups.toIntOrNull() ?: 30
                                val sq = customSquats.toIntOrNull() ?: 30
                                val rn = customRun.toFloatOrNull() ?: 3.0f
                                viewModel.startActiveWorkout("Custom Workout", "Custom", pu, su, sq, rn)
                                showCustomConfig = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = customPushups.isNotEmpty() && customSitups.isNotEmpty() && customSquats.isNotEmpty() && customRun.isNotEmpty()
                        ) {
                            Text("Start Workout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "DAILY CHALLENGES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeroBlue,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Saitama Hero Regimen",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Choose your level. Saitama trains daily until baldness, but we recommend gradual progressions for beginner heroes!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    val difficultyTiers = listOf(
                        QuadrupleTier("Easy Progressions", "Perfect starting point to condition tendons and core.", "Easy", 10, 10, 10, 1.0f, "1.0 km Walk", HeroBlueLight),
                        QuadrupleTier("Medium Conditioning", "Stepping up volume. Burns high calories and builds shape.", "Medium", 50, 50, 50, 5.0f, "5.0 km Run", HeroBlue),
                        QuadrupleTier("Saitama Original", "The legendary routine: 100 reps of everything, 10km run.", "Hard", 100, 100, 100, 10.0f, "10.0 km Run", HeroGold),
                        QuadrupleTier("Extreme Overload", "Beyond limits! Double reps of squats, pushups, sit-ups.", "Extreme", 200, 200, 200, 15.0f, "15.0 km Run", ErrorRed)
                    )

                    items(difficultyTiers) { tier ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.5.dp, tier.glowColor.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tier.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = tier.diffName.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tier.glowColor,
                                        modifier = Modifier
                                            .background(tier.glowColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = tier.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                                )

                                Divider(color = CosmicBorder.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RepItem("${tier.pushups} Pushups")
                                    RepItem("${tier.situps} Situps")
                                    RepItem("${tier.squats} Squats")
                                    RepItem(tier.runDesc)
                                }

                                Button(
                                    onClick = {
                                        viewModel.startActiveWorkout(
                                            planName = "Saitama Challenge",
                                            difficulty = tier.diffName,
                                            pushups = tier.pushups,
                                            situps = tier.situps,
                                            squats = tier.squats,
                                            runKm = tier.runKm
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = tier.glowColor),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("BEGIN CHALLENGE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Custom trigger
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CosmicBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCustomConfig = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = HeroBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Custom Challenge Builder",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Create custom counts for sit-ups, runs, and pushups.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ACTIVE WORKOUT INTERFACE PANEL
// ==========================================
@Composable
fun ActiveTrainingSuite(
    active: ActiveWorkout,
    viewModel: FitnessViewModel
) {
    var selectedExerciseType by remember { mutableStateOf("pushup") }

    val puCurrent = active.pushupsCurrent
    val suCurrent = active.situpsCurrent
    val sqCurrent = active.squatsCurrent
    val runCurrent = active.runCurrentKm

    val duration = active.durationSeconds
    val min = duration / 60
    val sec = duration % 60
    val timeFormatted = String.format("%02d:%02d", min, sec)

    LaunchedEffect(active.isActive) {
        if (active.isActive) {
            while (true) {
                delay(1000L)
                viewModel.updateActiveWorkoutProgress(
                    pushups = active.pushupsCurrent,
                    situps = active.situpsCurrent,
                    squats = active.squatsCurrent,
                    runKm = active.runCurrentKm,
                    durationSeconds = active.durationSeconds + 1
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ACTIVE CHALLENGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeroBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = active.planName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, HeroBlue.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = HeroBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, CosmicBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedStickFigure(
                    type = selectedExerciseType,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Focus on perfect form for ${selectedExerciseType.uppercase()}s",
                        fontSize = 10.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseTabItem("Push-ups", selectedExerciseType == "pushup", Modifier.weight(1f)) { selectedExerciseType = "pushup" }
            ExerciseTabItem("Sit-ups", selectedExerciseType == "situp", Modifier.weight(1f)) { selectedExerciseType = "situp" }
            ExerciseTabItem("Squats", selectedExerciseType == "squat", Modifier.weight(1f)) { selectedExerciseType = "squat" }
            ExerciseTabItem("Running", selectedExerciseType == "run", Modifier.weight(1f)) { selectedExerciseType = "run" }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val (current, target, label) = when (selectedExerciseType) {
                    "pushup" -> Triple(puCurrent, active.pushupsTarget, "Push-ups Completed")
                    "situp" -> Triple(suCurrent, active.situpsTarget, "Sit-ups Completed")
                    "squat" -> Triple(sqCurrent, active.squatsTarget, "Squats Completed")
                    else -> Triple(runCurrent, active.runTargetKm, "Distance Traveled (km)")
                }

                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                val fraction = if (target is Int && target > 0) (current as Int).toFloat() / target
                else if (target is Float && target > 0f) (current as Float) / target
                else 0f

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (current is Float) String.format("%.2f", current) else "$current",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = HeroBlue
                    )
                    Text(
                        text = " / " + if (target is Float) String.format("%.1f", target) + " km" else "$target reps",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { fraction.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = HeroBlue,
                    trackColor = CosmicSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedExerciseType != "run") {
                        Button(
                            onClick = {
                                val added = (current as Int) + 5
                                updateSpecificCount(selectedExerciseType, added, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+5 Reps", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                val added = (current as Int) + 10
                                updateSpecificCount(selectedExerciseType, added, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+10 Reps", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                updateSpecificCount(selectedExerciseType, target as Int, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Complete", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                val added = (current as Float) + 0.5f
                                updateSpecificCount(selectedExerciseType, added, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+500m", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                val added = (current as Float) + 1.0f
                                updateSpecificCount(selectedExerciseType, added, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1.0 km", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                updateSpecificCount(selectedExerciseType, target as Float, viewModel, active)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Complete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.cancelActiveWorkout() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, ErrorRed.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Quit Workout", fontWeight = FontWeight.Bold)
            }

            if (active.isActive) {
                Button(
                    onClick = { viewModel.pauseActiveWorkout() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pause Timer", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.resumeActiveWorkout() },
                    colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Resume Timer", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { viewModel.completeActiveWorkout() },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1.2f)
            ) {
                Text("Finish Session", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// TAB 3: EXERCISE & STRETCH LIBRARY SCREEN
// ==========================================
@Composable
fun LibraryScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Exercises, 1 = Stretching, 2 = Plans

    var selectedExerciseDetail by remember { mutableStateOf<Exercise?>(null) }
    var activeStretchTimer by remember { mutableStateOf<Stretch?>(null) }
    var stretchSecondsRemaining by remember { mutableStateOf(0) }
    var isStretchTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isStretchTimerRunning, stretchSecondsRemaining) {
        if (isStretchTimerRunning && stretchSecondsRemaining > 0) {
            delay(1000L)
            stretchSecondsRemaining -= 1
            if (stretchSecondsRemaining == 0) {
                isStretchTimerRunning = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(16.dp)
    ) {
        Text(text = "FITNESS ATLAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeroBlue, letterSpacing = 1.2.sp)
        Text(text = "Training Guides", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton("Strength Lib", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
            TabButton("Stretch Section", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
            TabButton("Workout Plans", selectedTab == 2, Modifier.weight(1f)) { selectedTab = 2 }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    val exercises = ExerciseData.exercises
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(exercises) { ex ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, CosmicBorder),
                                onClick = { selectedExerciseDetail = ex }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(CosmicSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = HeroBlue, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = ex.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = "${ex.category} • Target: ${ex.musclesWorked}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = ex.difficulty,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = getDifficultyColor(ex.difficulty),
                                        modifier = Modifier
                                            .background(getDifficultyColor(ex.difficulty).copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    val stretches = ExerciseData.stretches
                    if (activeStretchTimer != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CosmicSurface, RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(text = "STRETCH IN PROGRESS", fontSize = 11.sp, color = HeroBlue, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text(text = activeStretchTimer!!.name, fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)

                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, HeroBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "$stretchSecondsRemaining", fontSize = 48.sp, fontWeight = FontWeight.Black, color = HeroBlue)
                                    Text(text = "seconds", fontSize = 12.sp, color = TextSecondary)
                                }
                            }

                            Text(
                                text = "Benefits: ${activeStretchTimer!!.benefits}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { isStretchTimerRunning = !isStretchTimerRunning },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isStretchTimerRunning) WarningOrange else SuccessGreen),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isStretchTimerRunning) "Pause" else "Resume", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { activeStretchTimer = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Stop", color = TextPrimary)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(stretches) { str ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, CosmicBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = str.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(
                                                text = "${str.durationSeconds}s Timer",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HeroBlueLight,
                                                modifier = Modifier
                                                    .background(HeroBlue.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(text = str.benefits, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 4.dp))

                                        Button(
                                            onClick = {
                                                activeStretchTimer = str
                                                stretchSecondsRemaining = str.durationSeconds
                                                isStretchTimerRunning = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("START STRETCH TIMERS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    val plans = ExerciseData.plans
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(plans) { pl ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, CosmicBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = pl.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        Text(
                                            text = "${pl.durationDays} Days Plan",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HeroGold,
                                            modifier = Modifier
                                                .background(HeroGold.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(text = pl.description, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 6.dp))
                                    Text(text = "Target Goal: ${pl.targetGoal}", fontSize = 11.sp, color = HeroBlueLight, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val pushupsCount = if (pl.difficulty == "Easy") 15 else if (pl.difficulty == "Medium") 45 else 100
                                            val situpsCount = if (pl.difficulty == "Easy") 15 else if (pl.difficulty == "Medium") 45 else 100
                                            val squatsCount = if (pl.difficulty == "Easy") 15 else if (pl.difficulty == "Medium") 45 else 100
                                            val runKm = if (pl.difficulty == "Easy") 1.0f else if (pl.difficulty == "Medium") 4.0f else 10.0f

                                            viewModel.startActiveWorkout(pl.name, pl.difficulty, pushupsCount, situpsCount, squatsCount, runKm)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Activate This Plan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedExerciseDetail != null) {
            val ex = selectedExerciseDetail!!
            AlertDialog(
                onDismissRequest = { selectedExerciseDetail = null },
                confirmButton = {
                    Button(
                        onClick = { selectedExerciseDetail = null },
                        colors = ButtonDefaults.buttonColors(containerColor = HeroBlue)
                    ) {
                        Text("Got it")
                    }
                },
                title = {
                    Text(text = ex.name, fontWeight = FontWeight.Black, color = TextPrimary)
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmicSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedStickFigure(type = ex.animationType, modifier = Modifier.fillMaxSize())
                        }

                        DetailSection("Muscles Worked", ex.musclesWorked, HeroBlueLight)
                        DetailSection("Suggested sets/reps", "${ex.sets} Sets x ${ex.reps} Reps", TextPrimary)
                        DetailSection("Est. Calories Burned", "~${ex.caloriesBurned} kcal per set", SuccessGreen)

                        Text(text = "Instructions", fontWeight = FontWeight.Bold, color = HeroBlue)
                        ex.instructions.forEachIndexed { i, step ->
                            Text(text = "${i + 1}. $step", fontSize = 12.sp, color = TextPrimary)
                        }

                        Text(text = "Common Mistakes", fontWeight = FontWeight.Bold, color = ErrorRed)
                        ex.mistakes.forEach { mistake ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "• ", color = ErrorRed, fontWeight = FontWeight.Bold)
                                Text(text = mistake, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                },
                containerColor = CosmicSurface,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun DetailSection(label: String, content: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        Text(text = content, fontSize = 13.sp, color = color, fontWeight = FontWeight.ExtraBold)
    }
}

private fun getDifficultyColor(diff: String): Color {
    return when (diff.lowercase()) {
        "easy" -> SuccessGreen
        "medium" -> HeroBlue
        "hard" -> HeroGold
        else -> ErrorRed
    }
}

@Composable
fun TabButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) HeroBlue else CosmicSurface
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (selected) HeroBlue else CosmicBorder),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) CosmicBg else TextPrimary
        )
    }
}

// ==========================================
// TAB 4: NUTRITION & HYDRATION TRACKER
// ==========================================
@Composable
fun NutritionScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsState()
    val totalWaterToday by viewModel.todayWaterTotal.collectAsState()
    val nutritionLogs by viewModel.todayNutritionLogs.collectAsState()

    val currentStats = stats ?: UserStats()
    val waterFraction = (totalWaterToday.toFloat() / currentStats.waterGoalMl).coerceIn(0f, 2f)

    val caloriesTarget = currentStats.calorieGoal
    val caloriesToday = currentStats.caloriesToday
    val caloriesFraction = (caloriesToday.toFloat() / caloriesTarget).coerceIn(0f, 1f)

    var showFoodDialog by remember { mutableStateOf(false) }
    var foodNameInput by remember { mutableStateOf("") }
    var foodCalInput by remember { mutableStateOf("") }
    var foodProteinInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Text(text = "FUEL & HYDRATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeroBlue, letterSpacing = 1.sp)
                Text(text = "Nutrition & Water Tracker", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WaterWaveProgress(
                        progress = waterFraction,
                        modifier = Modifier
                            .size(110.dp)
                            .weight(1.2f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(2f)) {
                        Text(text = "DAILY HYDRATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "$totalWaterToday", fontSize = 28.sp, fontWeight = FontWeight.Black, color = HeroBlue)
                            Text(text = " / ${currentStats.waterGoalMl} ml", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.addWater(250) },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+250ml", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.addWater(500) },
                                colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+500ml", fontSize = 11.sp, color = CosmicBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "DAILY CALORIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "$caloriesToday", fontSize = 28.sp, fontWeight = FontWeight.Black, color = HeroGold)
                                Text(text = " / $caloriesTarget kcal", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text(text = "Keep track of protein to rebuild muscles!", fontSize = 11.sp, color = TextMuted)
                        }

                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = CosmicSurfaceVariant,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(6.dp.toPx())
                                )
                                drawArc(
                                    color = HeroGold,
                                    startAngle = -90f,
                                    sweepAngle = caloriesFraction * 360f,
                                    useCenter = false,
                                    style = Stroke(6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(text = "${(caloriesFraction * 100).toInt()}%", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MacroMeter("Protein", currentStats.proteinToday, 150, HeroBlue, Modifier.weight(1f))
                        MacroMeter("Carbs", currentStats.carbsToday, 250, HeroGold, Modifier.weight(1f))
                        MacroMeter("Fats", currentStats.fatToday, 70, WarningOrange, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showFoodDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = HeroBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LOG DAILY MEAL", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "TODAY'S LOGGED FOODS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (nutritionLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicSurface, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No meals logged for today.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(nutritionLogs) { meal ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = meal.foodName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Protein: ${meal.proteinGrams}g • Carbs: ${meal.carbsGrams}g • Fat: ${meal.fatGrams}g", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text(text = "${meal.calories} kcal", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HeroGold)
                    }
                }
            }
        }

        item {
            Text(
                text = "SUGGESTED RECOVERY RECIPES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        val recipes = listOf(
            Pair("Saitama Egg & Rice Ramen", "380 kcal • 24g Protein • High carb breakfast fuel."),
            Pair("Hero Class Protein Shake", "290 kcal • 32g Protein • Quick recovery replenishment."),
            Pair("Cabbage & Beef Calisthenics Pot", "520 kcal • 42g Protein • Perfect night muscle builder.")
        )

        items(recipes) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = HeroBlueLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = item.first, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = item.second, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }
    }

    if (showFoodDialog) {
        AlertDialog(
            onDismissRequest = { showFoodDialog = false },
            title = { Text("Log Meal Intake", color = TextPrimary, fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = {
                        val name = foodNameInput.ifEmpty { "Chicken Rice" }
                        val cal = foodCalInput.toIntOrNull() ?: 450
                        val protein = foodProteinInput.toIntOrNull() ?: 30
                        val carbs = (cal * 0.12f).toInt()
                        val fats = (cal * 0.03f).toInt()

                        viewModel.addNutrition(name, cal, protein, carbs, fats)
                        foodNameInput = ""
                        foodCalInput = ""
                        foodProteinInput = ""
                        showFoodDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeroBlue)
                ) {
                    Text("Save Meal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFoodDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = foodNameInput,
                        onValueChange = { foodNameInput = it },
                        label = { Text("Food Name (e.g. Eggs & Beef)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = foodCalInput,
                        onValueChange = { foodCalInput = it },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = foodProteinInput,
                        onValueChange = { foodProteinInput = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                }
            },
            containerColor = CosmicSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun MacroMeter(label: String, current: Int, target: Int, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(text = "${current}g", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val ratio = (current.toFloat() / target).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = CosmicSurfaceVariant
        )
    }
}

// ==========================================
// TAB 5: GEMINI AI COACH
// ==========================================
@Composable
fun CoachScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val aiResponse by viewModel.aiCoachResponse.collectAsState()
    val loading by viewModel.aiCoachLoading.collectAsState()

    var chatInput by remember { mutableStateOf("") }

    val presets = listOf(
        "Suggest a weekly training plan",
        "Am I overtraining? I feel exhausted",
        "How can I level up my Pushups?",
        "Give me brutal motivation!"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(16.dp)
    ) {
        Text(text = "AI TRAINING ADVISOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HeroBlue, letterSpacing = 1.sp)
        Text(text = "Saitama AI Coach", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(
            text = "Ask anything about training progressions, recovery, joint safety, or motivation.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (aiResponse.isEmpty() && !loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Sports, contentDescription = null, tint = HeroBlue, modifier = Modifier.size(44.dp))
                            Text(text = "Hello Cadet! Ask me anything.", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Or tap a quick command below:", color = TextSecondary, fontSize = 11.sp)

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                presets.forEach { item ->
                                    Button(
                                        onClick = { viewModel.askCoach(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = item, color = TextPrimary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                                modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = HeroGold, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = aiResponse,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        if (loading) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = HeroBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saitama Coach is formulating response...", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.clearCoachChat() },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset Conversation", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Ask Coach (e.g., recommend rest hours)", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HeroBlue,
                    unfocusedBorderColor = CosmicBorder
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (chatInput.isNotEmpty()) {
                        viewModel.askCoach(chatInput)
                        chatInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HeroBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(54.dp),
                enabled = chatInput.isNotEmpty() && !loading
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

// ==========================================
// TAB 6: PROFILE, ACHIEVEMENTS & SETTINGS
// ==========================================
@Composable
fun ProfileScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val currentStats = stats ?: UserStats()

    var showProfileEditor by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(currentStats.weightKg.toString()) }
    var heightInput by remember { mutableStateOf(currentStats.heightCm.toString()) }
    var fatInput by remember { mutableStateOf(currentStats.bodyFatPercentage.toString()) }
    var calGoalInput by remember { mutableStateOf(currentStats.calorieGoal.toString()) }
    var waterGoalInput by remember { mutableStateOf(currentStats.waterGoalMl.toString()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, CosmicBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CosmicSurfaceVariant)
                            .border(2.dp, HeroBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = HeroGold, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "HERO ASSOCIATION ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeroBlueLight, letterSpacing = 1.2.sp)
                    Text(text = "Saitama Cadet", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(text = "Current Rank: ${currentStats.fitnessLevel} (Level ${currentStats.level})", fontSize = 13.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricItem("Weight", "${currentStats.weightKg} kg")
                        MetricItem("Height", "${currentStats.heightCm} cm")
                        MetricItem("Body Fat", "${currentStats.bodyFatPercentage}%")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            weightInput = currentStats.weightKg.toString()
                            heightInput = currentStats.heightCm.toString()
                            fatInput = currentStats.bodyFatPercentage.toString()
                            calGoalInput = currentStats.calorieGoal.toString()
                            waterGoalInput = currentStats.waterGoalMl.toString()
                            showProfileEditor = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Body Profile & Goals", color = TextPrimary)
                    }
                }
            }
        }

        item {
            Text(
                text = "HERO ASSOCIATION BADGES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(achievements) { ach ->
            val icon = when (ach.iconName) {
                "ic_directions_run" -> Icons.Default.DirectionsRun
                "ic_fitness_center" -> Icons.Default.FitnessCenter
                "ic_local_fire_department" -> Icons.Default.LocalFireDepartment
                "ic_workspace_premium" -> Icons.Default.WorkspacePremium
                "ic_water_drop" -> Icons.Default.WaterDrop
                "ic_restaurant" -> Icons.Default.Restaurant
                else -> Icons.Default.CalendarToday
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (ach.isUnlocked) CosmicSurface else CosmicSurface.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (ach.isUnlocked) HeroGold.copy(alpha = 0.5f) else CosmicBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (ach.isUnlocked) HeroGold.copy(alpha = 0.15f) else CosmicSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (ach.isUnlocked) HeroGold else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ach.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ach.isUnlocked) TextPrimary else TextSecondary
                        )
                        Text(text = ach.description, fontSize = 11.sp, color = TextMuted)
                    }

                    if (ach.isUnlocked) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Unlocked", tint = SuccessGreen)
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

    if (showProfileEditor) {
        AlertDialog(
            onDismissRequest = { showProfileEditor = false },
            title = { Text("Update Physical Parameters", color = TextPrimary, fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = {
                        val wt = weightInput.toFloatOrNull() ?: currentStats.weightKg
                        val ht = heightInput.toFloatOrNull() ?: currentStats.heightCm
                        val ft = fatInput.toFloatOrNull() ?: currentStats.bodyFatPercentage
                        val cal = calGoalInput.toIntOrNull() ?: currentStats.calorieGoal
                        val water = waterGoalInput.toIntOrNull() ?: currentStats.waterGoalMl

                        viewModel.updateProfile(wt, ht, ft, cal, water)
                        showProfileEditor = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeroBlue)
                ) {
                    Text("Apply Parameters")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileEditor = false }) {
                    Text("Dismiss", color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = fatInput,
                        onValueChange = { fatInput = it },
                        label = { Text("Body Fat %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = calGoalInput,
                        onValueChange = { calGoalInput = it },
                        label = { Text("Calorie Goal (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                    OutlinedTextField(
                        value = waterGoalInput,
                        onValueChange = { waterGoalInput = it },
                        label = { Text("Water Goal (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HeroBlue)
                    )
                }
            },
            containerColor = CosmicSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HeroBlueLight)
    }
}
