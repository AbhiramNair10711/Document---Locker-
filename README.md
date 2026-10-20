# DocLocker — Secure Documents & Smart Conversion

DocLocker is a polished, practical mobile application for Android built using **Java** and **XML layouts**, combining a **Secure Document Locker** (inspired by DigiLocker) and a **Smart File Converter workspace** (inspired by iLovePDF).

---

## Features

1. **Dashboard Hub (`MainActivity`)**:
   - Central options menu allowing the user to choose between **My Vault (DocLocker)** and **PDF & File Converter**.
   - Consistent modern color scheme (Indigo/Purple #5032F0).
   - Quick access to Help & FAQ.

2. **Secure Document Locker (`DocLockerActivity`)**:
   - DigiLocker-inspired secure document manager.
   - Search functionality across documents and categories.
   - SQLite database persistence for document metadata (Aadhaar, Driving License, PAN Card, etc.).
   - File importing via System Document Picker (Storage Access Framework) and sample document addition.
   - Detailed document view (`DocumentDetailActivity`) with preview, document rename, share intent, and secure deletion.

3. **PDF Converter Workspace (`PdfConverterActivity`)**:
   - iLovePDF-inspired clean tool selection interface.
   - Tools: Merge PDF, Split PDF, Compress PDF, PDF to Word, Word to PDF, PDF to PPT, PDF to Excel, Edit PDF.
   - Conversion execution (`ConversionExecutionActivity`) with simulated progress and options to **Download to Device**, **Save Result to My Vault**, and **Share Converted File**.

---

## Architecture & Tech Stack
- **Language**: Java (Native Android)
- **UI**: XML Layouts, Material 3 Design Components (`ConstraintLayout`, `CardView`, `RecyclerView`, `CoordinatorLayout`)
- **Database**: SQLite (`DatabaseHelper`)
- **Intents**: Explicit navigation between activities; Implicit intents for camera capture, file picker, and sharing.

---

## Running the App
1. Open the project in Android Studio.
2. Ensure an Android Emulator (API 27+) or physical device is connected.
3. Run the `app:assembleDebug` build or click **Run** in Android Studio.
