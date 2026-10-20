package com.example.doclocker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DocLockerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_locker);

        dbHelper = new DatabaseHelper(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewDocs);
        MaterialButton fabAddDoc = findViewById(R.id.fabAddDoc);

        ivBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddDoc.setOnClickListener(v -> showAddDocumentDialog());
        loadDocuments(dbHelper.getAllDocuments());
    }

    private void applySearch(String query) {
        Cursor cursor;
        if (query.isEmpty()) {
            cursor = dbHelper.getAllDocuments();
        } else {
            cursor = dbHelper.searchDocuments(query);
        }
        loadDocuments(cursor);
    }

    private void loadDocuments(Cursor cursor) {
        int count = cursor != null ? cursor.getCount() : 0;
        Log.d("DocLockerVault", "loadDocuments count: " + count);
        DocumentAdapter adapter = new DocumentAdapter(cursor, false, (id, title, category, uri, size, date, expiry) -> {
            Intent intent = new Intent(DocLockerActivity.this, DocumentDetailActivity.class);
            intent.putExtra("DOC_ID", id);
            intent.putExtra("DOC_TITLE", title);
            intent.putExtra("DOC_CATEGORY", category);
            intent.putExtra("DOC_URI", uri);
            intent.putExtra("DOC_SIZE", size);
            intent.putExtra("DOC_DATE", date);
            intent.putExtra("DOC_EXPIRY", expiry);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddDocumentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Document to Vault");
        builder.setMessage("Choose how you want to add your document:");

        builder.setPositiveButton("Browse File (Picker)", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "image/*"
            });
            try {
                startActivityForResult(intent, 100);
            } catch (Exception e) {
                Log.e("DocLockerVault", "Error launching document picker: " + e.getMessage(), e);
                Toast.makeText(this, "Unable to launch document picker.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Sample Document", (dialog, which) -> {
            try {
                Cursor c = dbHelper.searchDocuments("COVID-19 Vaccine Certificate");
                if (c != null && c.getCount() > 0) {
                    c.close();
                    Toast.makeText(this, "Sample document already exists in Vault.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (c != null) c.close();

                File sampleFile = new File(getFilesDir(), "sample_vaccine_" + System.currentTimeMillis() + ".pdf");
                try (FileOutputStream fos = new FileOutputStream(sampleFile)) {
                    fos.write("%PDF-1.4 Sample Vaccine Certificate PDF Content".getBytes());
                }

                long rowId = dbHelper.addDocument("COVID-19 Vaccine Certificate", "Medical", sampleFile.getAbsolutePath(), "1.1 MB", "20 Oct 2023", "Permanent");
                if (rowId != -1) {
                    applySearch("");
                    Toast.makeText(this, "Sample document added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add sample document to database.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DocLockerVault", "Error adding sample document: " + e.getMessage(), e);
                Toast.makeText(this, "Error adding sample document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e("DocLockerVault", "Error getting file name: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
            if (result != null) {
                int idx = result.lastIndexOf(':');
                if (idx != -1) {
                    result = result.substring(idx + 1);
                }
            }
        }
        if (result == null || result.contains("encoded=") || result.contains("acc=")) {
            result = "ImportedDocument.pdf";
        }
        return result;
    }

    private String saveFileToInternalStorage(Uri sourceUri, String fileName) {
        try {
            File myFile = new File(getFilesDir(), System.currentTimeMillis() + "_" + fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
                 FileOutputStream outputStream = new FileOutputStream(myFile)) {
                if (inputStream != null) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                }
            }
            return myFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("DocLockerVault", "Failed to copy file to internal storage: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (Exception ignored) {}

                    String fileName = getFileNameFromUri(uri);
                    String internalPath = saveFileToInternalStorage(uri, fileName);
                    if (internalPath != null) {
                        long rowId = dbHelper.addDocument(fileName, "Personal Documents", internalPath, "2.4 MB", "Today", "2028");
                        if (rowId != -1) {
                            Log.d("DocLockerVault", "Successfully imported document rowId: " + rowId + ", path: " + internalPath);
                            applySearch("");
                            Toast.makeText(this, "Document imported successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to save document record in database.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to copy selected file to internal storage.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Selected file URI is null.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Import cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySearch(etSearch.getText() != null ? etSearch.getText().toString() : "");
    }
}
