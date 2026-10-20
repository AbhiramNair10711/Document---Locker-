# Test Plan

1. **Dashboard Navigation**: Verify that tapping **DocLocker** opens `DocLockerActivity` and tapping **PDF Converter** opens `PdfConverterActivity`.
2. **Vault CRUD Operations**:
   - Verify sample documents appear in `DocLockerActivity`.
   - Verify searching updates the list in real-time.
   - Verify adding a document via file picker or sample button updates the SQLite database.
   - Verify deleting a document removes it from the database and updates the UI.
3. **PDF Converter Workflow**:
   - Verify selecting a tool opens `ConversionExecutionActivity`.
   - Verify picking a file enables the "Start Conversion" button.
   - Verify conversion progress and success message.
   - Verify saving the converted result adds it to My Vault.
4. **Course Demos**:
   - Verify camera intent launches successfully.
   - Verify DatePicker correctly sets expiry date.
   - Verify BroadcastReceiver triggers toast notification on security broadcast event.
