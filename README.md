
---
# 🏃‍♂️ FitTrack - Fitness Tracker App

> **Internship Project @ CODTECH IT SOLUTIONS**

| Internship Info      | Details                            |
|----------------------|-------------------------------------|
| **Name**             | Suraj Kumar                         |
| **Intern ID**        | CT04DH510                           |
| **Domain**           | Android Development                 |
| **Duration**         | 4 Weeks                             |
| **Mentor**           | Neela Santhosh                      |

---
## 📸 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/c36dcf3b-c4dc-4eef-a797-e1e123ebafd4" width="200" />
  <img src="https://github.com/user-attachments/assets/a08ac921-78ef-4133-bb82-3dcedbcaa242" width="200" />
  <img src="https://github.com/user-attachments/assets/b0447273-13ae-424b-bb33-dfb4a44c0a5b" width="200" />
</p>


**FitTrack** is a modern Android app built with Java that helps users track their daily steps, monitor distance and calories burned,
and visualize weekly progress. It uses Android's built-in sensors for real-time tracking and features a clean Material Design UI.

---

## 🚀 Features

| Feature | Description |
|--------|-------------|
| 🚶‍♂️ **Step Tracking** | Real-time step counting using device sensors |
| 📏 **Distance & Calories** | Automatically calculated from step count |
| 🎯 **Daily Goal Tracking** | Circular progress bar updates with goal completion |
| 📊 **Weekly Progress Chart** | Line chart (MPAndroidChart) showing last 7 days of steps |
| 🔄 **Auto Step Reset at 12AM** | Steps reset daily at midnight via broadcast receiver |
| 🔔 **Daily Notification** | Shows daily step summary at a random time |
| 🕓 **Boot Persistence** | Automatically resumes tracking after reboot |
| 📡 **Background Service** | Tracks steps even when app is closed or screen is off |

---


---

## 🛠 Tech Stack

- **Java**
- **Room Database**
- **LiveData + ViewModel**
- **MPAndroidChart**
- **Material Design 3**
- **Foreground & Background Services**
- **Broadcast Receivers**

---

## 📥 Download APK

👉 [Click here to download the APK](https://drive.google.com/file/d/1CaZuATdPneBQEt24gZK9HX3P7vsPmeTr/view?usp=sharing)

---

### Installation:
```bash
git clone https://github.com/yourusername/ExpenseTrackerApp.git
cd ExpenseTrackerApp
````

> Open the project in Android Studio and click ▶️ Run.

---
## 📂 Folder Structure

```plaintext
FitTrack/
├── MainActivity.java
├── StepViewModel.java
├── StepRepository.java
├── StepDao.java
├── Step.java
├── StepSensorService.java
├── StepTrackingService.java
├── StepResetReceiver.java
├── BootReceiver.java
├── StepNotificationReceiver.java
├── NotificationScheduler.java
├── StepChartUtil.java
└── res/
    └── layout/, drawable/, values/
````

---

## 🔐 Permissions Used

```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_HEALTH" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## 🧠 How It Works

* Steps are detected using `Sensor.TYPE_STEP_COUNTER`.
* Daily entries are stored using `Room` and queried with `LiveData`.
* Charts are built with `MPAndroidChart`.
* Steps auto-reset using `BroadcastReceiver` at midnight.
* Notification is triggered with a random quote/summary.
* Data persists even if the app is closed or phone is restarted.

---

## ✅ Planned Features

* [ ] 📈 Advanced reports with monthly stats
* [ ] 📤 Export data to CSV
* [ ] 🧠 Smart daily goal suggestion
* [ ] 🌓 Dark mode

---

## 🤝 Contributing

Want to contribute? Fork this repo, create a feature branch and submit a PR!

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file.

---

## 🙌 Acknowledgements
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) by Phil Jay
* [Material Design Components](https://m3.material.io/) by Google


## 👨‍💻 Developer

**Suraj Kumar**
[GitHub](https://github.com/surajpsk12) | [LinkedIn](https://linkedin.com/in/surajvansh12)

---


