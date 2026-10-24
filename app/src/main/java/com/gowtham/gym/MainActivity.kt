package com.gowtham.gym

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gowtham.gym.ui.home.HomeScreen
import com.gowtham.gym.ui.log.LogScreen
import com.gowtham.gym.ui.login.LoginScreen
import com.gowtham.gym.ui.reports.ReportsScreen
import com.gowtham.gym.ui.workout.BodyMapScreen
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("gym_app_prefs", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme(
                colorScheme = androidx.compose.material3.darkColorScheme(
                    background = ThemeColors.background,
                    surface = ThemeColors.card
                )
            ) {
                val isLoggedIn = remember {
                    mutableStateOf(prefs.getBoolean("is_logged_in", false))
                }

                if (!isLoggedIn.value) {
                    LoginScreen(
                        onLoginSuccess = { email ->
                            prefs.edit()
                                .putBoolean("is_logged_in", true)
                                .putString("logged_in_email", email)
                                .apply()
                            isLoggedIn.value = true
                        }
                    )
                } else {
                    MainScreen()
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val activeColor: Color
) {
    object Home : BottomNavItem("home", "Summary", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite, HealthRingColors.calories)
    object Log : BottomNavItem("log", "Food", Icons.Outlined.Restaurant, Icons.Filled.Restaurant, HealthRingColors.fat)
    object Workout : BottomNavItem("workout", "Workout", Icons.Outlined.FitnessCenter, Icons.Filled.FitnessCenter, HealthRingColors.protein)
    object Reports : BottomNavItem("reports", "Trends", Icons.Outlined.BarChart, Icons.Filled.BarChart, HealthRingColors.carbs)
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            FloatingTabBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLog = {
                        navController.navigate(BottomNavItem.Log.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(BottomNavItem.Log.route) {
                LogScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.Workout.route) {
                BodyMapScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FloatingTabBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Log,
        BottomNavItem.Workout,
        BottomNavItem.Reports
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(CircleShape)
                .background(Color(0xE61C1C1E))
                .border(1.dp, ThemeColors.borderStrong, CircleShape)
        ) {
            val tabWidth = maxWidth / items.size
            val tabWidthPx = constraints.maxWidth.toFloat() / items.size

            val animatedOffset by animateFloatAsState(
                targetValue = selectedIndex * tabWidthPx,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "tabOffset"
            )


            Box(
                modifier = Modifier
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .width(tabWidth)
                    .height(58.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.activeIcon else item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(20.dp),
                                tint = if (isSelected) item.activeColor else ThemeColors.textTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else ThemeColors.textTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
