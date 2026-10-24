# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned
- Local backup and restore via JSON and CSV export
- On-device barcode scanner for instant nutritional intake lookup
- Rest interval countdown timer with sound notifications
- Custom exercise creator with user-defined muscle group tags

---

## [1.0.0] - 2026-10-23

### Added
- Core Android application architecture built with Kotlin 1.9.24 and Jetpack Compose Material 3.
- Offline Room SQLite database (`GymDatabase.kt`) storing food entries, hydration metrics, workout sessions, exercise definitions, personal records, and user nutrition goals.
- Reactive `GymDao` interface with Coroutine Flow streams for automatic UI synchronization.
- Unified `MainViewModel` implementing unidirectional data flow and state management.
- Dynamic Apple Health-style circular progress gauges for calories, protein, carbohydrates, fat, and hydration.
- Interactive anatomical anterior and posterior body maps targeting 13 muscle groups.
- Set-by-set workout logger tracking load, repetitions, notes, and Rate of Perceived Exertion (RPE).
- Automated Personal Record (PR) tracking with estimated 1-Rep Max (1RM) calculations using the Epley formula.
- Workout stopwatch session recorder capturing elapsed time, timestamps, and notes.
- Historical analytics view with daily averages, volume progression, and session breakdown.
- Built-in food catalog containing standard whole foods and macro values.
- Custom OLED dark theme with spring-physics floating bottom navigation bar.
- Completely offline architecture requiring zero network permissions in `AndroidManifest.xml`.
