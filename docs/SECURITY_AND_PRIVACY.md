# Security and Privacy Design

1. **Local-First Storage**: All document metadata and conversion records are stored locally using SQLite (`DatabaseHelper`). No document contents are sent to external cloud servers by default.
2. **Storage Access Framework (SAF)**: Uses standard system file pickers (`ACTION_GET_CONTENT`) without requiring broad file system permissions.
3. **Implicit Intents & Sharing**: External sharing uses Android content URIs and secure chooser intents (`ACTION_SEND`).
4. **Data Minimization**: Audit events and conversion history store only non-sensitive metadata (file names and status) rather than file payloads.
