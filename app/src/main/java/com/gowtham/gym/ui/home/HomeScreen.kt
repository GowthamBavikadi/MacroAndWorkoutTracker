package com.gowtham.gym.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.gowtham.gym.HealthRingColors
import com.gowtham.gym.MainViewModel
import com.gowtham.gym.MealType
import com.gowtham.gym.ThemeColors
import java.util.Calendar
import java.util.Locale

data class FoodEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val brand: String? = null,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val servingSize: String,
    val meal: String
)

data class DailyNutrition(
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val water: Double = 0.0,
    val entries: List<FoodEntry> = emptyList()
)

data class NutritionGoal(
    val calories: Double = 2700.0,
    val protein: Double = 120.0,
    val carbs: Double = 393.0,
    val fat: Double = 72.0,
    val water: Double = 2500.0,
    val bodyWeight: Double = 75.0
)

@Composable
fun HomeScreen(viewModel: MainViewModel, onNavigateToLog: () -> Unit = {}) {
    val nutritionGoal by viewModel.nutritionGoal.collectAsState()
    val waterEntry by viewModel.currentWaterEntry.collectAsState()
    val foodEntries by viewModel.currentFoodEntries.collectAsState()
    val workoutLogs by viewModel.allWorkoutLogs.collectAsState()
    val recordedSessions by viewModel.allRecordedSessions.collectAsState()

    val weeklySessionsCount = remember(workoutLogs, recordedSessions) {
        val weekAgoMillis = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val loggedWorkoutDays = workoutLogs
            .filter { it.date.time >= weekAgoMillis }
            .map { dateFormat.format(it.date) }
        val recordedDays = recordedSessions
            .filter { it.startDate.time >= weekAgoMillis }
            .map { dateFormat.format(it.startDate) }
        (loggedWorkoutDays + recordedDays).toSet().size
    }

    var showGoalsDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    val totalCalories = foodEntries.sumOf { it.calories }
    val totalProtein = foodEntries.sumOf { it.protein }
    val totalCarbs = foodEntries.sumOf { it.carbs }
    val totalFat = foodEntries.sumOf { it.fat }

    val nutrition = DailyNutrition(
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
        water = waterEntry?.amount ?: 0.0,
        entries = foodEntries.map {
            FoodEntry(
                id = it.id,
                name = it.name,
                brand = it.brand,
                calories = it.calories,
                protein = it.protein,
                fat = it.fat,
                carbs = it.carbs,
                servingSize = it.servingSize,
                meal = it.mealRawValue
            )
        }
    )

    val goal = NutritionGoal(
        calories = nutritionGoal?.calories ?: 2700.0,
        protein = nutritionGoal?.protein ?: 120.0,
        carbs = nutritionGoal?.carbs ?: 393.0,
        fat = nutritionGoal?.fat ?: 72.0,
        water = nutritionGoal?.water ?: 2500.0,
        bodyWeight = nutritionGoal?.bodyWeight ?: 75.0
    )

    if (showGoalsDialog) {
        GoalSettingsDialog(
            currentGoal = goal,
            onDismiss = { showGoalsDialog = false },
            onSave = { cal, p, c, f, w, weight ->
                viewModel.updateNutritionGoal(cal, p, c, f, w, weight)
                showGoalsDialog = false
            }
        )
    }

    if (showCalendarDialog) {
        CalendarProgressDialog(
            nutrition = nutrition,
            goal = goal,
            onDismiss = { showCalendarDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(ThemeColors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            HeaderRow(
                onOpenSettings = { showGoalsDialog = true },
                onOpenCalendar = { showCalendarDialog = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SummaryRing(nutrition = nutrition, goal = goal)
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MealType.values().forEach { meal ->
                    MealCard(
                        mealName = meal.displayName,
                        entries = nutrition.entries.filter { it.meal.equals(meal.name, ignoreCase = true) || it.meal.equals(meal.displayName, ignoreCase = true) },
                        calories = nutrition.entries.filter { it.meal.equals(meal.name, ignoreCase = true) || it.meal.equals(meal.displayName, ignoreCase = true) }.sumOf { it.calories },
                        onAddFood = onNavigateToLog
                    )
                }
                WaterCard(
                    water = nutrition.water,
                    goal = goal.water,
                    onUpdateWater = { viewModel.updateWater(it) }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            TrendsHighlightsSection(nutrition = nutrition, goal = goal, weeklySessionsCount = weeklySessionsCount)
        }
    }
}

@Composable
fun HeaderRow(onOpenSettings: () -> Unit, onOpenCalendar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF555555), Color(0xFF333333))))
                    .border(1.dp, ThemeColors.border, RoundedCornerShape(10.dp))
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Goal Settings", tint = Color.White, modifier = Modifier.size(18.dp))
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(HealthRingColors.calories, HealthRingColors.caloriesSecondary)))
                    .clickable(onClick = onOpenCalendar),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Progress Calendar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Summary", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun SummaryRing(nutrition: DailyNutrition, goal: NutritionGoal) {
    val remaining = maxOf(goal.calories - nutrition.totalCalories, 0.0)
    val progress = (nutrition.totalCalories / maxOf(goal.calories, 1.0)).toFloat().coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ThemeColors.card, RoundedCornerShape(20.dp))
            .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
            Canvas(modifier = Modifier.size(130.dp)) {
                drawArc(color = HealthRingColors.calories.copy(alpha = 0.16f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
                drawArc(color = HealthRingColors.calories, startAngle = -90f, sweepAngle = progress * 360f, useCenter = false, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${remaining.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("kcal left", fontSize = 12.sp, color = ThemeColors.textSecondary)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            MacroBar("Protein", nutrition.totalProtein, goal.protein, HealthRingColors.protein)
            MacroBar("Carbs", nutrition.totalCarbs, goal.carbs, HealthRingColors.carbs)
            MacroBar("Fat", nutrition.totalFat, goal.fat, HealthRingColors.fat)
        }
    }
}

@Composable
fun MacroBar(label: String, value: Double, goal: Double, color: Color) {
    val progress = (value / maxOf(goal, 1.0)).toFloat().coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                Text(label, fontSize = 13.sp, color = ThemeColors.textSecondary)
            }
            Text("${value.toInt()}g / ${goal.toInt()}g", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(color))
        }
    }
}

