# auto-Refresh-and-claim-app


#auto-Refresh-and-claim-app

Android OCR Screen Analyzer (Browser Development) Project Name Screen OCR Analyzer Objective Build a production-quality Android application completely in GitHub Codespaces. Do NOT require Android Studio on the local machine. Everything should be developed in the browser. APK should be generated automatically using GitHub Actions. Tech Stack Kotlin Jetpack Compose Material 3 Android SDK 35 CameraX (optional) Google ML Kit OCR Coroutines MVVM Architecture DataStore GitHub Actions Gradle Kotlin DSL Features Settings Screen Allow user to configure: Minimum Amount Maximum Amount Currency Example ₹1000 to ₹5000 Detection Interval Debug Mode Start Scan Stop Scan OCR Engine Use Google ML Kit Text Recognition. Detect every visible text on the screen. Extract Currency Numbers Decimal values Amount Parser Detect values like ₹500 ₹1200 ₹5,500 ₹10,000 INR 5000 Rs. 4500 Convert every detected value into Integer. Matching Engine Compare Detected Amount with User Selected Range Example Minimum = 1000 Maximum = 5000 If amount is inside range Create Match Event Overlay Draw transparent overlay. Highlight Detected Amount Bounding Box Confidence Logs Maintain logs. Time Detected Amount Confidence Bounding Box Matched = True/False Export logs as CSV. Dashboard Show OCR FPS Detection Count Matched Count Average OCR Time Memory Usage CPU Usage Architecture Use MVVM Repository Pattern Dependency Injection (Hilt optional) StateFlow ViewModel DataStore Folder Structure app/

ui/

ocr/

repository/

viewmodel/

overlay/

parser/

settings/

logs/

utils/

data/

models/ Build Gradle Kotlin DSL Latest Android SDK Latest ML Kit No deprecated libraries. GitHub Actions Create .github/workflows/android.yml Workflow should Checkout Repository Install Java 17 Setup Android SDK Grant Gradle Permission Build Debug APK Build Release APK Upload APK as Artifacts README Generate complete README including Installation Codespaces Setup How to Run How to Build APK How GitHub Actions Works How to Download APK Troubleshooting Project Architecture Folder Structure Screenshots Placeholder Future Improvements Documentation Generate Architecture Diagram Sequence Diagram Flow Diagram Performance OCR latency Below 150ms Memory optimized No memory leaks Coroutine based Testing Unit Tests Parser Tests OCR Tests UI Tests Benchmark Tests Deliverables Generate Complete Android Project Gradle Files Manifest Compose UI OCR Module Overlay Module Settings Module Repository README GitHub Actions License GitHub Codespaces Workflow Create GitHub Repository Open Codespaces Paste generated project Commit Push GitHub Actions automatically builds APK Open Actions tab Wait for build to finish Download APK from Artifacts Install APK on Android device for testing
