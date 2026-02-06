# Passwordless Authentication Android App

A production-ready Android application implementing passwordless email + OTP authentication with session tracking. Built using Jetpack Compose, MVVM architecture, and Kotlin Coroutines.

## Features

-  Email-based OTP authentication
-  60-second OTP expiry with countdown timer
-  3-attempt validation limit
-  Live session duration tracking
-  Analytics logging with Timber
-  Material 3 design
-  Local-only implementation (no backend required)

## 1. OTP Logic and Expiry Handling

### OTP Generation
- **Algorithm**: Random 6-digit number generation using `kotlin.random.Random`
- **Range**: 100000 to 999999 (ensures always 6 digits)
- **Storage**: In-memory `MutableMap<String, OtpData>` keyed by email
- **Expiry**: 60 seconds from generation time

### Expiry Handling
```kotlin
data class OtpData(
    val otp: String,
    val expiryTime: Long,  // System.currentTimeMillis() + 60_000
    val attemptsRemaining: Int = 3
)

fun isExpired(): Boolean = System.currentTimeMillis() > expiryTime
```

**Flow**:
1. OTP generated → `expiryTime = currentTime + 60000ms`
2. On validation → Check `isExpired()` first
3. If expired → Remove from storage, return `ValidationResult.Expired`
4. If valid → Continue to validation logic

### Attempt Tracking
- **Initial attempts**: 3
- **Decrement**: On each invalid OTP entry
- **Exhaustion**: When `attemptsRemaining == 0`, remove OTP and return `ValidationResult.AttemptsExhausted`
- **Reset**: New OTP generation resets attempts to 3

### Countdown Timer
- **Implementation**: `LaunchedEffect` in Compose
- **Update frequency**: 1 second
- **Display**: Real-time countdown in OTP screen
- **Behavior**: Disables "Verify OTP" button when timer reaches 0

## 2. Data Structures Used and Why

### `OtpData` (Data Class)
```kotlin
data class OtpData(
    val otp: String,
    val expiryTime: Long,
    val attemptsRemaining: Int = 3
)
```
**Why**: 
- Immutable data holder for OTP information
- `copy()` method for updating attempts without mutation
- Simple, type-safe structure

### `MutableMap<String, OtpData>`
```kotlin
private val otpStorage = mutableMapOf<String, OtpData>()
```
**Why**:
- **Key = Email**: Natural lookup key, ensures one OTP per email
- **Value = OtpData**: Encapsulates all OTP-related data
- **In-memory**: Meets "local-only" requirement
- **Mutable**: Allows updates for attempt tracking
- **O(1) lookup**: Fast retrieval and validation

**Alternatives considered**:
- List: O(n) lookup, no natural key
- Database: Overkill for temporary data
- Map: Perfect for key-value pairs with fast lookup

### `AuthState` (Sealed Class)
```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val email: String, val expiryTime: Long) : AuthState()
    data class OtpError(val message: String, val attemptsRemaining: Int = 0) : AuthState()
    data class Authenticated(val email: String, val sessionStartTime: Long) : AuthState()
}
```
**Why**:
- **Type-safe**: Compiler-enforced exhaustive `when` statements
- **Data encapsulation**: Each state carries only relevant data
- **Immutable**: Prevents accidental state mutations
- **Clear flow**: Easy to understand state transitions

### `ValidationResult` (Sealed Class)
```kotlin
sealed class ValidationResult {
    object Success : ValidationResult()
    object NoOtpFound : ValidationResult()
    object Expired : ValidationResult()
    object AttemptsExhausted : ValidationResult()
    data class Invalid(val attemptsRemaining: Int) : ValidationResult()
}
```
**Why**:
- **Explicit error handling**: Forces handling of all validation cases
- **Type-safe**: No magic strings or error codes
- **Self-documenting**: Clear what each result means

### `StateFlow<AuthState>`
```kotlin
private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
val authState: StateFlow<AuthState> = _authState.asStateFlow()
```
**Why**:
- **Reactive**: UI automatically updates on state changes
- **Lifecycle-aware**: Works seamlessly with Compose
- **Single source of truth**: One state drives entire UI
- **Coroutine-friendly**: Modern Kotlin approach

## 3. External SDK Choice: Timber

### Why Timber?

