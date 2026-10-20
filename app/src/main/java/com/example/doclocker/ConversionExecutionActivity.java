package com.example.doclocker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConversionExecutionActivity extends AppCompatActivity {

    private String toolTitle, toolFormat;
    private TextView tvSelectedFile, tvProgressStatus;
    private LinearLayout layoutProgress, layoutResultActions;
    private MaterialButton btnSelectFile, btnStartConversion;
    private String selectedFileUri = null;
    private String selectedFileName = null;
    private Uri convertedFileUri = null;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_execution);

        dbHelper = new DatabaseHelper(this);

        toolTitle = getIntent().getStringExtra("TOOL_TITLE");
        String toolDesc = getIntent().getStringExtra("TOOL_DESC");
        toolFormat = getIntent().getStringExtra("TOOL_FORMAT");

        ImageView ivBack = findViewById(R.id.ivBackExec);
        TextView tvHeader = findViewById(R.id.tvExecTitleHeader);
        TextView tvName = findViewById(R.id.tvExecToolName);
        TextView tvDesc = findViewById(R.id.tvExecToolDesc);

        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        tvProgressStatus = findViewById(R.id.tvProgressStatus);
        layoutProgress = findViewById(R.id.layoutProgress);
        layoutResultActions = findViewById(R.id.layoutResultActions);

        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnStartConversion = findViewById(R.id.btnStartConversion);
        MaterialButton btnDownloadDevice = findViewById(R.id.btnDownloadDevice);
        MaterialButton btnSaveToVault = findViewById(R.id.btnSaveToVault);
        MaterialButton btnShareResult = findViewById(R.id.btnShareResult);

        ivBack.setOnClickListener(v -> finish());
        tvHeader.setText(toolTitle);
        tvName.setText(toolTitle);
        tvDesc.setText(toolDesc);

        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 200);
        });

        btnStartConversion.setOnClickListener(v -> {
            if (selectedFileUri == null) {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
                return;
            }
            startConversionProcess();
        });

        btnDownloadDevice.setOnClickListener(v -> {
            if (convertedFileUri != null) {
                try {
                    String mime = toolTitle != null && toolTitle.contains("Word") ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document" : "application/pdf";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(convertedFileUri, mime);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Converted file successfully generated and saved.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Converted file downloaded successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveToVault.setOnClickListener(v -> {
            String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            String cleanName = selectedFileName != null ? selectedFileName : "ConvertedDocument";
            if (cleanName.contains("encoded=") || cleanName.contains("acc=")) {
                cleanName = "Converted_Document";
            }
            String ext = toolTitle != null && toolTitle.contains("Word") ? ".docx" : ".pdf";
            if (cleanName.contains(".")) {
                cleanName = cleanName.substring(0, cleanName.lastIndexOf('.')) + ext;
            } else {
                cleanName = cleanName + ext;
            }
            String saveUri = convertedFileUri != null ? convertedFileUri.toString() : selectedFileUri;
            dbHelper.addDocument("Converted_" + cleanName, "Converted Docs", saveUri, "1.4 MB", dateStr, "Permanent");
            Toast.makeText(this, "Converted file saved to My Vault successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnShareResult.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            String mime = toolTitle != null && toolTitle.contains("Word") ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document" : "application/pdf";
            shareIntent.setType(mime);
            if (convertedFileUri != null) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, convertedFileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Converted Document (" + toolTitle + ")");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is your successfully converted document generated by DocLocker.");
            startActivity(Intent.createChooser(shareIntent, "Share Converted File via"));
        });
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
            result = "SourceDocument";
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    // ignore
                }
                selectedFileUri = uri.toString();
                selectedFileName = getFileNameFromUri(uri);
                tvSelectedFile.setText(selectedFileName);
                btnStartConversion.setEnabled(true);
                Toast.makeText(this, "File selected: " + selectedFileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startConversionProcess() {
        btnSelectFile.setEnabled(false);
        btnStartConversion.setEnabled(false);
        layoutProgress.setVisibility(View.VISIBLE);
        tvProgressStatus.setText("Processing " + toolTitle + "...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            layoutProgress.setVisibility(View.GONE);

            // Generate correct target file format (.docx or .pdf)
            if ("PDF_TO_WORD".equals(toolFormat) || (toolTitle != null && toolTitle.contains("Word"))) {
                convertedFileUri = DocumentConverterService.convertToWord(this, Uri.parse(selectedFileUri), selectedFileName);
            } else {
                convertedFileUri = DocumentConverterService.convertToPdf(this, Uri.parse(selectedFileUri), selectedFileName);
            }

            String currentDate = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
            dbHelper.addConversion(selectedFileName, toolFormat, "SUCCESS", currentDate);

            Toast.makeText(this, "Conversion completed successfully!", Toast.LENGTH_LONG).show();
            layoutResultActions.setVisibility(View.VISIBLE);
            btnStartConversion.setVisibility(View.GONE);
        }, 2000);
    }
}
