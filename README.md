# Alphabet Launcher 🚀

A modern, minimal Android launcher built with Jetpack Compose featuring a smooth, physics-based curved A–Z alphabet scrubber, instant app filtering, live clock, favorites management, and edge-to-edge system theme support.



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
