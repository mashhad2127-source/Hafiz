# HIFZ TRACKER — Native Android Kotlin + Jetpack Compose Project

Complete offline-first Android application source using:
- Kotlin + Jetpack Compose (RTL Urdu layout)
- Room Database (offline SQLite persistence)
- WorkManager + Silent NotificationManager (Zero voice / Zero TTS / Zero audio)
- BootReceiver (reschedules silent notifications after phone reboot)
- Google Sheets API v4 (10 tabs + real spreadsheet charts)

## Build Signed/Debug APK Without Android Studio (From Mobile Browser)
1. Create a free GitHub account and new repository on github.com from your phone's browser.
2. Upload the contents of this ZIP into the repository (keeping `.github/workflows/build-apk.yml`).
3. Open the **Actions** tab on GitHub and click **Run workflow** on **Build HIFZ TRACKER Android APK**.
4. Once finished (~3 minutes), download the compiled **hifz-tracker-apk** artifact directly to your Android phone and install the `.apk`.
