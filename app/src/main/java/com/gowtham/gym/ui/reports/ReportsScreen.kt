package com.gowtham.gym.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.gym.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

enum class ReportType(val title: String) {
    NUTRITION("Nutrition"),
    SESSIONS("Sessions"),
    EXERCISES("Exercises")
}

@Composable
fun SegmentedPicker(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeColors.cardAlt)
            .padding(3.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isSelected) ThemeColors.pill else Color.Transparent)
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else ThemeColors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val workoutLogs by viewModel.allWorkoutLogs.collectAsState()
    val allPRs by viewModel.allPersonalRecords.collectAsState()
    val allFoodEntries by viewModel.allFoodEntries.collectAsState()
    val nutritionGoal by viewModel.nutritionGoal.collectAsState()
    var selectedReportType by remember { mutableStateOf(ReportType.EXERCISES) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text("Trends", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        SegmentedPicker(
            options = ReportType.values().map { it.title },
            selectedIndex = ReportType.values().indexOf(selectedReportType),
            onOptionSelected = { selectedReportType = ReportType.values()[it] }
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedReportType) {
            ReportType.NUTRITION -> NutritionReport(
                foodEntries = allFoodEntries,
                calorieGoal = (nutritionGoal?.calories ?: 2700.0).toFloat()
            )
            ReportType.SESSIONS -> SessionsReport(workoutLogs)
            ReportType.EXERCISES -> ExercisesReport(workoutLogs, allPRs)
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun NutritionReport(
    foodEntries: List<FoodEntryEntity>,
    calorieGoal: Float
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayLabelFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }

    val chartData = remember(foodEntries) {
        (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
            }
            val dateStr = dateFormat.format(cal.time)
            val dayLabel = dayLabelFormat.format(cal.time)
            val dayCalories = foodEntries
                .filter { it.dateString == dateStr }
                .sumOf { it.calories }
                .toFloat()
            Pair(dayLabel, dayCalories)
        }
    }

    val loggedDays = chartData.filter { it.second > 0f }
    val dailyAverage = if (loggedDays.isNotEmpty()) {
        (loggedDays.sumOf { it.second.toDouble() } / loggedDays.size).roundToInt()
    } else {
        0
    }
    val maxCalories = maxOf(
        calorieGoal * 1.15f,
        chartData.maxOfOrNull { it.second } ?: 0f,
        1000f
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ThemeColors.card)
            .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(HealthRingColors.calories.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = HealthRingColors.calories, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("Calories  Last 7 Days", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("%,d".format(dailyAverage), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Text("daily average over the past week", color = ThemeColors.textSecondary, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            chartData.forEach { (day, calories) ->
                val heightRatio = if (calories > 0f) {
                    (calories / maxCalories).coerceIn(0.08f, 1f)
                } else {
                    0.04f
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(heightRatio)
                            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                            .background(
                                if (calories > 0f) {
                                    Brush.verticalGradient(
                                        listOf(HealthRingColors.calories, HealthRingColors.caloriesSecondary)
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.08f))
                                    )
                                }
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(day, color = ThemeColors.textSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SessionsReport(logs: List<WorkoutLogEntryEntity>) {
    Text("Workout History", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))

    if (logs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ThemeColors.card)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No workout sessions logged yet.\nTap on any muscle in the Workout tab to log your sets!", color = ThemeColors.textSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    } else {
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            logs.forEach { log ->
                val totalVolume = log.sets.sumOf { it.weight * it.reps }
                val muscle = log.primaryMuscle

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ThemeColors.card)
                        .border(1.dp, ThemeColors.border, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(muscle.tileGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color.Black.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text(log.exerciseName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${log.sets.size} sets  ${dateFormat.format(log.date)}", color = ThemeColors.textSecondary, fontSize = 12.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${totalVolume.toInt()} kg", color = muscle.primaryColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Volume", color = ThemeColors.textTertiary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ExercisesReport(logs: List<WorkoutLogEntryEntity>, prs: List<PersonalRecordEntity>) {

    Column {
        Text("Splits Overview", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        val activeSplits = MuscleGroup.values().map { muscle ->
            val muscleLogs = logs.filter { it.primaryMuscle == muscle }
            val volume = muscleLogs.sumOf { log -> log.sets.sumOf { it.weight * it.reps } }
            val sets = muscleLogs.sumOf { it.sets.size }
            Triple(muscle, volume, sets)
        }.sortedByDescending { it.second }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(activeSplits) { (muscle, volume, sets) ->
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(ThemeColors.card)
                        .border(1.dp, ThemeColors.border, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(muscle.tileGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color.Black.copy(alpha = 0.85f), modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(muscle.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("$sets sets logged", color = ThemeColors.textSecondary, fontSize = 11.sp)
                    Text(
                        if (volume > 0) "${volume.toInt()} kg" else "0 kg",
                        color = muscle.primaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text("Personal Records", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (prs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ThemeColors.card)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Log your workout sessions to see your estimated 1RM and PR records here.", color = ThemeColors.textSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                prs.forEach { pr ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ThemeColors.card)
                            .border(1.dp, ThemeColors.border, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pr.exerciseName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Estimated 1RM: ${pr.estimated1RM.toInt()} kg", color = ThemeColors.textSecondary, fontSize = 12.sp)
                        }

                        Text("${pr.bestWeight.toInt()} kg  ${pr.reps}", color = HealthRingColors.fat, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
