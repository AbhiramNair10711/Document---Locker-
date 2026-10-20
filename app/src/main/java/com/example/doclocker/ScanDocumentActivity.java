package com.example.doclocker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanDocumentActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private TextView tvPageCount;
    private TextInputEditText etTitle;
    private Spinner spinnerCategory;
    private MaterialButton btnSavePdf;
    private final List<Bitmap> capturedPages = new ArrayList<>();
    private DatabaseHelper dbHelper;

    private final String[] scanCategories = {
            "Personal Documents",
            "Education",
            "Bank Documents",
            "Driving Licence",
            "Identity Documents",
            "Travel and Visa",
            "Medical Documents",
            "Insurance",
            "Employment",
            "Certificates",
            "Tax and Government",
            "Property",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);

        dbHelper = new DatabaseHelper(this);

        ImageView ivBack = findViewById(R.id.ivBackScan);
        ivPreview = findViewById(R.id.ivScanPreview);
        tvPageCount = findViewById(R.id.tvPageCount);
        etTitle = findViewById(R.id.etScanTitle);
        spinnerCategory = findViewById(R.id.spinnerScanCategory);
        MaterialButton btnCapture = findViewById(R.id.btnCapturePage);
        btnSavePdf = findViewById(R.id.btnSaveScannedPdf);

        ivBack.setOnClickListener(v -> finish());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scanCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnCapture.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(intent, 300);
            } catch (Exception e) {
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType("image/*");
                startActivityForResult(pickIntent, 301);
            }
        });

        btnSavePdf.setOnClickListener(v -> generateAndSavePdf());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 300 && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    capturedPages.add(bitmap);
                    ivPreview.setImageBitmap(bitmap);
                    tvPageCount.setText("Pages Captured: " + capturedPages.size());
                    btnSavePdf.setEnabled(true);
                    Toast.makeText(this, "Page captured successfully", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 301 && data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    if (bitmap != null) {
                        capturedPages.add(bitmap);
                        ivPreview.setImageBitmap(bitmap);
                        tvPageCount.setText("Pages Captured: " + capturedPages.size());
                        btnSavePdf.setEnabled(true);
                        Toast.makeText(this, "Page added from gallery", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void generateAndSavePdf() {
        if (capturedPages.isEmpty()) {
            Toast.makeText(this, "Please capture at least one page", Toast.LENGTH_SHORT).show();
            return;
        }

        String rawTitle = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (rawTitle.isEmpty()) {
            rawTitle = "Scanned_Document_" + System.currentTimeMillis();
        }
        final String title = rawTitle.toLowerCase().endsWith(".pdf") ? rawTitle : rawTitle + ".pdf";

        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Personal Documents";

        try {
            PdfDocument pdfDocument = new PdfDocument();
            for (int i = 0; i < capturedPages.size(); i++) {
                Bitmap bmp = capturedPages.get(i);
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bmp.getWidth(), bmp.getHeight(), i + 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                page.getCanvas().drawBitmap(bmp, 0, 0, null);
                pdfDocument.finishPage(page);
            }

            File pdfFile = new File(getFilesDir(), System.currentTimeMillis() + "_scanned.pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            dbHelper.addDocument(title, category, pdfFile.getAbsolutePath(), "1.8 MB", dateStr, "Permanent", "Scan", category, 0);

            Toast.makeText(this, "Scanned PDF saved to vault successfully!", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
