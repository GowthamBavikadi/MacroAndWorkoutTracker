package com.gowtham.gym

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID


enum class MealType(val displayName: String, val icon: String) {
    BREAKFAST("Breakfast", "cup_and_saucer"),
    LUNCH("Lunch", "takeout_bag"),
    SNACKS("Snacks", "carrot"),
    DINNER("Dinner", "fork_knife");

    companion object {
        fun forHour(hour: Int): MealType {
            return when (hour) {
                in 5..10 -> BREAKFAST
                in 11..14 -> LUNCH
                in 15..17 -> SNACKS
                in 18..23 -> DINNER
                else -> SNACKS
            }
        }
    }
}


object HealthRingColors {

    val calories = Color(0xFFFA114F)
    val caloriesSecondary = Color(0xFFFF3B30)


    val protein = Color(0xFFA4FF00)
    val proteinSecondary = Color(0xFF30D158)


    val carbs = Color(0xFF00F5EA)
    val carbsSecondary = Color(0xFF64D2FF)


    val fat = Color(0xFFFF9F0A)
    val fatSecondary = Color(0xFFFFD60A)


    val water = Color(0xFF32ADE6)
    val waterSecondary = Color(0xFF0A84FF)
}

object ThemeColors {
    val background = Color(0xFF000000)
    val canvasSubtle = Color(0xFF0F0F12)
    val card = Color(0xFF1C1C1E)
    val cardAlt = Color(0xFF2C2C2E)
    val pill = Color(0xFF3A3A3C)
    val border = Color.White.copy(alpha = 0.08f)
    val borderStrong = Color.White.copy(alpha = 0.14f)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFEBEBF5).copy(alpha = 0.62f)
    val textTertiary = Color(0xFFEBEBF5).copy(alpha = 0.34f)
    val accent = Color(0xFFFA114F)
}


enum class MuscleGroup(val title: String) {
    CHEST("Chest"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    ABS("Abs"),
    QUADS("Quads"),
    FOREARMS("Forearms"),
    TRAPS("Traps"),
    LATS("Lats"),
    TRICEPS("Triceps"),
    LOWER_BACK("Lower Back"),
    GLUTES("Glutes"),
    HAMSTRINGS("Hamstrings"),
    CALVES("Calves");

    val isAnterior: Boolean
        get() = when (this) {
            CHEST, SHOULDERS, BICEPS, ABS, QUADS, FOREARMS, TRAPS, CALVES -> true
            LATS, TRICEPS, LOWER_BACK, GLUTES, HAMSTRINGS -> false
        }

    val bodyPartCategory: String
        get() = when (this) {
            CHEST -> "Chest"
            SHOULDERS -> "Shoulders"
            BICEPS -> "Biceps"
            ABS -> "Abs"
            FOREARMS -> "Forearms"
            TRICEPS -> "Triceps"
            TRAPS, LATS, LOWER_BACK -> "Back"
            QUADS, GLUTES, HAMSTRINGS, CALVES -> "Legs"
        }

    val tileGradient: List<Color>
        get() = when (this) {
            CHEST -> listOf(Color(0xFFFF453A), Color(0xFFFA114F))
            SHOULDERS -> listOf(Color(0xFFFF9F0A), Color(0xFFFF7300))
            BICEPS -> listOf(Color(0xFF64D2FF), Color(0xFF0A84FF))
            ABS -> listOf(Color(0xFF30D158), Color(0xFF00C7BE))
            FOREARMS -> listOf(Color(0xFFBF5AF2), Color(0xFF8F45EC))
            QUADS -> listOf(Color(0xFFFFD60A), Color(0xFFFF9500))
            CALVES -> listOf(Color(0xFF00C7BE), Color(0xFF30B0C7))
            TRAPS -> listOf(Color(0xFF5E5CE6), Color(0xFF7D7AFF))
            LATS -> listOf(Color(0xFF34C759), Color(0xFF14AE94))
            TRICEPS -> listOf(Color(0xFFFF6482), Color(0xFFFF375F))
            LOWER_BACK -> listOf(Color(0xFF0A84FF), Color(0xFF32ADE6))
            GLUTES -> listOf(Color(0xFFFF375F), Color(0xFFD933AD))
            HAMSTRINGS -> listOf(Color(0xFF40C8E0), Color(0xFF00A3CC))
        }

    val primaryColor: Color
        get() = tileGradient.first()

    val secondaryColor: Color
        get() = tileGradient.last()

    companion object {
        fun resolve(from: String, exerciseName: String = ""): MuscleGroup {
            values().firstOrNull { it.title.equals(from, ignoreCase = true) }?.let { return it }
            val lowerEx = exerciseName.lowercase()
            val lowerPart = from.lowercase()

            if (lowerEx.contains("calf") || lowerEx.contains("calves")) return CALVES
            if (lowerEx.contains("glute") || lowerEx.contains("hip thrust") || lowerEx.contains("abduction")) return GLUTES
            if (lowerEx.contains("hamstring") || lowerEx.contains("leg curl") || lowerEx.contains("romanian") || lowerEx.contains("rdl") || lowerEx.contains("sumo")) return HAMSTRINGS
            if (lowerEx.contains("shrug") || lowerEx.contains("face pull")) return TRAPS
            if (lowerEx.contains("back extension") || lowerEx.contains("good morning") || lowerEx.contains("hyperextension") || lowerEx == "deadlift") return LOWER_BACK
            if (lowerEx.contains("wrist") || lowerEx.contains("forearm")) return FOREARMS
            if (lowerEx.contains("tricep") || lowerEx.contains("skull")) return TRICEPS
            if (lowerEx.contains("bicep") || lowerEx.contains("curl")) return BICEPS
            if (lowerEx.contains("shoulder") || lowerEx.contains("overhead press") || lowerEx.contains("arnold") || lowerEx.contains("lateral raise") || lowerEx.contains("front raise") || lowerEx.contains("rear delt")) return SHOULDERS
            if (lowerEx.contains("bench") || lowerEx.contains("chest") || lowerEx.contains("pec") || lowerEx.contains("dumbbell press")) return CHEST
            if (lowerEx.contains("crunch") || lowerEx.contains("plank") || lowerEx.contains("leg raise") || lowerEx.contains("ab ") || lowerEx.contains("twist") || lowerEx.contains("sit-up") || lowerEx.contains("climber")) return ABS
            if (lowerEx.contains("squat") || lowerEx.contains("leg press") || lowerEx.contains("lunge") || lowerEx.contains("leg extension")) return QUADS
            if (lowerEx.contains("lat") || lowerEx.contains("row") || lowerEx.contains("pull-up") || lowerEx.contains("chin-up") || lowerEx.contains("pulldown")) return LATS

            return when (lowerPart) {
                "chest", "push" -> CHEST
                "shoulders" -> SHOULDERS
                "biceps" -> BICEPS
                "triceps" -> TRICEPS
                "forearms" -> FOREARMS
                "abs", "core" -> ABS
                "traps" -> TRAPS
                "lower back" -> LOWER_BACK
                "back", "pull" -> LATS
                "glutes" -> GLUTES
                "hamstrings" -> HAMSTRINGS
                "calves" -> CALVES
                "legs", "quads" -> QUADS
                else -> CHEST
            }
        }
    }
}



@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val dateString: String,
    val name: String,
    val brand: String? = null,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: String = "1 serving",
    val servingAmount: Double? = 1.0,
    val servingUnit: String? = "serving",
    val mealRawValue: String,
    val saturatedFat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val cholesterol: Double = 0.0,
    val isCustom: Boolean = false
)

