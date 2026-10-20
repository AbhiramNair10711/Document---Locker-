# Architecture Documentation

DocLocker follows a clean Model-View-Controller / Activity-Repository pattern using native Android Java and XML layouts.

```mermaid
graph TD
    A[MainActivity: Dashboard Hub] --> B[DocLockerActivity: Secure Vault]
    A --> C[PdfConverterActivity: PDF Converter]
    A --> D[HelpWebViewActivity: Help & FAQ]

    B --> F[DatabaseHelper: SQLite Database]
    B --> G[DocumentDetailActivity]
    C --> H[ConversionExecutionActivity]
    H --> F
```

- **Presentation Layer**: Activities (`MainActivity`, `DocLockerActivity`, `PdfConverterActivity`, etc.) and XML layout definitions.
- **Data Layer**: `DatabaseHelper` (SQLiteOpenHelper) managing local tables for documents and conversion history.
- **Platform Integration**: Intents (Explicit & Implicit SAF file picker, camera capture, share) and BroadcastReceiver.
