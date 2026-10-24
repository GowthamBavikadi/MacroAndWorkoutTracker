# Contributing Guidelines

Thank you for your interest in contributing to **Workout and Macros Tracker**. We welcome bug reports, feature suggestions, and pull requests.

> Found a bug? [Open a bug report](https://github.com/GowthamBavikadi/MacroAndWorkoutTracker/issues/new)  
> Have an idea? [Open a feature request](https://github.com/GowthamBavikadi/MacroAndWorkoutTracker/issues/new)

---

## Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/GowthamBavikadi/MacroAndWorkoutTracker.git
   cd MacroAndWorkoutTracker
   ```

2. Open the project in Android Studio (Jellyfish 2023.3.1 or newer recommended).

3. Ensure JDK 17 is configured in Android Studio under **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.

4. Verify compilation:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Git and Branch Workflow

Working through feature branches keeps the git history clean and manageable:

1. Create a branch for your change:
   ```bash
   git checkout -b feat/add-export-backup
   ```

2. Format commits using the Conventional Commits specification:
   ```text
   feat(workout): add rest timer between logged sets
   fix(database): resolve foreign key constraint on session deletion
   docs(readme): update build instructions for Android 14
   perf(compose): optimize circular progress ring recomposition
   ```

3. Ensure project compiles and passes verification:
   ```bash
   ./gradlew check
   ```

4. Push your branch and open a Pull Request against `main`.

---

## Code Standards

| Concern | Standard |
| :--- | :--- |
| **Formatting** | Official Kotlin Style Guide (Kotlin 1.9+) |
| **Architecture** | Unidirectional Data Flow (UDF) via MVVM |
| **UI Components** | Jetpack Compose Material 3; keep composables stateless where possible |
| **Persistence** | Room SQLite DAOs returning Coroutine Flow for asynchronous streams |
| **Privacy** | Never introduce network calls or analytics SDKs; the app must remain 100% offline |

---

## Continuous Integration Checks

Every pull request is automatically built and validated via GitHub Actions. All build and lint checks must pass before merging.
