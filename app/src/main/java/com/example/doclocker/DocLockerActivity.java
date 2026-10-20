package com.example.doclocker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        FloatingActionButton fabAddDoc = findViewById(R.id.fabAddDoc);

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
            intent.setType("application/pdf");
            startActivityForResult(intent, 100);
        });

        builder.setNeutralButton("Sample Document", (dialog, which) -> {
            dbHelper.addDocument("COVID-19 Vaccine Certificate", "Medical", "content://sample/vaccine.pdf", "1.1 MB", "20 Oct 2023", "Permanent");
            applySearch(etSearch.getText() != null ? etSearch.getText().toString() : "");
            Toast.makeText(this, "Sample document added successfully", Toast.LENGTH_SHORT).show();
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
                // ignore
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
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(myFile);
            if (inputStream != null) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                inputStream.close();
                outputStream.close();
            }
            return myFile.getAbsolutePath();
        } catch (Exception e) {
            return sourceUri.toString();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileNameFromUri(uri);
                String internalPath = saveFileToInternalStorage(uri, fileName);
                dbHelper.addDocument(fileName, "Personal Documents", internalPath, "2.4 MB", "Today", "2028");
                applySearch("");
                Toast.makeText(this, "PDF document imported successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySearch(etSearch.getText() != null ? etSearch.getText().toString() : "");
    }
}
