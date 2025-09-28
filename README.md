# 🎓 Onboarding Flow Animation (Jetpack Compose)

An Android demo project showcasing **sequential animated cards** with gradient backgrounds, staggered animations, and API-driven configuration.  
Built entirely with **Jetpack Compose**.

---

## ✨ Features

- **🎬 Sequential card animation**
  - Cards animate in order:
    - `Offscreen → Halfscreen → Expanding → CollapsingTilted → Collapsed`
  - Last card remains expanded with CTA button.

- **🎨Dynamic Background**
  - Screen background gradient updates when each new card enters the **Expanding** state.

- **🚫 Blocking Touch Gestures**
  - User interaction is blocked until the final animation finishes.

- **📱 Composable UI**
  - MainEducationScreen orchestrates the whole flow
  - AnimatedEducationCard handles per-card animation
  - Lottie animation support for CTA icon.

- **🔗 API-driven config**
  - Cards list + UI timing from backend (ManualBuyEducationData)

---

## 🛠️ Tech Stack

- [Kotlin](https://kotlinlang.org/)  
- [Jetpack Compose](https://developer.android.com/jetpack/compose)  
- [Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3)  
- [Lottie for Compose](https://github.com/airbnb/lottie-android)  

---

## ▶️ How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/Rowdy-Rathore/JarAssignment2.git
   cd JarAssignment2
   ```
2. **Open in Android Studio (latest stable).**
3. **Sync Gradle dependencies.**
4. **Run the app on an emulator or physical device.**

---

## **📜 License**
This project is licensed under the MIT License.

---
