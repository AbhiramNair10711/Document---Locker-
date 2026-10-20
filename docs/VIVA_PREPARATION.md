# Viva Preparation Guide

### Key Concepts to Explain During Your Viva:
1. **Why Java and XML?**
   - We used native Android Java and XML layouts (`ConstraintLayout`, `RecyclerView`, `CardView`) to fulfill the native mobile application development requirements, ensuring direct integration with Android Jetpack components and SQLite.

2. **How is data persisted?**
   - We use SQLite via `DatabaseHelper` (`SQLiteOpenHelper`) to store secure document metadata (title, category, URI, file size, date, expiry) and conversion history.

3. **How do Intents work?**
   - **Explicit Intents** are used to navigate between activities (e.g., `MainActivity` to `DocLockerActivity` or `PdfConverterActivity`).
   - **Implicit Intents** are used for system file picking (`ACTION_GET_CONTENT`), camera capture (`ACTION_IMAGE_CAPTURE`), and sharing documents (`ACTION_SEND`).

4. **How are Course Requirements Met?**
   - We implemented a WebView loading local HTML help/privacy documentation (`help.html`), a DatePicker for expiry dates, an implicit camera intent, and a custom BroadcastReceiver (`SecurityAuditReceiver`) for security event notifications.
   - We mapped every requirement in `docs/COURSE_POLICY_MAPPING.md`.
