# Compose Multiplatform Template Modernization

**Date:** 2026-04-19
**Status:** Approved
**Purpose:** Modernize the CMP base template to be a production-ready foundation for real Android + iOS projects of any scale.

---

## Context

This is a Compose Multiplatform template project used as the starting point for real production apps targeting both Android and iOS. The current architecture (MVVM, Ktor, Koin, Compose Navigation) is fundamentally sound but needs modernization in state management, data layer, auth plumbing, design system, and testing infrastructure.

**Approach:** Incremental modernization within the existing single-module structure. No multi-module refactor — clean package boundaries provide sufficient separation for a template.

---

## 1. State Management Overhaul

### Problem

`BaseViewModel` manages state via 3 separate StateFlows (`isLoading`, `error`, `successMessage`). This allows impossible states (loading + error simultaneously) and forces screens to combine flows manually.

### Design

Single sealed interface per feature:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}
```

`BaseViewModel` simplified to manage a single `StateFlow<UiState<T>>`:

```kotlin
abstract class BaseViewModel<T> : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    protected fun execute(flow: Flow<ResponseHandler<T>>) {
        viewModelScope.launch {
            flow.collect { response ->
                _uiState.value = when (response) {
                    is ResponseHandler.Loading -> UiState.Loading
                    is ResponseHandler.Success -> UiState.Success(response.result!!)
                    is ResponseHandler.Error -> UiState.Error(response.apiError?.error?.message ?: "Unknown error")
                    is ResponseHandler.Failure -> UiState.Error(response.exception?.message ?: "Unknown error")
                }
            }
        }
    }

    abstract fun retry()
}
```

Screen-local UI state (e.g., `selectedUser`) remains as separate `MutableStateFlow` in the ViewModel — only data-fetching lifecycle state is unified.

Screens consume via single `when` block:

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()

when (val s = state) {
    is UiState.Loading -> LoadingContent()
    is UiState.Empty -> EmptyContent(onRefresh = { viewModel.retry() })
    is UiState.Error -> ErrorContent(s.message, onRetry = { viewModel.retry() })
    is UiState.Success -> { /* render s.data */ }
}
```

---

## 2. Room KMP Integration

### Structure

```
data/
  local/
    database/
      AppDatabase.kt
      dao/UserDao.kt
      entity/UserEntity.kt
      converter/
      datasource/LocalDataSource.kt
  network/
    datasource/RemoteDataSource.kt
    model/User.kt, Post.kt, ApiError.kt, etc.
  repository/
    UserRepository.kt
  mapper/
    UserMapper.kt
```

### Key Decisions

- **Separate entity classes from API models.** `User` (API) and `UserEntity` (Room) are different classes connected by mappers. DB schema and API responses evolve independently.
- **Repository as orchestrator.** ViewModels call Repository, which decides: fetch from cache, hit API and cache, or return local-only.
- **Template includes one sample** (User entity + DAO + repository) to demonstrate the pattern.
- **DataStore stays** for preferences/settings alongside Room for structured relational data.
- **`expect/actual` DatabaseFactory** for platform-specific `RoomDatabase.Builder`.

