<div align="center">
  <img src="app/src/main/res/drawable/logo.png" width="120" alt="Tsiamba Logo">
  <h1>Tsiamba</h1>
  <h3>Secure and lightweight password manager for Android</h3>
  <p><strong>A fast, offline, and secure way to manage your passwords with biometric authentication.</strong></p>
  <p>
    <img src="https://img.shields.io/badge/Kotlin-1.9.24-blue.svg" alt="Kotlin">
    <img src="https://img.shields.io/badge/Jetpack%20Compose-1.6.8-green.svg" alt="Compose">
    <img src="https://img.shields.io/badge/Android-API%2023%2B-brightgreen.svg" alt="Android">
    <img src="https://img.shields.io/badge/License-MIT-lightgrey.svg" alt="License">
  </p>
</div>

---

Tsiamba is a modern, offline-first password manager designed for Android. Built with Jetpack Compose, it focuses on speed, security, and a minimal footprint. It provides a seamless user experience with native biometric authentication and strong AES-256 encryption to keep your credentials safe.

## Overview

Tsiamba allows you to store, organize, and retrieve your passwords securely. It uses the Android Keystore system to encrypt your data at rest, ensuring that even if your device is compromised, your passwords remain unreadable. The application supports multiple languages (French, English, Malagasy) and adapts to your system's light or dark theme.

## Features

### Security and Authentication
- **AES-256-GCM Encryption**: All passwords and metadata are encrypted using the Android Keystore system.
- **Biometric Unlock**: Secure your vault with fingerprint, face unlock, or device PIN using the native KeyguardManager.
- **FLAG_SECURE Toggle**: Optionally allow or block screenshots and preview in recent apps from the settings.

### Password Management
- **Add and Edit Entries**: Store the site name, username (or email/phone), password, and notes.
- **Custom Categories**: Choose from a predefined list of popular services or create custom entries.
- **Search and Sort**: Quickly find your passwords by filtering or sorting them alphabetically or by date.

### Data Portability
- **Encrypted JSON Export/Import**: Backup your vault or transfer it to another device with AES-256 encrypted JSON files.

### User Interface
- **Material 3 Design**: A clean, modern interface that follows the latest Android design guidelines.
- **Multi-language Support**: Available in French, English, and Malagasy.
- **Light and Dark Themes**: Toggle between themes or follow the system settings.

## Technology Stack

| Technology | Purpose |
| :--- | :--- |
| Kotlin | Primary programming language |
| Jetpack Compose | UI toolkit for building native Android interfaces |
| AndroidX Lifecycle | Lifecycle-aware components for Compose |
| Android Keystore | Secure storage for encryption keys |
| Material 3 | Design system and components |
| R8 | Code shrinking and obfuscation for release builds |

## Project Structure

<pre>
Tsiamba/
├── app/
│   ├── src/main/java/com/tsiambajojoh/mg/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── AuthentificationBiometrique.kt
│   │   │   ├── Chiffrement.kt
│   │   │   ├── Langue.kt
│   │   │   ├── MotDePasse.kt
│   │   │   ├── MotDePasseRepository.kt
│   │   │   ├── Preferences.kt
│   │   │   ├── Site.kt
│   │   │   └── Textes.kt
│   │   └── ui/
│   │       ├── screens/
│   │       │   ├── DialogueConfirm.kt
│   │       │   ├── EcranAccueil.kt
│   │       │   ├── EcranAjout.kt
│   │       │   ├── EcranDetail.kt
│   │       │   ├── EcranParametres.kt
│   │       │   ├── EcranVerrou.kt
│   │       │   └── IconeDrawableFallback.kt
│   │       └── theme/
│   │           ├── Color.kt
│   │           ├── Theme.kt
│   │           └── Type.kt
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── LICENSE.md
</pre>

## Getting Started

### Prerequisites
- Android Studio (optional, but recommended for IDE support)
- JDK 17 or higher
- Android SDK (API 34)
- Gradle 8.7 or higher (handled by the wrapper)

### Clone the Repository
    git clone https://github.com/Johanes-mg/Tsiamba.git
    cd Tsiamba

### Build
    ./gradlew assembleDebug

### Run
    ./gradlew installDebug
    adb shell am start -n com.tsiambajojoh.mg/.MainActivity

## Design Philosophy

- **Security First**: Encryption and secure authentication are the core of Tsiamba.
- **Offline by Default**: Your data never leaves your device unless you explicitly export it.
- **Lightweight**: Built with minimal dependencies to ensure fast startup and low memory usage.
- **User-Centric**: Intuitive navigation and clear visual feedback.

## Data and Persistence

Tsiamba uses SharedPreferences to store encrypted JSON data. The encryption key is generated and stored in the Android Keystore, ensuring it is hardware-backed and cannot be extracted. 

**Recommendation**: Regularly export your vault to an encrypted JSON file and keep it in a safe place. This will help you recover your data if you switch devices or reset your phone.

## Performance

- **R8 Full Mode**: Enabled for release builds to minimize APK size and improve runtime performance.
- **Baseline Profiles**: Included by default via AndroidX libraries for faster startup.
- **Resource Shrinking**: Removes unused resources in release builds.
- **Optimized Memory Usage**: Uses SnapshotStateList and primitive types to reduce garbage collection.

## Roadmap

- [ ] Add support for password generation.
- [ ] Implement a password strength indicator.
- [ ] Add a "Copy to Clipboard" button with auto-clear.
- [ ] Support for cloud sync (optional).

## Contributing

Contributions are welcome! If you find a bug or have a feature request, please open an issue.

1. Fork the repository.
2. Create a new branch (git checkout -b feature/your-feature).
3. Commit your changes (git commit -m 'Add some feature').
4. Push to the branch (git push origin feature/your-feature).
5. Open a Pull Request.

## Author

**Johanes-mg**

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.

<div align="center">
  <p><strong>Tsiamba - Keep your passwords safe, everywhere.</strong></p>
  <p><em>Secure, lightweight, and always offline.</em></p>
</div>