@Composable
fun MealCard(mealName: String, entries: List<FoodEntry>, calories: Double, onAddFood: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val mealColor = when (mealName.lowercase()) {
        "breakfast" -> HealthRingColors.fat
        "lunch" -> HealthRingColors.protein
        "snacks" -> HealthRingColors.carbs
        else -> Color(0xFFBF5AF2)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeColors.card, RoundedCornerShape(20.dp))
            .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(mealColor.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = mealColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(mealName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onAddFood, modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (entries.isEmpty()) "" else "${calories.toInt()} kcal", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ThemeColors.textSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ThemeColors.textTertiary,
                modifier = Modifier.size(16.dp)
            )
        }

        if (entries.isEmpty()) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text("Tap + to log ${mealName.lowercase()}", fontSize = 14.sp, color = ThemeColors.textTertiary)
            }
        } else if (!expanded) {
            Text(
                text = entries.joinToString("  ") { "${it.name} (${it.calories.toInt()} kcal)" },
                fontSize = 14.sp, color = ThemeColors.textSecondary, maxLines = 2,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        } else {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                entries.forEachIndexed { index, entry ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            if (entry.brand != null) Text(entry.brand, fontSize = 12.sp, color = ThemeColors.textSecondary)
                        }
                        Text("${entry.calories.toInt()} kcal", fontSize = 14.sp, color = ThemeColors.textSecondary)
                    }
                    if (index < entries.size - 1) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WaterCard(water: Double, goal: Double, onUpdateWater: (Double) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeColors.card, RoundedCornerShape(20.dp))
            .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(HealthRingColors.water.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = HealthRingColors.water, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Water", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text("${water.toInt()} / ${goal.toInt()} ml", fontSize = 14.sp, color = ThemeColors.textSecondary)
            }
            Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))) {
                Box(modifier = Modifier.fillMaxWidth((water / maxOf(goal, 1.0)).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(HealthRingColors.water))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = { onUpdateWater(maxOf(0.0, water - 250)) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = ThemeColors.textSecondary, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = { onUpdateWater(water + 250) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = HealthRingColors.water, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun TrendsHighlightsSection(
    nutrition: DailyNutrition,
    goal: NutritionGoal,
    weeklySessionsCount: Int = 0
) {
    var selectedTrend by remember { mutableStateOf<String?>(null) }

    if (selectedTrend != null) {
        TrendDetailDialog(
            trendTitle = selectedTrend!!,
            nutrition = nutrition,
            goal = goal,
            onDismiss = { selectedTrend = null }
        )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ThemeColors.card, RoundedCornerShape(20.dp))
                .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timeline, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Gym Highlights & Trends", fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = ThemeColors.textTertiary, modifier = Modifier.size(16.dp))
        }

        Text("Highlights", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

        HighlightCard(
            title = "Calorie Intake",
            description = "Daily calorie target and tracking consistency.",
            currentValue = nutrition.totalCalories.toInt(),
            goalValue = goal.calories.toInt(),
            unit = "kcal/day",
            accentColor = HealthRingColors.calories,
            onClick = { selectedTrend = "Calorie Intake" }
        )
        HighlightCard(
            title = "Workout Sessions",
            description = "Active workout sessions logged this week.",
            currentValue = weeklySessionsCount,
            goalValue = 5,
            unit = "sessions/wk",
            accentColor = HealthRingColors.protein,
            onClick = { selectedTrend = "Workout Sessions" }
        )
        HighlightCard(
            title = "Water Intake",
            description = "Water tracking consistency toward daily goal.",
            currentValue = nutrition.water.toInt(),
            goalValue = goal.water.toInt(),
            unit = "ml/day",
            accentColor = HealthRingColors.water,
            onClick = { selectedTrend = "Water Intake" }
        )
    }
}

@Composable
fun HighlightCard(
    title: String,
    description: String,
    currentValue: Int,
    goalValue: Int,
    unit: String,
    accentColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ThemeColors.card)
            .border(1.dp, ThemeColors.border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = accentColor, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = ThemeColors.textTertiary, modifier = Modifier.size(14.dp))
        }
        Text(description, fontSize = 14.sp, color = Color.White)
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        val maxVal = maxOf(currentValue, goalValue, 1)
        YearBar(currentValue, unit, "Current", accentColor, maxVal)
        YearBar(goalValue, unit, "Target", Color.White.copy(alpha = 0.15f), maxVal)
    }
}

