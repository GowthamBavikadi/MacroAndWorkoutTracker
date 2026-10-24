# Workout and Macros Tracker

An offline, privacy-focused Android application for unified nutrition logging, muscle-targeted workout tracking, and volume analytics.

[![Download APK](https://img.shields.io/badge/Download-Gym.apk%20(3.2%20MB)-success?style=for-the-badge&logo=android&logoColor=white)](https://github.com/GowthamBavikadi/MacroAndWorkoutTracker/raw/main/Gym.apk)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84.svg?style=flat-square&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

---

## Quick Install (Just Download 1 File — No Setup Required)

You do **not** need Android Studio, a computer, or any technical setup to use this app:

1. **Download the App**: Tap **[Download `Gym.apk` (3.2 MB)](https://github.com/GowthamBavikadi/MacroAndWorkoutTracker/raw/main/Gym.apk)** directly on your Android phone.
2. **Install**: Open the downloaded `Gym.apk` file and tap **Install** (if prompted by Android, allow installing from your browser/files).
3. **Sign In**: Open **Gym** and enter your email address along with the access password provided by the author.

---

## Highlights

- **100% Offline & Lightweight** — Zero internet permissions or background services. All data stays locally on your device inside a single **3.2 MB** install.
- **Macro Tracking** — Circular progress rings for real-time calorie and macronutrient status (protein, carbs, fat) with daily hydration goals.
- **Anatomical Body Map** — Select and target 13 muscle groups across anterior and posterior anatomical models with custom exercise associations.
- **1RM Engine** — Logs sets, reps, weights, and RPE with automatic estimated 1-Rep Max calculation via the Epley equation.
- **Jetpack Compose UI** — Material 3 design tokens, OLED black theme, spring-physics tab switching, and custom canvas rendering.
- **Room Persistence** — Transactional DAOs, automated type converters, and reactive Kotlin Flow architecture.

---

## Features

### Nutrition and Macro Tracking
- Auto-selects meal type (Breakfast, Lunch, Snacks, Dinner) based on device time.
- Tracks daily calories, protein, carbohydrates, and fats against configurable goals.
- Micronutrient fields for saturated fat, fiber, sugar, sodium, and cholesterol.
- Built-in food catalog with support for user favorites and custom entries.
- Tap-to-log water intake with animated progress indicators.

### Workout and Exercise Logging
- 13 muscle groups: Chest, Shoulders, Biceps, Abs, Quads, Forearms, Traps, Lats, Triceps, Lower Back, Glutes, Hamstrings, and Calves.
- Set-by-set logging with load, reps, and optional RPE ratings.
- Automatic top set identification and personal record detection.
- Estimated 1RM via the Epley formula: `1RM = Weight * (1 + Reps / 30)`

### Analytics
- Volume progression tracking over time.
- Daily average calorie and macro distributions across weekly and monthly windows.
- Session history with duration, timestamps, and notes.

---

## Tech Stack

| Component | Technology |
| :--- | :--- |
| Language | Kotlin 1.9.24 |
| UI | Jetpack Compose, Material 3, Compose Navigation |
| Architecture | MVVM with Unidirectional Data Flow |
| Persistence | Room SQLite 2.6.1 with Coroutines and Flow |
| State | Android ViewModel and StateFlow |
| Serialization | Gson 2.11.0 |
| Build | Gradle 8.9, Kotlin DSL, Java 17 |

---

## Database Schema

| Table | Entity | Primary Key | Description |
| :--- | :--- | :--- | :--- |
| `food_entries` | `FoodEntryEntity` | `id` (UUID) | Logged meals, serving sizes, macros, micronutrients |
| `water_entries` | `WaterEntryEntity` | `dateString` | Daily aggregate water intake (mL) |
| `favorite_foods` | `FavoriteFoodEntity` | `id` (UUID) | Frequently logged food items |
| `nutrition_goals` | `NutritionGoalEntity` | `id` (UUID) | Daily targets for calories, macros, and water |
| `exercise_definitions` | `ExerciseDefinitionEntity` | `name` | Exercise catalog with primary and secondary muscle targets |
| `workout_log_entries` | `WorkoutLogEntryEntity` | `id` (UUID) | Logged sets, load, reps, and RPE |
| `personal_records` | `PersonalRecordEntity` | `exerciseName` | Lifetime best lifts and calculated 1RM |
| `recorded_workout_sessions` | `RecordedWorkoutSessionEntity` | `id` (UUID) | Session timestamps, duration, and notes |

---

## Project Structure

```
gym_android/
├── Gym.apk                      # Standalone pre-built Android APK (1-click install)
├── .github/workflows/ci.yml     # Automated GitHub Actions build pipeline
├── Makefile                     # Shortcut build/test/release commands
└── app/src/main/
    ├── AndroidManifest.xml
    ├── java/com/gowtham/gym/
    │   ├── MainActivity.kt          # Entry point, login gate, bottom navigation
    │   ├── MainViewModel.kt         # State management and reactive flows
    │   ├── Models.kt                # Entities, enums, color definitions
    │   ├── GymDatabase.kt           # Room database and type converters
    │   ├── GymDao.kt                # Data access object
    │   └── ui/
    │       ├── home/HomeScreen.kt       # Summary dashboard and macro rings
    │       ├── log/LogScreen.kt         # Food logging and hydration
    │       ├── login/LoginScreen.kt     # Local password gate
    │       ├── workout/BodyMapScreen.kt # Anatomical body map and set tracker
    │       └── reports/ReportsScreen.kt # Trends and volume history
    └── res/
        ├── drawable-nodpi/          # Anatomical assets
        ├── mipmap-*/                # Launcher icons
        └── values/                  # Colors and configuration
```

---

<details>
<summary><strong>Developer Build Instructions (Optional — Only for Building from Source)</strong></summary>

### Prerequisites
- Android Studio Jellyfish (2023.3.1) or newer
- JDK 17
- Android SDK Platform 34 (Min SDK 26 / Android 8.0+)

### Build from Source
```bash
git clone https://github.com/GowthamBavikadi/MacroAndWorkoutTracker.git
cd MacroAndWorkoutTracker
./gradlew assembleRelease
```

The minified release APK is generated at `app/build/outputs/apk/release/app-release.apk`.

</details>

---

## License

[MIT License](LICENSE)

## Author

Gowtham Bavikadi — [@GowthamBavikadi](https://github.com/GowthamBavikadi)

[Open an Issue](https://github.com/GowthamBavikadi/MacroAndWorkoutTracker/issues)
