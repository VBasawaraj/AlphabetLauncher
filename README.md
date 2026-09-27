# Alphabet Launcher 🚀

A modern, minimal Android launcher built with Jetpack Compose featuring a smooth, physics-based curved A–Z alphabet scrubber, instant app filtering, live clock, favorites management, and edge-to-edge system theme support.

The curve animation is written **100% custom from scratch** without any third-party animation libraries, using physics and math principles

Locked 60/120 FPS Rendering (graphicsLayer)
To ensure zero frame drops and stutter-free touch tracking:

Touch tracking uses awaitEachGesture in pointerInput with 0ms start latency.
State reads for deflection, scale, and alpha occur strictly inside Modifier.graphicsLayer { ... }.
This bypasses Compose recomposition and layout passes entirely; matrix transformations are executed directly during the draw/render phase on the GPU.

📚 Libraries Used
Library	Version	Justification
androidx.compose.bom	2026.02.01	Manages consistent, stable versions for all Jetpack Compose UI dependencies.
androidx.compose.ui	Via BOM	Foundation for modern declarative Android UI rendering.
androidx.compose.material3	Via BOM	Material 3 components, dynamic theming, and color schemes.
androidx.activity.compose	1.13.0	Integrates ComponentActivity, edge-to-edge support, and BackHandler.
androidx.lifecycle.runtime.ktx	2.6.1	ViewModel lifecycle management and Coroutine StateFlow collection.
androidx.core.ktx	1.19.1	Kotlin extensions for Android framework utilities and drawables.
junit	4.13.2	Fast JVM unit testing for math and data structures.

## 🛠️ Setup & Build Instructions

### Prerequisites
- Android Studio Ladybug / Meerkat or later
- JDK 17 or later (JDK 21/26 supported)
- Android SDK 35+ / compileSdk 37

### Building the Project
1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd AlphabetLauncher
   ```
2. Run unit tests:
   ```bash
   ./gradlew test
   ```
3. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
   The generated APK will be located at:
   `app/build/outputs/apk/debug/app-debug.apk`
4. Install and run on an Android device or emulator:
   ```bash
   ./gradlew installDebug
   ```
