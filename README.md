# Alphabet Launcher 🚀

A modern, minimal Android launcher built with Jetpack Compose featuring a smooth, physics-based curved A–Z alphabet scrubber, instant app filtering, live clock, favorites management, and edge-to-edge system theme support.

---

## 📱 Features

### 🌟 Core Requirements
1. **Home Screen**: Large live-updating clock and date, accompanied by a clean list of favorite applications.
2. **Curved A–Z Bar**: Vertical alphabet column pinned to the right edge with a Star (`☆`) at the top, letters `A` to `Z` in the center, and Dot (`•`) at the bottom.
3. **Smooth Curve Animation**: Letters smoothly bulge and bend toward the user's finger with a natural Gaussian falloff in real time at steady 60/120 fps.
4. **Letter Bubble**: Enlarged, circular bubble indicating the currently scrubbed letter with entrance and exit spring animations.
5. **Instant App Filtering**: Dragging across letters replaces the home screen with an animated header and case-insensitive sorted app list starting with that letter.
6. **Empty Letters Handling**: When no apps start with a scrubbed letter, a clear and elegant empty state is displayed (`"No apps found for '$letter'"`).
7. **Spring Release**: Lifting the finger triggers an elastic spring animation with subtle overshoot that settles back into a straight vertical bar while restoring the clock and favorites.
8. **App Launching**: Single tap on any app in any list launches the application immediately.
9. **High Performance**: Real launchable apps queried once with `PackageManager` (handling Android 11+ `<queries>` and permissions), pre-rendered bitmap caching, and zero layout recalculation during touch gestures.

### 🎁 Bonus Features Implemented
- **Home Launcher (Default Launcher)**: Configured with `CATEGORY_HOME` and `CATEGORY_DEFAULT` in `AndroidManifest.xml`.
- **Haptic Feedback**: Subtle mechanical tick vibration on every letter transition (`HapticHelper` using `VibrationEffect.EFFECT_TICK`).
- **Spring Physics**: Realistic spring physics (`dampingRatio = 0.55f`) creating an authentic elastic overshoot when the curved bar is released.
- **Swipe-Up Search**: Swiping up on the home screen smoothly transitions to an auto-focused search screen with real-time app filtering.
- **Favorites Persistence**: Long-pressing any app opens a modern options sheet allowing users to add/remove apps from favorites (persisted via `SharedPreferences`), view Android App Info, or launch the app.
- **Live Updates**: Dynamic `BroadcastReceiver` automatically reloads the launcher when apps are installed, updated, or uninstalled.
- **Empty Letter Dimming**: Letters without installed apps are automatically dimmed for quick visual scanning while remaining scrubbable.
- **Dark & Light Theme**: Seamlessly adapts to system theme with modern, high-contrast typography and color schemes.
- **Unit Test Suite**: JVM unit tests covering letter grouping, edge cases, touch-to-letter coordinate mapping, and curve deflection mathematics.

---

## 📐 How the Curve Animation Was Built

The curve animation is written **100% custom from scratch** without any third-party animation libraries, using physics and math principles:

### 1. Dynamic Coordinate Geometry
Instead of hardcoding screen heights or offsets, the alphabet bar dynamically measures available container height ($H$) inside `BoxWithConstraints`. Safe top and bottom insets are subtracted to determine the usable scrub height:
$$\text{itemHeight} = \frac{H - \text{topPadding} - \text{bottomPadding}}{28}$$
Each of the 28 elements (Star, letters A–Z, Dot) has an exact mathematical vertical center $Y_k$. Touch-to-letter mapping and visual rendering share this identical coordinate system, guaranteeing 100% pixel-perfect selection.

### 2. Gaussian Bell Curve Deflection
When the user places a finger at coordinate $Y_{\text{finger}}$, each letter $k$ with vertical center $Y_k$ has vertical distance $\Delta y = Y_k - Y_{\text{finger}}$. The horizontal deflection $D_k$ towards the center of the screen is computed via a Gaussian bell curve:
$$D_k = D_{\text{max}} \cdot \exp\left(-\frac{\Delta y^2}{2\sigma^2}\right) \cdot \text{bendProgress}$$
- $D_{\text{max}} \approx 58\,\text{dp}$: Maximum deflection peak directly beneath the user's finger.
- $\sigma \approx 72\,\text{dp}$: Standard deviation determining the natural width and slope of the curve.
- $\text{Scale}_k = 1.0 + 0.35 \cdot \exp\left(-\frac{\Delta y^2}{2\sigma^2}\right) \cdot \text{bendProgress}$: Subtle magnification for letters nearest the finger.

### 3. Elastic Spring Release with Overshoot
When the user lifts their finger, `bendProgress` transitions from $1.0$ to $0.0$ using Compose's spring physics:
```kotlin
spring(
    dampingRatio = 0.55f, // Bouncy overshoot
    stiffness = Spring.StiffnessMediumLow
)
```
Because the damping ratio is underdamped ($< 1.0$), the curve slightly overshoots past its resting point before settling, replicating the rubber-band elastic snap seen in modern physical launchers.

### 4. Locked 60/120 FPS Rendering (`graphicsLayer`)
To ensure zero frame drops and stutter-free touch tracking:
- Touch tracking uses `awaitEachGesture` in `pointerInput` with 0ms start latency.
- State reads for deflection, scale, and alpha occur **strictly inside `Modifier.graphicsLayer { ... }`**.
- This bypasses Compose recomposition and layout passes entirely; matrix transformations are executed directly during the draw/render phase on the GPU.

---

## 📚 Libraries Used

| Library | Version | Justification |
| :--- | :--- | :--- |
| `androidx.compose.bom` | `2026.02.01` | Manages consistent, stable versions for all Jetpack Compose UI dependencies. |
| `androidx.compose.ui` | Via BOM | Foundation for modern declarative Android UI rendering. |
| `androidx.compose.material3` | Via BOM | Material 3 components, dynamic theming, and color schemes. |
| `androidx.activity.compose` | `1.13.0` | Integrates `ComponentActivity`, edge-to-edge support, and `BackHandler`. |
| `androidx.lifecycle.runtime.ktx` | `2.6.1` | ViewModel lifecycle management and Coroutine StateFlow collection. |
| `androidx.core.ktx` | `1.19.1` | Kotlin extensions for Android framework utilities and drawables. |
| `junit` | `4.13.2` | Fast JVM unit testing for math and data structures. |

*Note: In accordance with assignment instructions, zero third-party animation libraries were used. All curve physics, animations, and touch scrubber mechanics are custom implemented.*

---

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
