package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FitnessViewModel = viewModel()
                var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = CosmicSurface,
                            tonalElevation = 8.dp
                        ) {
                            AppTab.values().forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = if (selected) HeroBlue else TextSecondary
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) HeroBlue else TextSecondary
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = HeroBlue.copy(alpha = 0.12f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            AppTab.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { currentTab = it }
                            )
                            AppTab.CHALLENGE -> ChallengeScreen(viewModel = viewModel)
                            AppTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                            AppTab.NUTRITION -> NutritionScreen(viewModel = viewModel)
                            AppTab.COACH -> CoachScreen(viewModel = viewModel)
                            AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
