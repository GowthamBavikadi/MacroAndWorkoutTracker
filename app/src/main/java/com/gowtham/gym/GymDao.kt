package com.gowtham.gym

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {

    @Query("SELECT * FROM food_entries WHERE dateString = :date")
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntryEntity>>

    @Query("SELECT * FROM food_entries")
    fun getAllFoodEntries(): Flow<List<FoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntryEntity)

    @Delete
    suspend fun deleteFoodEntry(entry: FoodEntryEntity)


    @Query("SELECT * FROM water_entries WHERE dateString = :date")
    fun getWaterEntryForDate(date: String): Flow<WaterEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterEntry(entry: WaterEntryEntity)


    @Query("SELECT * FROM nutrition_goals LIMIT 1")
    fun getNutritionGoal(): Flow<NutritionGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionGoal(goal: NutritionGoalEntity)


    @Query("SELECT * FROM exercise_definitions")
    fun getAllExerciseDefinitions(): Flow<List<ExerciseDefinitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseDefinition(definition: ExerciseDefinitionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExerciseDefinitions(definitions: List<ExerciseDefinitionEntity>)

    @Query("DELETE FROM exercise_definitions WHERE name = :name")
    suspend fun deleteExerciseDefinition(name: String)


    @Query("SELECT * FROM workout_log_entries ORDER BY date DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(entry: WorkoutLogEntryEntity)

    @Delete
    suspend fun deleteWorkoutLog(entry: WorkoutLogEntryEntity)

    @Query("DELETE FROM workout_log_entries WHERE id = :id")
    suspend fun deleteWorkoutLogById(id: String)


    @Query("SELECT * FROM personal_records")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseName = :exerciseName LIMIT 1")
    suspend fun getPersonalRecordForExercise(exerciseName: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(record: PersonalRecordEntity)


    @Query("SELECT * FROM recorded_workout_sessions ORDER BY startDate DESC")
    fun getAllRecordedSessions(): Flow<List<RecordedWorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordedSession(session: RecordedWorkoutSessionEntity)
}