@Entity(tableName = "water_entries")
data class WaterEntryEntity(
    @PrimaryKey val dateString: String,
    val amount: Double
)

@Entity(tableName = "favorite_foods")
data class FavoriteFoodEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val brand: String? = null,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: String,
    val servingAmount: Double? = null,
    val servingUnit: String? = null,
    val saturatedFat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val cholesterol: Double = 0.0
)

@Entity(tableName = "nutrition_goals")
data class NutritionGoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val calories: Double = 2700.0,
    val protein: Double = 120.0,
    val fat: Double = 72.0,
    val carbs: Double = 393.0,
    val water: Double = 2500.0,
    val bodyWeight: Double = 0.0
)

@Entity(tableName = "exercise_definitions")
data class ExerciseDefinitionEntity(
    @PrimaryKey val name: String,
    val bodyPart: String
) {
    val primaryMuscle: MuscleGroup
        get() = MuscleGroup.resolve(bodyPart, name)

    fun matches(muscle: MuscleGroup): Boolean {
        if (primaryMuscle == muscle) return true
        val lower = name.lowercase()
        return when (muscle) {
            MuscleGroup.TRICEPS -> lower.contains("bench press") || lower.contains("overhead press")
            MuscleGroup.GLUTES -> lower.contains("squat") || lower.contains("lunge") || lower.contains("deadlift") || lower.contains("rdl")
            MuscleGroup.HAMSTRINGS -> lower.contains("deadlift") || lower.contains("good morning")
            MuscleGroup.TRAPS -> lower.contains("deadlift") || lower.contains("face pull") || lower.contains("lateral raise")
            MuscleGroup.LOWER_BACK -> lower.contains("barbell row") || lower.contains("t-bar row") || lower.contains("squat")
            MuscleGroup.FOREARMS -> lower.contains("hammer curl") || lower.contains("reverse") || lower.contains("deadlift")
            else -> false
        }
    }
}

data class SetEntry(
    val id: String = UUID.randomUUID().toString(),
    val weight: Double,
    val reps: Int
)

