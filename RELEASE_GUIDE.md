# Saitama Training — APK Release & Installation Guide 🚀

This guide provides step-by-step instructions on how to build, transfer, and install the **Saitama Training** Android application on your phone.

---

## 📱 Quick Start: Choosing the Right APK Type

Depending on your needs, you can build two types of APK files:

| Build Type | Best For | Security Signing | Minification & Shrinking | Setup Difficulty |
| :--- | :--- | :--- | :--- | :--- |
| **Debug APK** | Sideloading, quick testing, or sharing with friends for instant use. | Self-signed Debug Key | Off | **Zero (recommended for fast testing)** |
| **Release APK** | App store publishing or final highly optimized local storage. | Upload Key (Production JKS) | On (Proguard & R8 resource shrinking) | Medium |

---

## ⚡ Method 1: The Automated Build Script (Easiest)

We have created pre-configured build scripts that automate everything (environment checks, self-signed keystore generation, clean build, and moving the output file).

### On macOS / Linux
1. Open your terminal in the root of the project.
2. Grant execution permission:
   ```bash
   chmod +x build-apk.sh
   ```
3. Run the script:
   ```bash
   ./build-apk.sh
   ```
4. Follow the interactive prompts to choose **Debug** or **Release** (the script will automatically create a keystore called `my-upload-key.jks` if needed).

### On Windows
1. Double-click the `build-apk.bat` file in the root directory, or open CMD and run:
   ```cmd
   build-apk.bat
   ```
2. Follow the on-screen options.

**Your final APK will be placed in:** `/build-outputs/SaitamaTraining-[Variant].apk`

---

## 🛠️ Method 2: Manual Terminal Commands (For Developers)

If you prefer to run raw Gradle tasks directly, use the following commands:

### To Build a Debug APK
```bash
gradle clean assembleDebug
```
*The output file will be at:* `app/build/outputs/apk/debug/app-debug.apk`

### To Build a Release APK (Requires keystore environment variables or a pre-configured keystore)
```bash
gradle clean assembleRelease
```
*The output file will be at:* `app/build/outputs/apk/release/app-release.apk`

---

## 🔐 Setting up Your Custom Production Keystore (Optional)

If you intend to build a signed release APK manually without our script:
1. Generate an upload key using JDK's `keytool`:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Set the following environment variables in your command line or `.env` file before building:
   - `KEYSTORE_PATH` = Full path to your `.jks` file
   - `STORE_PASSWORD` = Your keystore store password
   - `KEY_PASSWORD` = Your key alias password

---

## 📲 How to Install the APK on Your Android Phone

1. **Transfer the APK to Your Phone**:
   - **Cloud Storage (Easiest)**: Upload the `.apk` file to Google Drive, Dropbox, or OneDrive from your computer, then open the respective app on your phone.
   - **USB Cable**: Connect your phone to your PC/Mac, change connection mode to "File Transfer", and drag-and-drop the `.apk` file into your phone's `Download` folder.
   - **Chat App / Local Sharing**: Send it to yourself via an instant messaging app or local sharing service (like Quick Share / Nearby Share).

2. **Allow Sideloading (Unknown Sources)**:
   - When you tap the downloaded APK, Android may say: *"For your security, your phone is not allowed to install unknown apps from this source."*
   - Tap **Settings** in that dialog box.
   - Toggle on **"Allow from this source"** (e.g., allow Chrome, Google Drive, or your Files app to install apps).

3. **Install the App**:
   - Tap **Install** on the prompt.
   - Once complete, tap **Open** to launch the Saitama Training app!

---

## 💡 Troubleshooting

* **App Not Installed (Conflicting Packages)**:
  - If you already have a version of the Saitama Training app installed (e.g. from a different build or testing session), you must uninstall it first before installing a new version. Android will reject updates if the signing keys or version configurations do not match.
* **Gradle Wrapper issues locally**:
  - If running locally and you see gradle issues, ensure your system `JAVA_HOME` points to JDK 11 or higher. Jetpack Compose with modern compiler targets requires JDK 11+.
