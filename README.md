#  ThermalVision – An Android Thermal Image Analysis App

ThermalVision is an Android application designed to analyze thermal images for temperature detection, hotspot identification, and interactive analysis. Built using **Kotlin** with **Jetpack Compose** and integrated with **Chaquopy** for embedded Python-based image processing, the app delivers a lightweight, offline-first solution for real-time thermal diagnostics.



## 📱 Features

- Upload or capture thermal images (TIFF or PNG).
- Set **minimum** and **maximum temperature thresholds** using sliders.
- Real-time **temperature detection** at user-tapped coordinates.
- Export results as a **CSV file** with temperature data and image metadata.
- Built using **Kotlin + Compose UI** and **scikit-image (Python)** .
- Works on Android 13 (Tiramisu) and above .



## 📁 Project Structure

```text
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
```


## 📸 Screenshots

| Landing Page | Uploading Image | Analysis  | Result | 
|--------------|------------------------|------------------|------------------|
| ![Landing](https://github.com/LakshmiSrikumar/ThermalVision/blob/72d7fc54f77dc1e4896da7f19941fc5dfba1e3b0/media/home_screen.jpg) | ![Upload](https://github.com/LakshmiSrikumar/ThermalVision/blob/72d7fc54f77dc1e4896da7f19941fc5dfba1e3b0/media/upload.jpg) | ![Analysis](https://github.com/LakshmiSrikumar/ThermalVision/blob/72d7fc54f77dc1e4896da7f19941fc5dfba1e3b0/media/Analysis.jpg) |  ![Result](https://github.com/LakshmiSrikumar/ThermalVision/blob/72d7fc54f77dc1e4896da7f19941fc5dfba1e3b0/media/Sheets.jpg) |


## 🎥 Demo Video

Watch the working of the app here:

https://github.com/user-attachments/assets/387266ff-f824-4b75-8968-55a50274f002


---

## 🚀 Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/LakshmiSrikumar/ThermalVision.git
   cd ThermalVision


