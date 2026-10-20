package com.example.doclocker;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DocumentDetailActivity extends AppCompatActivity {

    private long docId;
    private String docTitle, docCategory, docUri, docSize, docDate, docExpiry;
    private DatabaseHelper dbHelper;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        dbHelper = new DatabaseHelper(this);

        docId = getIntent().getLongExtra("DOC_ID", -1);
        docTitle = getIntent().getStringExtra("DOC_TITLE");
        docCategory = getIntent().getStringExtra("DOC_CATEGORY");
        docUri = getIntent().getStringExtra("DOC_URI");
        docSize = getIntent().getStringExtra("DOC_SIZE");
        docDate = getIntent().getStringExtra("DOC_DATE");
        docExpiry = getIntent().getStringExtra("DOC_EXPIRY");

        ImageView ivBack = findViewById(R.id.ivBackDetail);
        tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvSize = findViewById(R.id.tvDetailSize);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvExpiry = findViewById(R.id.tvDetailExpiry);

        MaterialButton btnView = findViewById(R.id.btnViewDocument);
        MaterialButton btnRename = findViewById(R.id.btnRenameDocument);
        MaterialButton btnShare = findViewById(R.id.btnShareDocument);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteDocument);

        ivBack.setOnClickListener(v -> finish());

        updateUiValues(tvCategory, tvSize, tvDate, tvExpiry);

        btnView.setOnClickListener(v -> {
            try {
                Uri viewUri;
                if (docUri != null && docUri.startsWith("content://sample/")) {
                    File sampleFile = new File(getCacheDir(), "sample_document.pdf");
                    if (!sampleFile.exists()) {
                        try (FileOutputStream fos = new FileOutputStream(sampleFile)) {
                            fos.write("%PDF-1.4 sample pdf content".getBytes());
                        } catch (Exception ignored) {}
                    }
                    viewUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", sampleFile);
                } else if (docUri != null && docUri.startsWith("/")) {
                    File file = new File(docUri);
                    if (file.exists()) {
                        viewUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
                    } else {
                        viewUri = Uri.parse(docUri);
                    }
                } else if (docUri != null && docUri.startsWith("content://")) {
                    File internalFile = new File(getFilesDir(), System.currentTimeMillis() + "_view.pdf");
                    try (InputStream in = getContentResolver().openInputStream(Uri.parse(docUri));
                         FileOutputStream out = new FileOutputStream(internalFile)) {
                        if (in != null) {
                            byte[] buf = new byte[4096];
                            int len;
                            while ((len = in.read(buf)) != -1) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                    dbHelper.updateDocumentUri(docId, internalFile.getAbsolutePath());
                    docUri = internalFile.getAbsolutePath();
                    viewUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", internalFile);
                } else {
                    viewUri = Uri.parse(docUri);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(viewUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setClipData(ClipData.newUri(getContentResolver(), "PDF Document", viewUri));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No compatible PDF viewer found on your device. Please install Adobe Reader or Google Drive PDF Viewer.", Toast.LENGTH_LONG).show();
            }
        });

        btnRename.setOnClickListener(v -> showRenameDialog());

        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(DocumentDetailActivity.this, ShareReviewActivity.class);
            intent.putExtra("DOC_TITLE", docTitle);
            intent.putExtra("DOC_CATEGORY", docCategory);
            intent.putExtra("DOC_URI", docUri);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Document")
                .setMessage("Are you sure you want to delete '" + docTitle + "' from your secure vault?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteDocument(docId);
                    Toast.makeText(this, "Document deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show());
    }

    private void updateUiValues(TextView tvCategory, TextView tvSize, TextView tvDate, TextView tvExpiry) {
        tvTitle.setText(docTitle);
        tvCategory.setText("Category: " + docCategory);
        tvSize.setText("File Size: " + docSize);
        tvDate.setText("Added Date: " + docDate);
        tvExpiry.setText("Expiry Date: " + docExpiry);
    }

    private void showRenameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Document");

        final EditText input = new EditText(this);
        input.setText(docTitle);
        input.setSelection(docTitle.length());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                dbHelper.updateDocumentTitle(docId, newName);
                docTitle = newName;
                tvTitle.setText(docTitle);
                Toast.makeText(this, "Document renamed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Document name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
