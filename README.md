# Timestamp Camera Pro 📷📍

Timestamp Camera Pro is a professional geotagging and timestamp camera Android application designed for construction sites, field inspections, land surveys, security patrols, and logistics delivery verification.

## 🚀 GitHub Actions Auto-APK Build

This repository includes a pre-configured GitHub Actions workflow (`.github/workflows/build-apk.yml`) that automatically compiles and packages the APK whenever you push code or trigger it manually.

### How to Download APK from GitHub:
1. Push this project to your GitHub repository (via AI Studio's **Export to GitHub** button).
2. Go to your repository on GitHub.
3. Click on the **Actions** tab at the top.
4. Select the latest workflow run: **"Build Android APK"**.
5. Scroll down to the **Artifacts** section at the bottom of the page.
6. Click on **`TimestampCameraPro-Debug-APK`** to download your ready-to-install `.apk` file!

---

## 🛠️ Building Locally

If you clone the repository locally on your computer:

```bash
# Make gradlew executable
chmod +x gradlew

# Build Debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## ✨ Features

- **High-Precision Watermark Engine:** Burns date, time, GPS coordinates (Decimal, DMS, UTM), street address, altitude, and compass bearing directly into photo pixels.
- **Industry Presets:** Construction/Jobsite, Field Inspection & QA, Security Patrol, Land Survey, and Delivery/Logistics.
- **Real-Time Camera HUD:** Live overlay with tap-to-focus, zoom, flash controls, and lens switching.
- **Local Room Database:** Encrypted local persistence of all captured media records and metadata.
- **Mock GPS Detector:** Alerts if mock or simulated GPS locations are detected.
