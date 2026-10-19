# DocLocker — Secure Document Manager & File Converter

**DocLocker** is an Android-focused mobile application project designed to help users organize important documents, protect access to private files, and convert supported file formats from one convenient interface.

> **Project status:** This repository is being developed as a Mobile Application Development academic project for the 2026–2027 academic year. The features below describe the project goals. Update the implementation status as features are built and tested; planned features should not be presented as already available.

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Objectives](#objectives)
- [Features](#features)
- [User Interface](#user-interface)
- [Technology Stack](#technology-stack)
- [Course Policy Alignment](#course-policy-alignment)
- [Getting Started](#getting-started)
- [Configuration Notes](#configuration-notes)
- [Privacy and Security](#privacy-and-security)
- [Testing](#testing)
- [Limitations](#limitations)
- [Development Roadmap](#development-roadmap)
- [Contributing](#contributing)
- [Academic Integrity](#academic-integrity)
- [License](#license)

## Overview

Important documents can be spread across folders, email attachments, messaging applications, and other storage locations. Finding the correct file, protecting private documents, and converting files between formats can become inconvenient.

DocLocker aims to bring these everyday tasks together in one mobile application, with two main areas:

- **My Vault:** import, organize, search, rename, open, and manage documents.
- **PDF & File Converter:** convert supported files and download, save, or share the resulting output.

A dedicated Settings section is planned for appearance preferences, security options, Help & Support, account-related options where supported, and application information.

## Problem Statement

Users often need to manage identity documents, certificates, financial records, work files, and other important documents on their phones. Separate apps for storage, viewing, and conversion can make the process fragmented. DocLocker explores a simpler workflow for document organization, protected access, file viewing, and conversion.

## Objectives

- Build a user-friendly Android application with a colourful, consistent interface.
- Provide a document vault with search, categories, and rename functionality.
- Protect vault access using a PIN or password, with optional biometric unlock where supported.
- Store document metadata persistently and handle files safely.
- Make documents saved in the vault accessible for viewing or opening in compatible applications.
- Provide genuine file conversion for supported formats.
- Allow converted files to be downloaded to the device independently of saving them to the vault.
- Provide working Light Mode, Dark Mode, and System Default preferences.
- Include practical Settings, Help & Support, Privacy, and About screens.
- Demonstrate relevant Mobile Application Development course concepts and document their implementation.

## Features

### 1. Home Dashboard

The planned dashboard includes:
- DocLocker branding and a profile/settings shortcut.
- Actual document count and storage information, when available.
- Prominent My Vault and PDF & File Converter cards.
- Quick actions such as Import Document and Convert File.
- Recently accessed or added documents.
- Useful empty states for new users.

Dashboard statistics and recent files should come from real application data rather than hard-coded examples.

### 2. My Vault

Planned functionality includes:
- Importing files through the Android system document picker.
- Organizing documents by categories.
- Searching and filtering the document list.
- Renaming documents without changing their actual file format.
- Viewing file names, formats, sizes, and relevant dates.
- Opening or previewing supported documents.
- Exporting, sharing, or deleting documents with appropriate confirmation.
- Persisting document metadata across application restarts.

### 3. Vault Security

The intended security features include:
- PIN or password setup and vault unlocking.
- Optional biometric authentication where supported by the device.
- Automatic vault locking.
- Secure handling of private files and temporary decrypted copies.
- Clear privacy information and appropriate warnings before exporting files.

The final security design must use established platform APIs and cryptographic methods where encryption is implemented. The app must not store passwords in plain text or claim to be professionally audited.

### 4. PDF & File Converter

The converter is intended to support formats such as:
- DOC/DOCX to PDF
- PDF to DOCX/Word
- PPT/PPTX to PDF
- TXT to PDF
- CSV to PDF, where supported
- Images to PDF, where supported

The actual list of supported conversions depends on the conversion engine implemented in the project. Some formats may require an external library or a configured backend.

After a successful conversion, the intended result actions are:
- **Download to Device** — save the converted output to device storage.
- **Save to My Vault** — store the output in the document vault.
- **Share** — share the output through a compatible Android application.

Changing a filename extension is not a real conversion. The app should report success only after the output file has actually been generated and saved or made available.

### 5. Settings, Appearance, and Help

The planned Settings section includes:
- Light Mode, Dark Mode, and System Default.
- Vault PIN/password and biometric preferences, where implemented.
- Automatic vault-lock preferences.
- Help Centre and frequently asked questions.
- Contact Support and Report a Problem, using real configured destinations or honest fallback instructions.
- Privacy & Security information.
- Account options only if genuine account functionality exists.
- About DocLocker and application version information.

The app should work in local-only mode without requiring users to create an account. It must not display fake sign-in or cloud-sync functionality.

## User Interface

DocLocker aims for a colourful, modern visual style using a consistent primary colour such as indigo or royal blue, with coordinated purple, teal, and coral accents.

Design goals:
- Clear visual hierarchy and readable typography.
- Colourful file-type icons and useful dashboard cards.
- Consistent spacing, rounded cards, and restrained shadows.
- Accessible contrast and touch targets.
- Responsive layouts for different Android screen sizes.
- Consistent styling across Home, My Vault, Converter, Settings, and Help screens.

The interface should be visually rich without unnecessary menus or unrelated features.

## Technology Stack

The final technology choices depend on the actual project configuration:
- **IDE:** Android Studio
- **Primary UI:** Flutter/Dart if the existing project uses Flutter
- **Native Android integration:** Java where needed for platform-specific functionality
- **Local persistence:** SQLite or a compatible database layer
- **File access:** Android system document picker / Storage Access Framework
- **Security:** Android-supported key management and established cryptographic APIs/libraries, where implemented
- **Optional services:** Conversion backend or account service only when configured and documented

Use dependency versions compatible with the existing project. Do not commit API keys, passwords, signing credentials, or private test documents.

## Course Policy Alignment

DocLocker is intended to demonstrate relevant concepts from the Mobile Application Development course policy. Actual implementation evidence should be maintained in a separate course mapping document as development progresses.

| Course topic | Intended project application |
|---|---|
| UI design and layouts | Dashboard, vault, converter, settings, and responsive controls |
| Multiscreen navigation | Navigation between document and settings workflows |
| File handling | Importing, opening, renaming, exporting, and managing files |
| Images and text/CSV files | Image display and supported text/CSV operations |
| Database connectivity | Persistent document metadata and CRUD operations |
| Intents | File selection, camera integration if implemented, and sharing |
| Date/time controls | Only where a genuine document-management feature needs them |
| WebView | Help/privacy content if implemented and appropriate |
| Flutter/native integration | As required by the actual project architecture |
| Application publishing | Release preparation and signing documentation |

Do not claim that a topic is implemented until its code and behaviour have been verified. Course-specific evidence should not clutter the regular user interface.

## Getting Started

### Prerequisites

Install or configure the tools required by the actual project:
- Android Studio
- A compatible Android SDK
- Android Emulator or a supported physical Android device
- Git
- Flutter SDK and Dart, if the project uses Flutter
- A compatible JDK and Gradle toolchain, if required by the Android project

### Clone the Repository

After creating the repository on GitHub, clone it using the repository URL:

```bash
git clone https://github.com/<your-username>/<repository-name>.git
cd <repository-name>
```

Replace the placeholders with your GitHub username and repository name.

### Open the Project

1. Launch Android Studio.
2. Select **Open** and choose the cloned project directory.
3. Allow dependencies to synchronize.
4. Resolve SDK or toolchain prompts using versions compatible with the project.
5. Start an emulator through Device Manager or connect a physical Android device.
6. Run the app using Android Studio.

If the project uses Flutter, run these commands from the Flutter project directory:

```bash
flutter doctor
flutter pub get
flutter devices
flutter run
```

If the project is native Android, synchronize Gradle and use Android Studio's Run action. Follow any additional setup instructions in the project.

## Configuration Notes

Some features may depend on additional setup:
- **Biometrics:** availability depends on device hardware, Android version, and enrollment.
- **File viewing:** some office document formats may require a compatible installed application or conversion to PDF.
- **Conversion engine:** certain conversions may require a local library or a configured remote service.
- **Remote conversion:** disclose what is transmitted and obtain user consent before uploading a document.
- **Account or cloud services:** include only if genuinely implemented and configured.

If a feature is unavailable, show a clear explanation rather than pretending it succeeded.

## Privacy and Security

DocLocker is intended to handle potentially sensitive personal documents. Its implementation should follow these principles:
- Request permissions only when necessary.
- Prefer the system document picker over broad storage access.
- Do not store passwords or PINs in plain text.
- Use established cryptographic methods if document encryption is implemented.
- Protect encryption keys using supported platform mechanisms where feasible.
- Avoid logging document contents, passwords, or API keys.
- Do not upload files without clear disclosure and user consent.
- Handle temporary files carefully.
- Confirm destructive actions and warn before exporting private files outside the vault.
- Keep optional account, cloud, and location features disabled unless implemented and explicitly enabled.

**Security disclaimer:** DocLocker is an academic project, not a professionally audited or certified secure-storage product. Its actual security depends on the implemented controls, dependencies, device configuration, and user actions.

## Testing

Testing should be performed against the actual implemented features. Do not mark tests as passed unless they have been run.

Recommended checks:
- Importing a file and opening it after restarting the app.
- Renaming a document and confirming the new name persists.
- Handling missing, corrupted, and unsupported files gracefully.
- Testing vault authentication and biometric fallback.
- Confirming theme preferences persist after restarting the app.
- Testing genuine supported conversions.
- Downloading converted output and opening it in a compatible application.
- Confirming database records persist across restarts.
- Testing permission denial, cancellation, and unavailable services.
- Checking that sensitive content is not exposed in logs.

For a Flutter project, use the checks supported by the project:

```bash
flutter analyze
flutter test
```

For native Android projects, use Android Studio's test runner and the appropriate Gradle test tasks.

## Limitations

The final limitations depend on the implemented version. Potential constraints include:
- Conversion support and formatting fidelity vary by engine.
- PDF-to-Word conversion may not preserve complex layouts.
- Some office document types may require an external viewer.
- Biometric functionality depends on the device.
- Remote services require configuration and privacy review.
- A local vault is not automatically a backup; loss of app data or encryption keys may make files unrecoverable if no recovery mechanism exists.
- Market-wide uniqueness cannot be guaranteed without separate research.

Update this section as the project is implemented and tested.

## Development Roadmap

1. Inspect the existing project and confirm the architecture.
2. Improve DocLocker branding, colour scheme, and home dashboard.
3. Implement and verify document import, opening, renaming, and persistence.
4. Implement vault authentication and biometric support where available.
5. Implement genuine supported conversions and download-to-device functionality.
6. Add working theme preferences and relevant Settings/Help screens.
7. Test the application on an emulator and, where possible, a physical device.
8. Document course-policy coverage, limitations, and release preparation.

## Contributing

For team development:
1. Discuss interfaces and feature ownership before editing shared files.
2. Create a branch for each feature.
3. Commit small, understandable changes.
4. Test changes before merging them.
5. Do not commit API keys, passwords, signing credentials, private documents, or generated build files.
6. Keep this README accurate as functionality changes.

Example workflow:

```bash
git checkout -b feature/settings-screen
git add .
git commit -m "Improve settings screen"
git push -u origin feature/settings-screen
```

## Academic Integrity

This project is intended for academic evaluation. Team members should understand the submitted code, follow institutional requirements for AI-assisted development, acknowledge third-party libraries and references where required, and be prepared to explain design and implementation decisions.

## License

No license has been selected yet. Until a license is added, do not assume that other people have permission to reuse, distribute, or modify this repository.
