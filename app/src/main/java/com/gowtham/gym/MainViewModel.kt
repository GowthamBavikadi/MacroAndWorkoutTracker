package com.gowtham.gym

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GymDatabase.getDatabase(application).gymDao()
    private val prefs = application.getSharedPreferences("gym_app_prefs", Context.MODE_PRIVATE)

    private val _currentDate = MutableStateFlow(Date())
    val currentDate: StateFlow<Date> = _currentDate.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dateStringFlow = _currentDate.map { dateFormat.format(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, dateFormat.format(Date()))

    val nutritionGoal = dao.getNutritionGoal()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentFoodEntries = dateStringFlow.flatMapLatest { dateStr ->
        dao.getFoodEntriesForDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentWaterEntry = dateStringFlow.flatMapLatest { dateStr ->
        dao.getWaterEntryForDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val allFoodEntries = dao.getAllFoodEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allWorkoutLogs = dao.getAllWorkoutLogs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allPersonalRecords = dao.getAllPersonalRecords()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val exerciseCatalog = dao.getAllExerciseDefinitions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allRecordedSessions = dao.getAllRecordedSessions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _sessionStartTime = MutableStateFlow<Long?>(null)
    val sessionStartTime: StateFlow<Long?> = _sessionStartTime.asStateFlow()

    private val _rpeEnabled = MutableStateFlow(prefs.getBoolean("rpeEnabled", true))
    val rpeEnabled: StateFlow<Boolean> = _rpeEnabled.asStateFlow()

    fun setRpeEnabled(enabled: Boolean) {
        _rpeEnabled.value = enabled
        prefs.edit().putBoolean("rpeEnabled", enabled).apply()
    }


    val trainedMusclesToday = allWorkoutLogs.map { logs ->
        logs.filter { isSameDay(it.date, Date()) }
            .map { it.primaryMuscle }
            .toSet()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    init {

        viewModelScope.launch {
            if (dao.getNutritionGoal().firstOrNull() == null) {
                dao.insertNutritionGoal(NutritionGoalEntity())
            }
        }


        viewModelScope.launch {
            val existing = dao.getAllExerciseDefinitions().first()
            if (existing.isEmpty()) {
                dao.insertAllExerciseDefinitions(seedExercises())
            }
        }


        val savedStart = prefs.getLong("activeWorkoutSessionStartTime", 0L)
        if (savedStart > 0L) {
            _sessionStartTime.value = savedStart
            _isSessionActive.value = true
        }
    }


    fun startSession() {
        val now = System.currentTimeMillis()
        _sessionStartTime.value = now
        _isSessionActive.value = true
        prefs.edit().putLong("activeWorkoutSessionStartTime", now).apply()
    }

    fun stopSession(notes: String = "") {
        val startMillis = _sessionStartTime.value ?: return
        val endMillis = System.currentTimeMillis()
        val durationSeconds = (endMillis - startMillis) / 1000

        val session = RecordedWorkoutSessionEntity(
            startDate = Date(startMillis),
            endDate = Date(endMillis),
            durationSeconds = durationSeconds,
            notes = notes
        )

        viewModelScope.launch {
            dao.insertRecordedSession(session)
        }

        _isSessionActive.value = false
        _sessionStartTime.value = null
        prefs.edit().remove("activeWorkoutSessionStartTime").apply()
    }

    fun cancelSession() {
        _isSessionActive.value = false
        _sessionStartTime.value = null
        prefs.edit().remove("activeWorkoutSessionStartTime").apply()
    }

    fun formattedElapsedTime(nowMillis: Long = System.currentTimeMillis()): String {
        val start = _sessionStartTime.value ?: return "0:00"
        val elapsed = Math.max(0L, (nowMillis - start) / 1000)
        val hours = elapsed / 3600
        val minutes = (elapsed % 3600) / 60
        val seconds = elapsed % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }


    fun exercisesForMuscle(muscle: MuscleGroup, query: String = ""): List<ExerciseDefinitionEntity> {
        val catalog = exerciseCatalog.value.ifEmpty { seedExercises() }
        val matching = catalog.filter { it.matches(muscle) }.toMutableList()

        val logs = allWorkoutLogs.value
        matching.sortWith(Comparator { ex1, ex2 ->
            val p1 = if (ex1.primaryMuscle == muscle) 1 else 0
            val p2 = if (ex2.primaryMuscle == muscle) 1 else 0
            if (p1 != p2) return@Comparator p2 - p1

            val c1 = logs.count { it.exerciseName.equals(ex1.name, ignoreCase = true) }
            val c2 = logs.count { it.exerciseName.equals(ex2.name, ignoreCase = true) }
            if (c1 != c2) return@Comparator c2 - c1

            ex1.name.compareTo(ex2.name, ignoreCase = true)
        })

        if (query.isNotBlank()) {
            return matching.filter { it.name.contains(query, ignoreCase = true) }
        }
        return matching
    }

    fun addCustomExercise(name: String, bodyPart: String) {
        viewModelScope.launch {
            dao.insertExerciseDefinition(ExerciseDefinitionEntity(name = name, bodyPart = bodyPart))
        }
    }

    fun deleteExercise(name: String) {
        viewModelScope.launch {
            dao.deleteExerciseDefinition(name)
        }
    }

    fun setsLoggedToday(muscle: MuscleGroup): Int {
        val logs = allWorkoutLogs.value
        val todayLogs = logs.filter { isSameDay(it.date, Date()) && it.primaryMuscle == muscle }
        return todayLogs.sumOf { it.sets.size }
    }


    fun logWorkoutSession(
        exerciseName: String,
        bodyPart: String,
        sets: List<SetEntry>,
        rpe: Double? = null,
        notes: String = ""
    ) {
        val entry = WorkoutLogEntryEntity(
            exerciseName = exerciseName,
            bodyPart = bodyPart,
            date = Date(),
            sets = sets,
            rpe = rpe,
            notes = notes
        )

        viewModelScope.launch {
            dao.insertWorkoutLog(entry)


            val topSet = entry.topSet
            if (topSet != null) {
                val existingPR = dao.getPersonalRecordForExercise(exerciseName)
                if (existingPR != null) {
                    if (topSet.weight > existingPR.bestWeight ||
                        (topSet.weight == existingPR.bestWeight && topSet.reps > existingPR.reps)
                    ) {
                        dao.insertPersonalRecord(
                            PersonalRecordEntity(
                                exerciseName = exerciseName,
                                bestWeight = topSet.weight,
                                reps = topSet.reps,
                                date = entry.date
                            )
                        )
                    }
                } else {
                    dao.insertPersonalRecord(
                        PersonalRecordEntity(
                            exerciseName = exerciseName,
                            bestWeight = topSet.weight,
                            reps = topSet.reps,
                            date = entry.date
                        )
                    )
                }
            }
        }
    }

    fun deleteWorkoutLog(id: String) {
        viewModelScope.launch {
            dao.deleteWorkoutLogById(id)
        }
    }


    fun addWater(amount: Double) {
        val dateStr = dateFormat.format(_currentDate.value)
        val current = currentWaterEntry.value?.amount ?: 0.0
        viewModelScope.launch {
            dao.insertWaterEntry(WaterEntryEntity(dateString = dateStr, amount = current + amount))
        }
    }

    fun updateWater(amount: Double) {
        val dateStr = dateFormat.format(_currentDate.value)
        viewModelScope.launch {
            dao.insertWaterEntry(WaterEntryEntity(dateString = dateStr, amount = amount))
        }
    }

    fun addFoodEntry(
        name: String,
        calories: Double,
        protein: Double,
        fat: Double,
        carbs: Double,
        mealType: String,
        servingSize: String = "1 serving"
    ) {
        val dateStr = dateFormat.format(_currentDate.value)
        viewModelScope.launch {
            dao.insertFoodEntry(
                FoodEntryEntity(
                    dateString = dateStr,
                    name = name,
                    brand = null,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    servingSize = servingSize,
                    servingAmount = 1.0,
                    servingUnit = "serving",
                    mealRawValue = mealType
                )
            )
        }
    }

    fun deleteFoodEntry(entry: FoodEntryEntity) {
        viewModelScope.launch {
            dao.deleteFoodEntry(entry)
        }
    }

    fun updateNutritionGoal(
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
        water: Double,
        bodyWeight: Double = 0.0
    ) {
        viewModelScope.launch {
            val existing = dao.getNutritionGoal().firstOrNull()
            val id = existing?.id ?: UUID.randomUUID().toString()
            dao.insertNutritionGoal(
                NutritionGoalEntity(
                    id = id,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    water = water,
                    bodyWeight = bodyWeight
                )
            )
        }
    }

    fun searchFoodCatalog(query: String): List<CatalogFoodItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return builtInFoodDatabase
        return builtInFoodDatabase.filter {
            it.name.lowercase().contains(q) || (it.brand?.lowercase()?.contains(q) == true)
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val c1 = Calendar.getInstance().apply { time = date1 }
        val c2 = Calendar.getInstance().apply { time = date2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    companion object {
        fun seedExercises(): List<ExerciseDefinitionEntity> {
            return listOf(

                ExerciseDefinitionEntity("Bench Press", "Chest"),
                ExerciseDefinitionEntity("Incline Bench Press", "Chest"),
                ExerciseDefinitionEntity("Incline Dumbbell Press", "Chest"),
                ExerciseDefinitionEntity("Flat Dumbbell Press", "Chest"),
                ExerciseDefinitionEntity("Chest Fly", "Chest"),
                ExerciseDefinitionEntity("Incline Chest Fly", "Chest"),
                ExerciseDefinitionEntity("Pec Deck", "Chest"),


                ExerciseDefinitionEntity("Overhead Press", "Shoulders"),
                ExerciseDefinitionEntity("Arnold Press", "Shoulders"),
                ExerciseDefinitionEntity("Dumbbell Shoulder Press", "Shoulders"),
                ExerciseDefinitionEntity("Lateral Raise", "Shoulders"),
                ExerciseDefinitionEntity("Front Raise", "Shoulders"),
                ExerciseDefinitionEntity("Cable Lateral Raise", "Shoulders"),
                ExerciseDefinitionEntity("Reverse Pec Deck (Rear Delt)", "Shoulders"),


                ExerciseDefinitionEntity("Tricep Pushdown", "Triceps"),
                ExerciseDefinitionEntity("Rope Tricep Pushdown", "Triceps"),
                ExerciseDefinitionEntity("Overhead Tricep Extension", "Triceps"),
                ExerciseDefinitionEntity("Skull Crushers", "Triceps"),


                ExerciseDefinitionEntity("Lat Pulldown", "Lats"),
                ExerciseDefinitionEntity("Wide-Grip Lat Pulldown", "Lats"),
                ExerciseDefinitionEntity("Close-Grip Lat Pulldown", "Lats"),
                ExerciseDefinitionEntity("Barbell Row", "Lats"),
                ExerciseDefinitionEntity("Dumbbell Row", "Lats"),
                ExerciseDefinitionEntity("Seated Cable Row", "Lats"),
                ExerciseDefinitionEntity("T-Bar Row", "Lats"),
                ExerciseDefinitionEntity("Pull-Up", "Lats"),
                ExerciseDefinitionEntity("Chin-Up", "Lats"),
                ExerciseDefinitionEntity("Straight Arm Pulldown", "Lats"),


                ExerciseDefinitionEntity("Face Pull", "Traps"),
                ExerciseDefinitionEntity("Barbell Shrug", "Traps"),
                ExerciseDefinitionEntity("Dumbbell Shrug", "Traps"),


                ExerciseDefinitionEntity("Back Extension", "Lower Back"),
                ExerciseDefinitionEntity("Good Morning", "Lower Back"),
                ExerciseDefinitionEntity("Hyperextension", "Lower Back"),
                ExerciseDefinitionEntity("Deadlift", "Lower Back"),


                ExerciseDefinitionEntity("Bicep Curl", "Biceps"),
                ExerciseDefinitionEntity("Hammer Curl", "Biceps"),
                ExerciseDefinitionEntity("Preacher Curl", "Biceps"),
                ExerciseDefinitionEntity("Cable Curl", "Biceps"),
                ExerciseDefinitionEntity("Concentration Curl", "Biceps"),
                ExerciseDefinitionEntity("EZ-Bar Curl", "Biceps"),
                ExerciseDefinitionEntity("Incline Dumbbell Curl", "Biceps"),
                ExerciseDefinitionEntity("Wrist Curl", "Forearms"),
                ExerciseDefinitionEntity("Reverse Wrist Curl", "Forearms"),


                ExerciseDefinitionEntity("Squat", "Quads"),
                ExerciseDefinitionEntity("Leg Press", "Quads"),
                ExerciseDefinitionEntity("Hack Squat", "Quads"),
                ExerciseDefinitionEntity("Bulgarian Split Squat", "Quads"),
                ExerciseDefinitionEntity("Lunges", "Quads"),
                ExerciseDefinitionEntity("Walking Lunges", "Quads"),
                ExerciseDefinitionEntity("Leg Extension", "Quads"),


                ExerciseDefinitionEntity("Romanian Deadlift (RDL)", "Hamstrings"),
                ExerciseDefinitionEntity("Sumo Deadlift", "Hamstrings"),
                ExerciseDefinitionEntity("Leg Curl", "Hamstrings"),
                ExerciseDefinitionEntity("Seated Leg Curl", "Hamstrings"),


                ExerciseDefinitionEntity("Hip Thrust", "Glutes"),
                ExerciseDefinitionEntity("Glute Kickback", "Glutes"),
                ExerciseDefinitionEntity("Glute Bridge", "Glutes"),
                ExerciseDefinitionEntity("Cable Hip Abduction", "Glutes"),


                ExerciseDefinitionEntity("Calf Raise", "Calves"),
                ExerciseDefinitionEntity("Seated Calf Raise", "Calves"),


                ExerciseDefinitionEntity("Crunches", "Abs"),
                ExerciseDefinitionEntity("Cable Crunch", "Abs"),
                ExerciseDefinitionEntity("Hanging Leg Raise", "Abs"),
                ExerciseDefinitionEntity("Ab Wheel Rollout", "Abs"),
                ExerciseDefinitionEntity("Plank", "Abs"),
                ExerciseDefinitionEntity("Russian Twist", "Abs"),
                ExerciseDefinitionEntity("Sit-Up", "Abs"),
                ExerciseDefinitionEntity("Mountain Climbers", "Abs")
            )
        }
    }
}