### Sample DAO

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<UserEntity>>

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
```

---

## 3. Auth Plumbing

No login/register UI — just the infrastructure each project plugs into.

### Components

**AuthState** — observable auth state:

```kotlin
sealed interface AuthState {
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState
    data object Loading : AuthState
}
```

**AuthManager** — single source of truth, provided via Koin:

```kotlin
class AuthManager(
    private val encryptedDataStore: EncryptedDataStoreRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun setTokens(access: String, refresh: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearAuth()
    suspend fun isAuthenticated(): Boolean
}
```

**Ktor Auth Plugin** — uses Ktor's built-in `Auth` plugin with `bearer` provider:

```kotlin
install(Auth) {
    bearer {
        loadTokens { /* get from AuthManager */ }
        refreshTokens { /* call refresh endpoint, update AuthManager */ }
    }
}
```

**Navigation-level auth gating** — `AppNavigation` observes `authState` to redirect unauthenticated users. Wiring included, no login screen.

### What's NOT included (each project provides)

- Login/register UI screens
- Specific auth API endpoints
- OAuth/social login flows
- Biometric auth

---

## 4. Design System & Component Library

### Theme Architecture

```kotlin
data class AppThemeConfig(
    val lightColors: ColorScheme,
    val darkColors: ColorScheme,
    val typography: Typography = defaultTypography(),
    val shapes: Shapes = defaultShapes(),
    val dimensions: Dimensions = defaultDimensions()
)

data class Dimensions(
    val spacingXs: Dp,    // 4.dp
    val spacingSm: Dp,    // 8.dp
    val spacingMd: Dp,    // 16.dp
    val spacingLg: Dp,    // 24.dp
    val spacingXl: Dp,    // 32.dp
    val cornerRadius: Dp, // 12.dp
    val iconSize: Dp,     // 24.dp
)

val LocalDimensions = staticCompositionLocalOf { defaultDimensions() }
```

The GitHub color scheme is replaced with a neutral default that each project re-skins by changing `AppThemeConfig`.

### Component Library (`ui/components/`)

| Component | Purpose |
|-----------|---------|
| `AppButton` | Primary, secondary, outlined, text variants |
| `AppTextField` | Styled input with error state support |
| `AppCard` | Consistent card with elevation/padding |
| `AppTopBar` | Top app bar with back nav + actions |
| `AppDialog` | Confirmation/alert dialog |
| `AppBottomSheet` | Standard bottom sheet |
| `AppLoadingButton` | Button with inline loading indicator |
| `AppSnackbar` | Themed snackbar host |

All components use `LocalDimensions` and Material 3 theme — swapping `AppThemeConfig` re-skins everything.

### Updated BaseContent

```kotlin
@Composable
fun <T> BaseContent(
    state: UiState<T>,
    onRetry: () -> Unit,
    empty: @Composable () -> Unit = { EmptyContent(onRefresh = onRetry) },
    loading: @Composable () -> Unit = { LoadingContent() },
    error: @Composable (String) -> Unit = { ErrorContent(it, onRetry = onRetry) },
    content: @Composable (T) -> Unit
)
```

---

## 5. Library Changes

### Additions

| Library | Purpose |
|---------|---------|
| Kermit (Touchlab) | Logging — replaces Napier. Better crash reporter integrations. |
| Koin Compiler Plugin | Compile-time DI graph verification |
| Turbine | Flow testing — standard for KMP |
| Kover | Code coverage — JetBrains official |
| Coil 3 | KMP-native image loading |
| SKIE (Touchlab) | Swift/Flow interop — bridges Flows to async/await |
| Room KMP | Local database |

### Removals

| Library | Reason |
|---------|--------|
| Napier | Replaced by Kermit |
| GitHub color scheme | Replaced by neutral configurable theme |

### Unchanged

- Ktor 3.4.1
- Koin 4.1.1 (adding compiler plugin only)
- Compose Navigation 2.9.0
- DataStore
- kotlinx-serialization

---

## 6. Project Structure

```
composeApp/src/commonMain/kotlin/.../
├── App.kt
├── Logger.kt                              -- rewired to Kermit
├── navigation/
│   ├── Screen.kt
│   └── AppNavigation.kt                   -- + auth gating
├── data/
│   ├── network/
│   │   ├── NetworkClient.kt               -- + Ktor Auth plugin
│   │   ├── NetworkExtensions.kt
│   │   ├── HttpConstants.kt
│   │   ├── ResponseHandler.kt
│   │   ├── datasource/RemoteDataSource.kt
│   │   └── model/
│   │       ├── User.kt, Post.kt, etc.
│   │       └── ApiError.kt
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/UserDao.kt
│   │   │   ├── entity/UserEntity.kt
│   │   │   ├── converter/
│   │   │   └── datasource/LocalDataSource.kt
│   │   └── preferences/
│   │       ├── DataStoreRepository.kt
│   │       └── EncryptedDataStoreRepository.kt
│   ├── repository/
│   │   └── UserRepository.kt
│   └── mapper/
│       └── UserMapper.kt
├── auth/
│   └── AuthManager.kt
├── presentation/
│   ├── viewmodel/
│   │   ├── BaseViewModel.kt
│   │   ├── UserViewModel.kt
│   │   ├── PostsViewModel.kt
│   │   └── MainScreenViewModel.kt
│   ├── screen/
│   │   ├── MainScreen.kt
│   │   ├── UserListScreen.kt
│   │   └── PostsListScreen.kt
│   └── defaults/
│       ├── BaseContent.kt
│       ├── LoadingContent.kt
│       ├── ErrorContent.kt
│       └── EmptyContent.kt
├── ui/
│   ├── theme/
│   │   ├── AppThemeConfig.kt
│   │   ├── Dimensions.kt
│   │   ├── Theme.kt
│   │   ├── Type.kt
│   │   └── Shape.kt
│   └── components/
│       ├── AppButton.kt
│       ├── AppTextField.kt
│       ├── AppCard.kt
│       ├── AppTopBar.kt
│       ├── AppDialog.kt
│       ├── AppBottomSheet.kt
│       ├── AppLoadingButton.kt
│       └── AppSnackbar.kt
├── di/
│   ├── AppModules.kt
│   ├── DataModule.kt
│   ├── PresentationModule.kt
│   └── PlatformModule.kt
└── platform/
    ├── Platform.kt
    ├── DataStoreFactory.kt
    └── DatabaseFactory.kt
```

### Testing Structure

```
composeApp/src/commonTest/kotlin/.../
├── data/
│   ├── repository/UserRepositoryTest.kt
│   └── network/ApiServiceTest.kt
├── presentation/
│   └── viewmodel/UserViewModelTest.kt
└── fake/
    ├── FakeApiService.kt
    └── FakeUserDao.kt
```

Fakes preferred over mocks — work on all platforms without code generation.

---

## 7. Sample Feature Flow (End-to-End)

Every feature follows this pattern:

```
API → RemoteDataSource → Repository → ViewModel → Screen
                              ↕
         Room (DAO) → LocalDataSource
```

**Repository** (orchestrator):

```kotlin
class UserRepository(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val mapper: UserMapper
) {
    fun getUsers(): Flow<ResponseHandler<List<User>>> = flow {
        emit(ResponseHandler.Loading())
        val cached = local.getUsers().first()
        if (cached.isNotEmpty()) {
            emit(ResponseHandler.Success(mapper.toDomain(cached)))
        }
        remote.getUsers()
            .onSuccess { users ->
                local.upsertUsers(mapper.toEntities(users))
                emit(ResponseHandler.Success(users))
            }
            .onError { emit(it) }
    }
}
```

**ViewModel** (minimal):

```kotlin
class UserViewModel(
    private val repository: UserRepository
) : BaseViewModel<List<User>>() {
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    init { loadUsers() }
    private fun loadUsers() = execute(repository.getUsers())
    fun selectUser(user: User) { _selectedUser.value = user }
    override fun retry() = loadUsers()
}
```

**Screen** (just UI):

```kotlin
@Composable
fun UserListScreen(viewModel: UserViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BaseContent(state = state, onRetry = viewModel::retry) { users ->
        LazyColumn {
            items(users) { user ->
                AppCard(onClick = { viewModel.selectUser(user) }) {
                    Text(user.name)
                }
            }
        }
    }
}
```

New features just need: entity + DAO + remote data source + repository + mapper + ViewModel + screen.