**Chosen**: [Timber](https://github.com/JakeWharton/timber) v5.0.1

**Reasons**:
1. **Simplicity**: Single-line initialization, minimal setup
2. **Lightweight**: ~50KB, no heavy dependencies
3. **Production-safe**: Easy to disable in release builds
4. **Android-optimized**: Built specifically for Android logging
5. **Jake Wharton**: Trusted, well-maintained library

**Alternatives considered**:
- **Firebase Analytics**: Too heavy for simple logging, requires Google services
- **Custom logging**: Reinventing the wheel, more maintenance
- **Timber**: Perfect balance of simplicity and functionality

### Implementation
```kotlin
// LokalApplication.kt
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

**Usage**:
```kotlin
Timber.d("OTP Generated for: $email")
Timber.e("OTP Validation Failed for: $email | Reason: $reason")
```

**Benefits**:
- Automatic tagging
- Debug-only logging (production-safe)
- Clean API
- No boilerplate

## 4. Development Approach: GPT vs Manual Implementation

### What I Used GPT For

#### 1. **Timber SDK Integration** 
- **Why**: First time using Timber, needed proper setup
- **What**: 
  - Gradle dependency configuration
  - Application class initialization
  - Best practices for debug-only logging
- **Learning**: Now understand Timber's `DebugTree` pattern and lifecycle

#### 2. **Gradle Dependency Resolution**
- **Why**: AGP 9.0 compatibility issues with KAPT/Dagger
- **What**: Troubleshooting plugin conflicts
- **Learning**: Understood AGP 9.0's built-in Kotlin support

#### 3. **LaunchedEffect for Timers**
- **Why**: Needed lifecycle-aware countdown implementation
- **What**: Proper coroutine usage in Compose
- **Learning**: Now understand `LaunchedEffect` keys and cancellation

### What I Implemented Myself

#### 1. **OTP Logic & Validation** 
- Designed the `OtpData` structure
- Implemented Map-based storage strategy
- Created validation flow with attempts/expiry
- Designed `ValidationResult` sealed class

#### 2. **MVVM Architecture** 
- Structured the project (data/viewmodel/ui layers)
- Designed `AuthState` sealed class
- Implemented StateFlow pattern
- Created state-based navigation

#### 3. **UI/UX Design** 
- Designed all three screens (Login, OTP, Session)
- Material 3 component selection
- Error handling and loading states
- User feedback mechanisms

#### 4. **Business Logic** 
- Email validation using Android Patterns
- OTP generation algorithm
- Session duration calculation
- Analytics event design

#### 5. **Manual Dependency Injection** 
- Created `AuthViewModelFactory`
- Designed dependency flow
- Decided against Dagger/Hilt due to AGP issues

### My Understanding

**Strong areas**:
- Kotlin language features (sealed classes, data classes, coroutines)
- MVVM architecture pattern
- Jetpack Compose fundamentals
- StateFlow and reactive programming
- Android lifecycle management

**Areas where GPT helped**:
- Specific library setup (Timber)
- Gradle/AGP compatibility issues
- Compose-specific patterns (LaunchedEffect)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM
- **Async**: Kotlin Coroutines + StateFlow
- **DI**: Manual (ViewModelFactory)
- **Logging**: Timber
- **Build**: Gradle (Kotlin DSL)

## Project Structure

```
app/src/main/java/com/example/lokalassignment/
├── data/
│   ├── OtpData.kt              # OTP model
│   └── OtpManager.kt           # OTP business logic
├── analytics/
│   ├── AnalyticsLogger.kt      # Analytics interface
│   └── TimberLogger.kt         # Timber implementation
├── viewmodel/
│   ├── AuthState.kt            # UI state sealed class
│   └── AuthViewModel.kt        # ViewModel with business logic
├── ui/
│   ├── LoginScreen.kt          # Email input screen
│   ├── OtpScreen.kt            # OTP validation screen
│   └── SessionScreen.kt        # Session tracking screen
├── AuthViewModelFactory.kt     # ViewModel factory
├── LokalApplication.kt         # App initialization
└── MainActivity.kt             # Entry point
```

## Setup & Run

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or higher
- **Android SDK**: API 34 (Android 14)
- **Git**: For cloning the repository

### Step 1: Clone the Repository
```bash
git clone https://github.com/Yashu-18/LokalAssignment.git
cd LokalAssignment
```

### Step 2: Open in Android Studio
1. Launch Android Studio
2. Click **File → Open**
3. Navigate to the cloned `LokalAssignment` folder
4. Click **OK**

### Step 3: Sync Gradle
- Android Studio will automatically prompt to sync Gradle
- If not, click **File → Sync Project with Gradle Files**
- Wait for sync to complete (may take a few minutes on first run)

### Step 4: Build the Project
```bash
# Using Gradle wrapper (recommended)
./gradlew build

# Or use Android Studio: Build → Make Project
```

### Step 5: Run the App

#### Option A: Using Android Studio
1. Connect an Android device (USB debugging enabled) or start an emulator
2. Click the **Run** button (green play icon) or press `Shift + F10`
3. Select your device/emulator
4. Wait for installation and launch

#### Option B: Using Command Line
```bash
# Install on connected device/emulator
./gradlew installDebug

# Or install and launch
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.lokalassignment/.MainActivity
```

### Step 6: View Logs (Optional)
To see OTP values and analytics logs:
```bash
# Filter Timber logs
adb logcat -s Timber

# Or in Android Studio: Logcat → Filter by "Timber"
```

## Testing the App

### 1. Login Flow
- Enter email: `test@example.com`
- Click "Send OTP"
- Check Logcat for OTP (filter: `tag:Timber`)

### 2. OTP Validation
- Enter the 6-digit OTP from Logcat
- Watch countdown timer (60s)
- Test invalid OTP (attempts decrement)
- Try "Resend OTP"

### 3. Session Screen
- Verify email display
- Watch live session timer
- Click "Logout"
- Check Logcat for session duration

## Key Features

### Email Validation
- Uses `android.util.Patterns.EMAIL_ADDRESS`
- Real-time validation
- Clear error messages

### OTP Security
- 60-second expiry
- 3-attempt limit
- Automatic cleanup
- No OTP reuse

### Session Tracking
- Precise millisecond tracking
- Live timer updates
- Formatted duration display
- Analytics on logout

