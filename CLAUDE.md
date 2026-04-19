# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build Android app
./gradlew :androidApp:assembleDebug

# Build shared module
./gradlew :composeApp:build

# Run all tests
./gradlew :composeApp:allTests

# Run common tests only
./gradlew :composeApp:testDebugUnitTest

# Clean build
./gradlew clean

# Check dependencies
./gradlew :composeApp:dependencies
```

Android SDK must be at `E:\Dev\Android\SDK` — `local.properties` with `sdk.dir=E:\\Dev\\Android\\SDK` is gitignored and must exist locally.

## Architecture

**Kotlin Multiplatform + Compose Multiplatform** project targeting Android and iOS. Two modules:

- **`composeApp`** — Shared KMP library containing all business logic, UI, networking, and data. This is where ~95% of code lives.
- **`androidApp`** — Thin Android shell. `MainActivity` initializes Koin and Logger, then calls `App()`.
- **`iosApp/`** — Swift wrapper. `iOSApp.swift` initializes Koin; `ContentView.swift` bridges Compose via `ComposeView` (UIViewControllerRepresentable).

### Layer Structure (all under `composeApp/src/commonMain/kotlin/.../`)

| Layer | Location | Purpose |
|-------|----------|---------|
| Navigation | `navigation/` | Type-safe sealed class routes (`Screen.Main`, `Screen.Users`, `Screen.Posts`) with kotlinx.serialization |
| Presentation | `presentation/viewmodel/` | MVVM — `BaseViewModel` provides loading/error/success state management; concrete VMs expose `StateFlow<UiState>` |
| Presentation | `presentation/screen/` | Compose screens consuming VM state via `collectAsStateWithLifecycle()` |
| Presentation | `presentation/defaults/` | `BaseContent` composable handles Loading/Error/Empty/Success rendering |
| Data | `data/network/` | Ktor `HttpClient` with JSON serialization, Napier logging, 60s timeout. `enqueue<T>()` returns `Flow<ResponseHandler<T>>` |
| Data | `data/repository/` | `DataStoreRepository` (plain) and `EncryptedDataStoreRepository` (AES on Android, no-op on iOS) |
| Data | `data/model/` | `@Serializable` data classes (User, Post, etc.) |
| DI | `di/` | Koin modules split by layer: `dataModule`, `presentationModule`, `platformModule` (expect/actual) |

### Platform Abstractions (expect/actual)

- **`Platform`** interface — device info (UDID, OS, model, app version). Implementations in `androidMain`/`iosMain`.
- **`DataStoreFactory`** — creates platform-specific `DataStore<Preferences>`.
- **`EncryptionService`** — Android uses AES/CBC with AndroidKeyStore; iOS is a placeholder.
- **`platformModule`** — Koin module providing platform-specific bindings.

### Key Patterns

- **ResponseHandler** sealed class: `Loading`, `Success(data)`, `Error(apiError)`, `Failure(exception)` — used throughout network layer.
- **BaseViewModel.executeOperationWithFlow()** — collects a `Flow<ResponseHandler<T>>` and auto-manages loading/error state.
- API base URL: `https://jsonplaceholder.typicode.com` (configured in `HttpConstants.kt`).
- Theme: GitHub-inspired Material 3 color scheme in `ui/theme/GitHubColorScheme.kt`.
- Logging: Napier singleton wrapper in `Logger.kt`.

## Dependencies (version catalog: `gradle/libs.versions.toml`)

Kotlin 2.3.10, Compose Multiplatform 1.10.1, AGP 9.1.0, Ktor 3.4.1, Koin 4.1.1, kotlinx-serialization 1.10.0, DataStore 1.2.0, Navigation 2.9.0, Napier 2.7.1.

## iOS Integration

iOS framework is `ComposeApp` (static). Entry: `MainViewController.kt` returns `ComposeUIViewController { App() }`. Koin init exposed via `KoinIOS.kt` → called from Swift as `KoinIOSKt.doInitKoin()`.
