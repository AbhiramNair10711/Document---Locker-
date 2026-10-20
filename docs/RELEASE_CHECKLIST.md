# Release Checklist

1. **Application ID**: `com.example.doclocker`
2. **Versioning**: Version Code `1`, Version Name `1.0`.
3. **Permissions Review**: Uses standard system intents (`ACTION_GET_CONTENT`, `ACTION_IMAGE_CAPTURE`) requiring minimal runtime permissions.
4. **Signing**: Configure release keystore in `app/build.gradle.kts` for production signing.
5. **Build Verification**: Run `gradle_build("app:assembleDebug")` to ensure successful compilation.