@Entity(tableName = "workout_log_entries")
data class WorkoutLogEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseName: String,
    val bodyPart: String,
    val date: Date,
    val sets: List<SetEntry>,
    val rpe: Double? = null,
    val notes: String = ""
) {
    val topSet: SetEntry?
        get() = sets.maxByOrNull { it.weight }

    val primaryMuscle: MuscleGroup
        get() = MuscleGroup.resolve(bodyPart, exerciseName)
}

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val exerciseName: String,
    val bestWeight: Double,
    val reps: Int,
    val date: Date
) {

    val estimated1RM: Double
        get() = bestWeight * (1.0 + reps.toDouble() / 30.0)
}

@Entity(tableName = "recorded_workout_sessions")
data class RecordedWorkoutSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val startDate: Date,
    val endDate: Date,
    val durationSeconds: Long,
    val notes: String = ""
) {
    val formattedDuration: String
        get() {
            val total = durationSeconds
            val hours = total / 3600
            val minutes = (total % 3600) / 60
            val seconds = total % 60
            return when {
                hours > 0 -> "${hours} hr ${minutes} min"
                minutes > 0 -> "${minutes} min ${seconds} sec"
                else -> "${seconds} sec"
            }
        }
}

data class CatalogFoodItem(
    val name: String,
    val brand: String? = null,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: String = "100 g",
    val defaultMeal: MealType = MealType.SNACKS
)

val builtInFoodDatabase: List<CatalogFoodItem> = listOf(
    CatalogFoodItem("Eggs (Large)", "Grade A", 72.0, 6.2, 0.5, 5.0, "1 egg (50g)", MealType.BREAKFAST),
    CatalogFoodItem("Egg Whites", null, 52.0, 11.0, 0.7, 0.2, "100 g", MealType.BREAKFAST),
    CatalogFoodItem("Oatmeal (Rolled Oats)", "Quaker", 389.0, 16.9, 66.3, 6.9, "100 g", MealType.BREAKFAST),
    CatalogFoodItem("High-Protein Cereal", null, 364.0, 26.0, 57.3, 5.9, "100 g", MealType.BREAKFAST),
    CatalogFoodItem("Corn Flakes", "Kellogg's", 378.0, 6.7, 84.9, 1.0, "100 g", MealType.BREAKFAST),
    CatalogFoodItem("Whole Milk", null, 61.0, 3.2, 4.8, 3.3, "100 ml", MealType.BREAKFAST),
    CatalogFoodItem("Chicken Breast (Cooked)", null, 165.0, 31.0, 0.0, 3.6, "100 g", MealType.LUNCH),
    CatalogFoodItem("Chicken Breast (Raw)", null, 106.0, 22.5, 0.0, 1.9, "100 g", MealType.LUNCH),
    CatalogFoodItem("White Rice (Cooked)", null, 130.0, 2.7, 28.2, 0.3, "100 g", MealType.LUNCH),
    CatalogFoodItem("Brown Rice (Cooked)", null, 111.0, 2.6, 23.0, 0.9, "100 g", MealType.LUNCH),
    CatalogFoodItem("Salmon (Cooked)", null, 208.0, 20.4, 0.0, 13.4, "100 g", MealType.DINNER),
    CatalogFoodItem("Ground Beef (90/10)", null, 176.0, 26.0, 0.0, 10.0, "100 g", MealType.DINNER),
    CatalogFoodItem("Tuna (Canned in Water)", null, 116.0, 25.5, 0.0, 0.8, "100 g", MealType.LUNCH),
    CatalogFoodItem("Sweet Potato (Baked)", null, 90.0, 2.0, 20.7, 0.2, "100 g", MealType.DINNER),
    CatalogFoodItem("Whey Protein", "Optimum Nutrition", 120.0, 24.0, 3.0, 1.5, "1 scoop (30g)", MealType.SNACKS),
    CatalogFoodItem("Greek Yogurt (0% Fat)", null, 59.0, 10.0, 3.6, 0.4, "100 g", MealType.SNACKS),
    CatalogFoodItem("Peanut Butter (Crunchy)", null, 588.0, 25.1, 20.0, 50.4, "2 tbsp (32g)", MealType.SNACKS),
    CatalogFoodItem("Dates", null, 277.0, 1.8, 75.0, 0.2, "100 g", MealType.SNACKS),
    CatalogFoodItem("Almonds", null, 579.0, 21.2, 21.6, 49.9, "1 oz (28g)", MealType.SNACKS),
    CatalogFoodItem("Banana", null, 89.0, 1.1, 22.8, 0.3, "1 medium (118g)", MealType.SNACKS),
    CatalogFoodItem("Apple", null, 52.0, 0.3, 13.8, 0.2, "1 medium (182g)", MealType.SNACKS),
    CatalogFoodItem("Olive Oil", "Extra Virgin", 884.0, 0.0, 0.0, 100.0, "1 tbsp (14g)", MealType.DINNER)
)