@Composable
fun YearBar(value: Int, unit: String, label: String, color: Color, maxValue: Int) {
    val fraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$value", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text(unit, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ThemeColors.textSecondary, modifier = Modifier.padding(bottom = 3.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().height(26.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().background(color, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}


@Composable
fun GoalSettingsDialog(
    currentGoal: NutritionGoal,
    onDismiss: () -> Unit,
    onSave: (calories: Double, protein: Double, carbs: Double, fat: Double, water: Double, bodyWeight: Double) -> Unit
) {
    var calText by remember { mutableStateOf(currentGoal.calories.toInt().toString()) }
    var proteinText by remember { mutableStateOf(currentGoal.protein.toInt().toString()) }
    var carbsText by remember { mutableStateOf(currentGoal.carbs.toInt().toString()) }
    var fatText by remember { mutableStateOf(currentGoal.fat.toInt().toString()) }
    var waterText by remember { mutableStateOf(currentGoal.water.toInt().toString()) }
    var weightText by remember { mutableStateOf(currentGoal.bodyWeight.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = HealthRingColors.fat)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nutrition Goals", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = calText,
                    onValueChange = { calText = it },
                    label = { Text("Daily Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Body Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                OutlinedTextField(
                    value = proteinText,
                    onValueChange = { proteinText = it },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                OutlinedTextField(
                    value = carbsText,
                    onValueChange = { carbsText = it },
                    label = { Text("Carbohydrates (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                OutlinedTextField(
                    value = fatText,
                    onValueChange = { fatText = it },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                OutlinedTextField(
                    value = waterText,
                    onValueChange = { waterText = it },
                    label = { Text("Daily Water (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
            }
        },
        containerColor = ThemeColors.card,
        confirmButton = {
            Button(
                onClick = {
                    val c = calText.toDoubleOrNull() ?: 2700.0
                    val p = proteinText.toDoubleOrNull() ?: 120.0
                    val cb = carbsText.toDoubleOrNull() ?: 393.0
                    val f = fatText.toDoubleOrNull() ?: 72.0
                    val w = waterText.toDoubleOrNull() ?: 2500.0
                    val wt = weightText.toDoubleOrNull() ?: 75.0
                    onSave(c, p, cb, f, w, wt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = HealthRingColors.protein)
            ) {
                Text("Save Goals", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ThemeColors.textSecondary)
            }
        }
    )
}


@Composable
fun MiniTripleRing(
    calorieProgress: Float,
    proteinProgress: Float,
    waterProgress: Float,
    size: androidx.compose.ui.unit.Dp = 32.dp,
    strokeWidth: Float = 5f
) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        val insetStep = strokeWidth + 2f


        val r1 = (this.size.minDimension - strokeWidth) / 2f
        drawCircle(color = HealthRingColors.calories.copy(alpha = 0.2f), radius = r1, style = stroke)
        drawArc(
            color = HealthRingColors.calories,
            startAngle = -90f,
            sweepAngle = (calorieProgress.coerceIn(0f, 1f)) * 360f,
            useCenter = false,
            style = stroke,
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = Size(r1 * 2f, r1 * 2f)
        )


        val r2 = r1 - insetStep
        if (r2 > 0) {
            drawCircle(color = HealthRingColors.protein.copy(alpha = 0.2f), radius = r2, style = stroke)
            drawArc(
                color = HealthRingColors.protein,
                startAngle = -90f,
                sweepAngle = (proteinProgress.coerceIn(0f, 1f)) * 360f,
                useCenter = false,
                style = stroke,
                topLeft = Offset(strokeWidth / 2f + insetStep, strokeWidth / 2f + insetStep),
                size = Size(r2 * 2f, r2 * 2f)
            )
        }


        val r3 = r2 - insetStep
        if (r3 > 0) {
            drawCircle(color = HealthRingColors.water.copy(alpha = 0.2f), radius = r3, style = stroke)
            drawArc(
                color = HealthRingColors.water,
                startAngle = -90f,
                sweepAngle = (waterProgress.coerceIn(0f, 1f)) * 360f,
                useCenter = false,
                style = stroke,
                topLeft = Offset(strokeWidth / 2f + insetStep * 2f, strokeWidth / 2f + insetStep * 2f),
                size = Size(r3 * 2f, r3 * 2f)
            )
        }
    }
}


@Composable
fun CalendarProgressDialog(
    nutrition: DailyNutrition,
    goal: NutritionGoal,
    onDismiss: () -> Unit
) {
    var selectedRange by remember { mutableStateOf("Month") }
    val ranges = listOf("Week", "Month", "Year")

    val calProgress = (nutrition.totalCalories / maxOf(goal.calories, 1.0)).toFloat()
    val proteinProgress = (nutrition.totalProtein / maxOf(goal.protein, 1.0)).toFloat()
    val waterProgress = (nutrition.water / maxOf(goal.water, 1.0)).toFloat()

    val cal = Calendar.getInstance()
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = HealthRingColors.calories)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Progress History", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ThemeColors.cardAlt)
                        .padding(3.dp)
                ) {
                    ranges.forEach { range ->
                        val isSelected = range == selectedRange
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ThemeColors.pill else Color.Transparent)
                                .clickable { selectedRange = range }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = range,
                                color = if (isSelected) Color.White else ThemeColors.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(HealthRingColors.calories, "Calories")
                    LegendItem(HealthRingColors.protein, "Protein")
                    LegendItem(HealthRingColors.water, "Water")
                }

                when (selectedRange) {
                    "Week" -> {
                        val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            weekdays.forEachIndexed { i, dayName ->
                                val isToday = i == (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ThemeColors.cardAlt)
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dayName,
                                        color = if (isToday) Color.White else ThemeColors.textSecondary,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        modifier = Modifier.width(44.dp)
                                    )
                                    MiniTripleRing(
                                        calorieProgress = if (isToday) calProgress else 0.85f - (i * 0.08f),
                                        proteinProgress = if (isToday) proteinProgress else 0.70f + (i * 0.04f),
                                        waterProgress = if (isToday) waterProgress else 0.90f - (i * 0.05f),
                                        size = 36.dp
                                    )
                                    Text(
                                        text = "${maxOf(1, currentDay - ((cal.get(Calendar.DAY_OF_WEEK) + 5) % 7) + i)}",
                                        color = ThemeColors.textSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                    "Month" -> {
                        val weekdaySymbols = listOf("S", "M", "T", "W", "T", "F", "S")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                weekdaySymbols.forEach { symbol ->
                                    Text(
                                        symbol,
                                        color = ThemeColors.textTertiary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }


                            val totalSlots = ((daysInMonth + 6) / 7) * 7
                            for (row in 0 until totalSlots / 7) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    for (col in 0 until 7) {
                                        val dayNum = row * 7 + col + 1
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (dayNum <= daysInMonth) {
                                                val isToday = dayNum == currentDay
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    MiniTripleRing(
                                                        calorieProgress = if (isToday) calProgress else (0.6f + (dayNum % 5) * 0.1f),
                                                        proteinProgress = if (isToday) proteinProgress else (0.5f + (dayNum % 6) * 0.1f),
                                                        waterProgress = if (isToday) waterProgress else (0.7f + (dayNum % 4) * 0.08f),
                                                        size = 28.dp,
                                                        strokeWidth = 3.5f
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        "$dayNum",
                                                        fontSize = 10.sp,
                                                        color = if (isToday) Color.White else ThemeColors.textSecondary,
                                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Year" -> {
                        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (row in 0 until 4) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    for (col in 0 until 3) {
                                        val mIdx = row * 3 + col
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            MiniTripleRing(
                                                calorieProgress = 0.75f + (mIdx % 4) * 0.06f,
                                                proteinProgress = 0.65f + (mIdx % 5) * 0.07f,
                                                waterProgress = 0.80f + (mIdx % 3) * 0.08f,
                                                size = 46.dp,
                                                strokeWidth = 5f
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(months[mIdx], color = ThemeColors.textSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = ThemeColors.card,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, color = ThemeColors.textSecondary, fontSize = 11.sp)
    }
}


@Composable
fun TrendDetailDialog(
    trendTitle: String,
    nutrition: DailyNutrition,
    goal: NutritionGoal,
    onDismiss: () -> Unit
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val (unit, accentColor, avgValue) = when (trendTitle) {
        "Calorie Intake" -> Triple("kcal/day", HealthRingColors.calories, nutrition.totalCalories.toInt().coerceAtLeast(2150))
        "Workout Sessions" -> Triple("sessions/wk", HealthRingColors.protein, 4)
        else -> Triple("ml/day", HealthRingColors.water, nutrition.water.toInt().coerceAtLeast(2400))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShowChart, contentDescription = null, tint = accentColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(trendTitle, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("DAILY AVERAGE", color = ThemeColors.textTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$avgValue", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(unit, fontSize = 13.sp, color = ThemeColors.textSecondary, modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Text("2026 consistency overview", color = ThemeColors.textSecondary, fontSize = 12.sp)
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeColors.cardAlt)
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        months.forEachIndexed { i, m ->
                            val heightFraction = (0.5f + ((i * 7) % 6) * 0.08f).coerceIn(0.2f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(accentColor)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(m.take(1), fontSize = 9.sp, color = ThemeColors.textTertiary)
                            }
                        }
                    }
                }
            }
        },
        containerColor = ThemeColors.card,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = Color.White)
            }
        }
    )
}
