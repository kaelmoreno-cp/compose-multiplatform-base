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
| Navigation | `navigation/` | Type-safe sealed class routes with auth gating |
| Auth | `auth/` | `AuthManager` + `AuthState` sealed interface for auth plumbing |
| Presentation | `presentation/viewmodel/` | MVVM — `BaseViewModel<T>` manages single `StateFlow<UiState<T>>`; VMs without data type extend `ViewModel()` directly |
| Presentation | `presentation/screen/` | Compose screens consuming `UiState` via `collectAsStateWithLifecycle()` |
| Presentation | `presentation/defaults/` | `BaseContent` composable handles `UiState` rendering (Loading/Error/Empty/Success) |
| Data | `data/network/` | Ktor `HttpClient` with Auth plugin, Kermit logging. `enqueue<T>()` returns `Flow<ResponseHandler<T>>` |
| Data | `data/network/datasource/` | `RemoteDataSource` wraps `ApiService` |
| Data | `data/network/model/` | `@Serializable` API models (User, Post, etc.) |
| Data | `data/local/database/` | Room KMP — `AppDatabase`, DAOs, entities |
| Data | `data/local/database/datasource/` | `LocalDataSource` wraps Room DAOs |
| Data | `data/local/preferences/` | `DataStoreRepository` and `EncryptedDataStoreRepository` |
| Data | `data/repository/` | `UserRepository` — orchestrates remote + local data |
| Data | `data/mapper/` | `UserMapper` — entity <-> domain model conversion |
| DI | `di/` | Koin modules: `dataModule`, `presentationModule`, `platformModule` (expect/actual) |
| UI | `ui/theme/` | `AppThemeConfig`, `Dimensions`, `AppTheme` composable with `CompositionLocal` |
| UI | `ui/components/` | Reusable components: `AppButton`, `AppCard`, `AppTopBar`, `AppDialog`, etc. |

### Platform Abstractions (expect/actual)

- **`Platform`** interface — device info (UDID, OS, model, app version).
- **`DataStoreFactory`** — creates platform-specific `DataStore<Preferences>`.
- **`DatabaseFactory`** — creates platform-specific `RoomDatabase.Builder`.
- **`EncryptionService`** — Android: AES/CBC with AndroidKeyStore; iOS: Base64 encoding.
- **`platformModule`** — Koin module providing platform-specific bindings.

### Key Patterns

- **UiState<T>** sealed interface: `Loading`, `Success(data)`, `Error(message)`, `Empty`.
- **BaseViewModel<T>** — single `StateFlow<UiState<T>>` with `execute(flow)` for automatic state management.
- **ResponseHandler** sealed class: `Loading`, `Success(data)`, `Error(apiError)`, `Failure(exception)`.
- **Repository pattern** — cache-first strategy: emit cached → fetch remote → cache → emit fresh.
- **Auth plumbing** — `AuthManager` manages auth state; Ktor Auth plugin handles bearer tokens.
- API base URL: `https://jsonplaceholder.typicode.com` (configured in `HttpConstants.kt`).
- Theme: Configurable `AppThemeConfig` with neutral defaults. Each project re-skins via config.
- Logging: Kermit (Touchlab) singleton wrapper in `Logger.kt`.

### Feature Flow Pattern

```
API → RemoteDataSource → Repository → ViewModel → Screen
                              ↕
         Room (DAO) → LocalDataSource
```

New features need: entity + DAO + remote data source + repository + mapper + ViewModel + screen.

## Dependencies (version catalog: `gradle/libs.versions.toml`)

Kotlin 2.3.10, Compose Multiplatform 1.10.1, AGP 9.1.0, Ktor 3.4.1 (with Auth plugin), Koin 4.1.1, kotlinx-serialization 1.10.0, DataStore 1.2.0, Navigation 2.9.0, Room KMP 2.7.1, Kermit 2.0.5, Coil 3.2.0, SKIE 0.10.1, Turbine 1.2.0, Kover 0.9.1.

## Testing

Tests use fakes (not mocks) for cross-platform compatibility. Test utilities:
- `FakeApiService`, `FakeUserDao`, `FakeAuthManager` in `commonTest/fake/`
- Turbine for Flow assertions
- kotlinx-coroutines-test for ViewModel testing

## iOS Integration

iOS framework is `ComposeApp` (static). SKIE plugin bridges Kotlin Flows to Swift async/await. Entry: `MainViewController.kt` returns `ComposeUIViewController { App() }`. Koin init exposed via `KoinIOS.kt` → called from Swift as `KoinIOSKt.doInitKoin()`.
