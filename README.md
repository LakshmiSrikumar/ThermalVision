#  ThermalVision – An Android Thermal Image Analysis App

ThermalVision is an Android application designed to analyze thermal images for temperature detection, hotspot identification, and interactive analysis. Built using **Kotlin** with **Jetpack Compose** and integrated with **Chaquopy** for embedded Python-based image processing, the app delivers a lightweight, offline-first solution for real-time thermal diagnostics.

---

## 📱 Features

- Upload or capture thermal images (TIFF or PNG).
- Set **minimum** and **maximum temperature thresholds** using sliders.
- Real-time **temperature detection** at user-tapped coordinates.
- Export results as a **CSV file** with temperature data and image metadata.
- Built using **Kotlin + Compose UI** and **scikit-image (Python)** .

---

## 📁 Project Structure

ThermalVision/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/thermal/
│   │   │   │   ├── MainActivity.kt            # Entry point and navigation handler
│   │   │   │   └── ui/
│   │   │   │       ├── LandingPage.kt         # Start screen UI
│   │   │   │       ├── LoadingPage.kt         # Image selection + temperature range UI
│   │   │   │       ├── AnalysisPage.kt        # Displays processed image & results
│   │   │   │       └── theme/                 # Jetpack Compose theming
│   │   │   ├── python/utils.py                # Python code for image processing (via Chaquopy)
│   │   │   ├── res/                           # Android resources (manifest, drawables, etc.)
│   │   ├── test/                              # Unit tests
│   │   └── androidTest/                       # UI / instrumentation tests
│   ├── build.gradle.kts                       # Module-level Gradle config
├── gradle/                                    # Gradle wrapper config
├── settings.gradle.kts                        # Project settings
├── local.properties                           # Local SDK paths
├── build.gradle.kts                           # Root-level Gradle config
├── .gitignore
└── README.md


## 📸 Screenshots

| Landing Page | Uploading Image | Analysis Results |
|--------------|------------------------|------------------|
| ![Landing](![Screenshot_20250423_030051_ThermalApp](https://github.com/user-attachments/assets/0af9c30c-8d65-4332-96b0-91a94301e064)) | ![Upload](![Screenshot_20250423_030051_ThermalApp](https://github.com/user-attachments/assets/cca7e247-cae5-43a4-bdbd-19b328287edf)) | ![Results](![Screenshot_20250423_030138_ThermalApp](https://github.com/user-attachments/assets/dcb1e480-0f60-4fb6-96c1-1adb2807d1ce)) |

---

## 🎥 Demo Video

Watch the working of the app here:  
[![Watch the video](https://github.com/user-attachments/assets/0fc00ad4-eee6-492c-91df-1e52c9747ab6)


---

## 🚀 Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/LakshmiSrikumar/ThermalVision.git
   cd thermalvision-app
