# 🍅 Slicetime - Visual Focus Timer

**Slicetime** is a productivity application designed to help users manage their workflow using a visual interval method. By alternating between "Focus" (Red) and "Break" (Green) modes, the app encourages deep work habits and prevents burnout.

## ✨ Key Features

* **Visual Timer Loop:** Intuitive color-coded interface.
    * 🔴 **Focus Mode:** Red visual countdown for work sessions.
    * 🟢 **Break Mode:** Green visual countdown for rest.
* **Customizable Settings:** Users can adjust focus and break durations via sliders (saved locally).
* **Statistics Tracking:** Tracks total sessions and focus minutes using SharedPreferences.
* **Smart Notifications:** Alerts users when a session ends, even if the app is in the background (Android 13+ permission handling).
* **Monetization Model:**
    * Free tier: Limited to 5 sessions/day + AdMob Banner Ads.
    * Premium logic built-in (Unlock unlimited sessions & remove ads).

## 🛠 Tech Stack & Architecture

* **Language:** Kotlin
* **UI:** XML Layouts, Material Design Components
* **Architecture:** Single Activity Architecture (Navigation Component)
* **Data Persistence:** SharedPreferences (for user settings & stats)
* **Background Tasks:** CountDownTimer, NotificationManager
* **Monetization:** Google AdMob integration
* **Version Control:** Git & GitHub


## 🔮 Future Roadmap (V2.0)

* [ ] **Cloud Sync:** Firebase Authentication & Realtime Database integration to save stats across devices.
* [ ] **Social Mode:** "Study Together" rooms.
* [ ] **White Noise:** Integrated background sounds for better focus.
* [ ] **In-App Purchases:** Implementation of Google Play Billing Library.

## 👨‍💻 Author

Developed by **Adir**.

---
