# Course Policy Mapping

| Course Outcome / Topic | Implementation File / Screen | Description |
| :--- | :--- | :--- |
| **CO1: UI Design & Android Studio** | `activity_main.xml`, `activity_doc_locker.xml`, `activity_pdf_converter.xml` | Material 3 layouts, ConstraintLayout, CardView, RecyclerView, RecyclerView adapters, and custom color scheme. |
| **CO2: File Handling (Text/Images)** | `DocLockerActivity.java`, `ConversionExecutionActivity.java` | System document picker (Storage Access Framework), file URIs, MIME types, and secure file handling. |
| **CO3: Database & Persistence** | `DatabaseHelper.java` | SQLite database storing document metadata (CRUD operations) and conversion history. |
| **CO4: Full Application & Intents** | `MainActivity.java`, `DocumentDetailActivity.java`, `CourseDemoActivity.java` | Explicit activity navigation, implicit intents (camera capture, file picker, sharing), WebView help page, DatePicker, and BroadcastReceiver. |

---

## Ten Laboratory Exercise Concepts
1. **Android Studio installation and emulator configuration**: Configured and tested on Android emulator (API 27+).
2. **General UI design and interactive controls**: Built using Material 3 buttons, cards, search bars, and adapters.
3. **Explicit and implicit intents**: Implemented for activity transitions, file picking (`ACTION_GET_CONTENT`), camera capture (`ACTION_IMAGE_CAPTURE`), and sharing (`ACTION_SEND`).
4. **ListView / RecyclerView with adapters**: `DocumentAdapter` and `ConversionToolAdapter` populate lists and grids.
5. **WebView**: `HelpWebViewActivity` loads local `help.html` asset.
6. **Time and date pickers**: `DatePickerDialog` used in `CourseDemoActivity` for document expiry dates.
7. **Database operations**: SQLite `DatabaseHelper` handles `INSERT`, `SELECT`, `DELETE` operations.
8. **Camera intent**: `MediaStore.ACTION_IMAGE_CAPTURE` invoked in `CourseDemoActivity`.
9. **Broadcast sender/receiver**: Custom `SecurityAuditReceiver` registers and listens for internal security audit broadcasts.
