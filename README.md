# auto-Refresh-and-claim-app

## Overview

Auto Refresh & Claim is an Android accessibility automation app that detects on-screen "Claim" and "Confirm" buttons and clicks them automatically. The app also supports a user-configurable refresh interval so detection can run on a schedule.

## Features

- Accessibility service that detects visible buttons labeled "Claim" and "Confirm"
- Automatically clicks the claim button and then the confirm button
- User-configurable refresh interval in milliseconds
- Instructions screen and one-tap access to Android Accessibility settings
- Gradle build support with GitHub Actions for APK generation

## Project Structure

- `app/` - Android application module
- `app/src/main/java/com/example/autorefreshclaim/` - Kotlin app source code
- `.github/workflows/android.yml` - GitHub Actions workflow for APK build

## How to Use

1. Open the app on your Android device.
2. Tap **Open Accessibility Settings**.
3. Enable the `Auto Refresh & Claim` accessibility service.
4. Enter the desired refresh interval in milliseconds and tap **Save interval**.
5. Return to the target app or screen where the "Claim" button appears.
6. Watch the in-app log overlay for action status and the current service state.

> The service will periodically scan the active screen and attempt to click the first button containing the word `Claim`. If a confirmation button appears afterward, it will also click `Confirm`.

## Installation

### Build locally

1. Clone the repository.
2. Open a terminal in the repository root.
3. Run `./gradlew :app:assembleDebug`.
4. Install the generated APK from `app/build/outputs/apk/debug/app-debug.apk`.

### GitHub Actions

The workflow file `.github/workflows/android.yml` runs on push or pull request to the `main` branch.

What the workflow does:
- Checks out the repository.
- Sets up JDK 17.
- Builds the app using `./gradlew :app:assembleDebug`.
- Uploads the generated APK as a workflow artifact named `debug-apk`.

### How to download the APK from GitHub Actions

1. Open the repository on GitHub.
2. Go to the `Actions` tab.
3. Select the workflow run for the latest commit or pull request.
4. In the run summary, open the `Artifacts` section.
5. Download the `debug-apk` artifact.
6. Extract the artifact and use the APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Notes

- You must enable the accessibility service on the device for automatic clicking to work.
- If claim buttons disappear quickly, increase the refresh interval to give the app more time to detect them.
- This app is intended for automation of button pressing and depends on the accessibility framework.

## Included Files

- `AndroidManifest.xml` - app manifest and accessibility service registration
- `MainActivity.kt` - Compose UI for settings and instructions
- `ClaimAccessibilityService.kt` - accessibility event handling and click automation
- `Preferences.kt` - persistent refresh interval storage
- `.github/workflows/android.yml` - CI workflow to build the debug APK

## Development

- Android SDK 34
- Kotlin 1.9.10
- Jetpack Compose
- Gradle 8.0

## Troubleshooting

- If build fails due to Java version, make sure the project uses JDK 17 or JDK 21.
- If the accessibility service does not appear, confirm the app is installed and the service is enabled in Settings.
- Use logs in Android Studio or `adb logcat` to inspect the `ClaimAccessibility` log tag.

