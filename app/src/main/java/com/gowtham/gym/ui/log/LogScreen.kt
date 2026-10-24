package com.gowtham.gym.ui.log

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.gym.*
import com.gowtham.gym.ui.home.DailyNutrition
import com.gowtham.gym.ui.home.FoodEntry
import com.gowtham.gym.ui.home.NutritionGoal

@Composable
fun LogScreen(viewModel: MainViewModel) {
    var query by remember { mutableStateOf("") }
    var activeMeal by remember { mutableStateOf(MealType.BREAKFAST) }
    var showAddCustomDialog by remember { mutableStateOf(false) }
    var selectedCatalogFoodToLog by remember { mutableStateOf<CatalogFoodItem?>(null) }
    var entryToDelete by remember { mutableStateOf<FoodEntryEntity?>(null) }

    val nutritionGoal by viewModel.nutritionGoal.collectAsState()
    val waterEntry by viewModel.currentWaterEntry.collectAsState()
    val foodEntries by viewModel.currentFoodEntries.collectAsState()

    val nutrition = DailyNutrition(
        totalCalories = foodEntries.sumOf { it.calories },
        totalProtein = foodEntries.sumOf { it.protein },
        totalCarbs = foodEntries.sumOf { it.carbs },
        totalFat = foodEntries.sumOf { it.fat },
        water = waterEntry?.amount ?: 0.0,
        entries = emptyList()
    )

    val goal = NutritionGoal(
        calories = nutritionGoal?.calories ?: 2700.0,
        protein = nutritionGoal?.protein ?: 120.0,
        carbs = nutritionGoal?.carbs ?: 393.0,
        fat = nutritionGoal?.fat ?: 72.0,
        water = nutritionGoal?.water ?: 2500.0
    )


    if (selectedCatalogFoodToLog != null) {
        val food = selectedCatalogFoodToLog!!
        var scaleAmountText by remember { mutableStateOf("100") }
        val ratio = (scaleAmountText.toDoubleOrNull() ?: 100.0) / 100.0
        val cal = food.calories * ratio
        val p = food.protein * ratio
        val c = food.carbs * ratio
        val f = food.fat * ratio

        AlertDialog(
            onDismissRequest = { selectedCatalogFoodToLog = null },
            title = {
                Column {
                    Text(food.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    if (food.brand != null) Text(food.brand, color = ThemeColors.textSecondary, fontSize = 12.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Serving: ${food.servingSize}", color = ThemeColors.textSecondary, fontSize = 13.sp)

                    OutlinedTextField(
                        value = scaleAmountText,
                        onValueChange = { scaleAmountText = it },
                        label = { Text("Amount (grams or % of serving)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ThemeColors.cardAlt)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${cal.toInt()} kcal", color = HealthRingColors.calories, fontWeight = FontWeight.Bold)
                        Text("P ${p.toInt()}g", color = HealthRingColors.protein, fontWeight = FontWeight.Bold)
                        Text("C ${c.toInt()}g", color = HealthRingColors.carbs, fontWeight = FontWeight.Bold)
                        Text("F ${f.toInt()}g", color = HealthRingColors.fat, fontWeight = FontWeight.Bold)
                    }

                    Text("Meal: ${activeMeal.displayName}", color = ThemeColors.textSecondary, fontSize = 12.sp)
                }
            },
            containerColor = ThemeColors.card,
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addFoodEntry(
                            name = food.name,
                            calories = cal,
                            protein = p,
                            fat = f,
                            carbs = c,
                            mealType = activeMeal.displayName,
                            servingSize = "${scaleAmountText}g"
                        )
                        selectedCatalogFoodToLog = null
                        query = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRingColors.protein)
                ) {
                    Text("Log to ${activeMeal.displayName}", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCatalogFoodToLog = null }) {
                    Text("Cancel", color = ThemeColors.textSecondary)
                }
            }
        )
    }


    if (showAddCustomDialog) {
        var foodName by remember { mutableStateOf(query) }
        var caloriesStr by remember { mutableStateOf("300") }
        var proteinStr by remember { mutableStateOf("25") }
        var carbsStr by remember { mutableStateOf("30") }
        var fatStr by remember { mutableStateOf("10") }
        var selectedMeal by remember { mutableStateOf(activeMeal.displayName) }

        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("Add Custom Food", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        placeholder = { Text("e.g. Protein Shake") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White)
                        )
                        OutlinedTextField(
                            value = proteinStr,
                            onValueChange = { proteinStr = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = carbsStr,
                            onValueChange = { carbsStr = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White)
                        )
                        OutlinedTextField(
                            value = fatStr,
                            onValueChange = { fatStr = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White)
                        )
                    }
                }
            },
            containerColor = ThemeColors.card,
            confirmButton = {
                Button(
                    onClick = {
                        val name = foodName.ifBlank { "Custom Food" }
                        val cal = caloriesStr.toDoubleOrNull() ?: 0.0
                        val p = proteinStr.toDoubleOrNull() ?: 0.0
                        val c = carbsStr.toDoubleOrNull() ?: 0.0
                        val f = fatStr.toDoubleOrNull() ?: 0.0

                        viewModel.addFoodEntry(
                            name = name,
                            calories = cal,
                            protein = p,
                            fat = f,
                            carbs = c,
                            mealType = selectedMeal
                        )
                        showAddCustomDialog = false
                        query = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRingColors.protein)
                ) {
                    Text("Add Food", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("Cancel", color = ThemeColors.textSecondary)
                }
            }
        )
    }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Entry?", color = Color.White) },
            text = { Text("Remove \"${entryToDelete?.name}\" from log?", color = ThemeColors.textSecondary) },
            containerColor = ThemeColors.card,
            confirmButton = {
                Button(
                    onClick = {
                        entryToDelete?.let { viewModel.deleteFoodEntry(it) }
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRingColors.calories)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Cancel", color = ThemeColors.textSecondary)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(ThemeColors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "NUTRITION",
                        color = HealthRingColors.calories,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text("Food Log", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ThemeColors.cardAlt)
                        .border(1.dp, ThemeColors.border, RoundedCornerShape(50))
                        .clickable { showAddCustomDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Custom", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }


            MacroSummaryBar(nutrition, goal)

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealType.values().forEach { meal ->
                    val isSelected = activeMeal == meal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ThemeColors.pill else ThemeColors.card)
                            .border(1.dp, if (isSelected) HealthRingColors.fat else ThemeColors.border, RoundedCornerShape(12.dp))
                            .clickable { activeMeal = meal }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            meal.displayName,
                            color = if (isSelected) Color.White else ThemeColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            SearchBarWidget(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )


            if (query.isNotBlank()) {
                val catalogResults = remember(query) { viewModel.searchFoodCatalog(query) }
                val loggedResults = remember(query, foodEntries) {
                    foodEntries.filter { it.name.contains(query, ignoreCase = true) }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ThemeColors.cardAlt)
                            .border(1.dp, HealthRingColors.protein.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .clickable { showAddCustomDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = HealthRingColors.protein)
                        Text("Add \"$query\" as custom food", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (catalogResults.isNotEmpty()) {
                        Text("Catalog Foods", color = ThemeColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        catalogResults.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ThemeColors.card)
                                    .border(1.dp, ThemeColors.border, RoundedCornerShape(14.dp))
                                    .clickable { selectedCatalogFoodToLog = item }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text("${item.servingSize}  P ${item.protein.toInt()}g C ${item.carbs.toInt()}g F ${item.fat.toInt()}g", color = ThemeColors.textSecondary, fontSize = 12.sp)
                                }
                                Text("${item.calories.toInt()} kcal", color = HealthRingColors.calories, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    if (loggedResults.isNotEmpty()) {
                        Text("Logged Today", color = ThemeColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        loggedResults.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ThemeColors.card)
                                    .border(1.dp, ThemeColors.border, RoundedCornerShape(14.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text("${entry.mealRawValue}  ${entry.calories.toInt()} kcal", color = ThemeColors.textSecondary, fontSize = 12.sp)
                                }
                                IconButton(onClick = { entryToDelete = entry }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            } else {

                Column(modifier = Modifier.padding(top = 10.dp)) {
                    val mealHours = listOf(
                        Pair(MealType.BREAKFAST, 8),
                        Pair(MealType.LUNCH, 13),
                        Pair(MealType.SNACKS, 16),
                        Pair(MealType.DINNER, 20)
                    )

                    mealHours.forEach { (meal, hour) ->
                        val mealEntries = foodEntries.filter {
                            it.mealRawValue.equals(meal.name, ignoreCase = true) || it.mealRawValue.equals(meal.displayName, ignoreCase = true)
                        }.map {
                            FoodEntry(
                                id = it.id,
                                name = it.name,
                                calories = it.calories,
                                protein = it.protein,
                                fat = it.fat,
                                carbs = it.carbs,
                                servingSize = it.servingSize,
                                meal = it.mealRawValue
                            )
                        }

                        TimeSlotRow(
                            mealName = meal.displayName,
                            label = timeLabel(hour),
                            entries = mealEntries,
                            onAdd = {
                                activeMeal = meal
                                showAddCustomDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroSummaryBar(nutrition: DailyNutrition, goal: NutritionGoal) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MacroPill(icon = Icons.Default.LocalFireDepartment, label = "${nutrition.totalCalories.toInt()}/${goal.calories.toInt()} kcal", color = HealthRingColors.calories)
        MacroPill(icon = null, label = "P ${nutrition.totalProtein.toInt()}g", color = HealthRingColors.protein)
        MacroPill(icon = null, label = "C ${nutrition.totalCarbs.toInt()}g", color = HealthRingColors.carbs)
        MacroPill(icon = null, label = "F ${nutrition.totalFat.toInt()}g", color = HealthRingColors.fat)
    }
}

@Composable
fun MacroPill(icon: androidx.compose.ui.graphics.vector.ImageVector?, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
        }
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
fun SearchBarWidget(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search foods or brands...", color = ThemeColors.textSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ThemeColors.textSecondary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = ThemeColors.textSecondary)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ThemeColors.cardAlt,
                unfocusedContainerColor = ThemeColors.cardAlt,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )
    }
}

fun timeLabel(hour: Int): String = if (hour < 12) "$hour AM" else if (hour == 12) "12 PM" else "${hour - 12} PM"

@Composable
fun TimeSlotRow(mealName: String, label: String, entries: List<FoodEntry>, onAdd: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.06f), thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = if (entries.isEmpty()) 16.dp else 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.width(72.dp)) {
                Text(mealName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ThemeColors.textTertiary)
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (entries.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    entries.forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(entry.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            Text("${entry.calories.toInt()} kcal", fontSize = 13.sp, color = ThemeColors.textSecondary)
                        }
                    }
                }
            } else {
                Text(
                    "No items logged",
                    color = ThemeColors.textTertiary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
