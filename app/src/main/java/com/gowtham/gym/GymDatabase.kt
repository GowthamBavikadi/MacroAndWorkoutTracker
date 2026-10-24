package com.gowtham.gym

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromSetEntryList(value: List<SetEntry>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<SetEntry>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSetEntryList(value: String): List<SetEntry> {
        val gson = Gson()
        val type = object : TypeToken<List<SetEntry>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

@Database(
    entities = [
        FoodEntryEntity::class,
        WaterEntryEntity::class,
        FavoriteFoodEntity::class,
        NutritionGoalEntity::class,
        ExerciseDefinitionEntity::class,
        WorkoutLogEntryEntity::class,
        PersonalRecordEntity::class,
        RecordedWorkoutSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "gym_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
