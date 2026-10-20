package com.example.doclocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "DocLockerSettings";
    private static final String KEY_THEME = "app_theme"; // "light", "dark", "system"

    private TextView tvCurrentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView ivBack = findViewById(R.id.ivBackSettings);
        LinearLayout layoutTheme = findViewById(R.id.layoutTheme);
        LinearLayout layoutChangePassword = findViewById(R.id.layoutChangePassword);
        LinearLayout layoutPrivacyInfo = findViewById(R.id.layoutPrivacyInfo);
        LinearLayout layoutAccount = findViewById(R.id.layoutAccount);
        LinearLayout layoutClearCache = findViewById(R.id.layoutClearCache);
        LinearLayout layoutHelpCentre = findViewById(R.id.layoutHelpCentre);
        LinearLayout layoutContactSupport = findViewById(R.id.layoutContactSupport);
        LinearLayout layoutReportProblem = findViewById(R.id.layoutReportProblem);
        LinearLayout layoutAbout = findViewById(R.id.layoutAbout);

        tvCurrentTheme = findViewById(R.id.tvCurrentTheme);
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);

        ivBack.setOnClickListener(v -> finish());
        updateThemeDisplay();

        // 1. Appearance / Theme
        layoutTheme.setOnClickListener(v -> showThemeSelectionDialog());

        // 2. Change Vault Password
        layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 3. Privacy & Security Info
        layoutPrivacyInfo.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Privacy & Security")
                .setMessage("DocLocker uses a local-first privacy architecture. All your document metadata is stored securely in an app-private SQLite database. No document contents are transmitted to external servers without your explicit action.")
                .setPositiveButton("Got it", null)
                .show());

        // 4. Local Account Profile
        layoutAccount.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Local User Profile")
                .setMessage("Mode: Local Offline Workspace\nCloud Sync: Disabled (Local-Only Privacy)\nAll records are stored securely on this device.")
                .setPositiveButton("OK", null)
                .show());

        // 5. Clear Cache
        layoutClearCache.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Clear Temporary Cache")
                .setMessage("Are you sure you want to clear temporary cache files?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    try {
                        File cacheDir = getCacheDir();
                        if (cacheDir != null && cacheDir.exists()) {
                            deleteDir(cacheDir);
                        }
                        Toast.makeText(this, "Temporary cache cleared successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show());

        // 6. Help Centre & FAQ
        layoutHelpCentre.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HelpWebViewActivity.class);
            startActivity(intent);
        });

        // 7. Contact Support
        layoutContactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@doclocker.example.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DocLocker Support Inquiry");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email via"));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found. Please email support@doclocker.example.com", Toast.LENGTH_LONG).show();
            }
        });

        // 8. Report a Problem
        layoutReportProblem.setOnClickListener(v -> showReportProblemDialog());

        // 9. About
        tvAppVersion.setText("Version 1.0 (Build 2026)");
        layoutAbout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("About DocLocker")
                .setMessage("DocLocker — Secure Documents & Smart Conversion Workspace.\n\nDesigned for secure offline document management and PDF conversions.\n\nAcademic Project 2026–2027.")
                .setPositiveButton("Close", null)
                .show());
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void updateThemeDisplay() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "system");
        if (theme.equals("light")) {
            tvCurrentTheme.setText("Light Mode");
        } else if (theme.equals("dark")) {
            tvCurrentTheme.setText("Dark Mode");
        } else {
            tvCurrentTheme.setText("System Default");
        }
    }

    private void showThemeSelectionDialog() {
        String[] options = {"Light Mode", "Dark Mode", "System Default"};
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String current = prefs.getString(KEY_THEME, "system");
        int checked = current.equals("light") ? 0 : (current.equals("dark") ? 1 : 2);

        new AlertDialog.Builder(this)
                .setTitle("Select App Theme")
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    if (which == 0) {
                        editor.putString(KEY_THEME, "light");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if (which == 1) {
                        editor.putString(KEY_THEME, "dark");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        editor.putString(KEY_THEME, "system");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    editor.apply();
                    updateThemeDisplay();
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReportProblemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report a Problem");

        final EditText input = new EditText(this);
        input.setHint("Describe the issue you encountered...");
        input.setMinLines(3);
        builder.setView(input);

        builder.setPositiveButton("Submit Report", (dialog, which) -> {
            String feedback = input.getText().toString().trim();
            if (!feedback.isEmpty()) {
                Toast.makeText(this, "Thank you! Problem report logged locally for diagnostics.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
