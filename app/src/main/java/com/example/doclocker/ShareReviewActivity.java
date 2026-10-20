package com.example.doclocker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import java.io.File;

public class ShareReviewActivity extends AppCompatActivity {

    private String docTitle, docCategory, docUri;
    private CheckBox cb1, cb2, cb3;
    private MaterialButton btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_review);

        docTitle = getIntent().getStringExtra("DOC_TITLE");
        docCategory = getIntent().getStringExtra("DOC_CATEGORY");
        docUri = getIntent().getStringExtra("DOC_URI");

        ImageView ivBack = findViewById(R.id.ivBackShare);
        TextView tvName = findViewById(R.id.tvShareDocName);
        TextView tvCategory = findViewById(R.id.tvShareDocCategory);

        cb1 = findViewById(R.id.cbCheck1);
        cb2 = findViewById(R.id.cbCheck2);
        cb3 = findViewById(R.id.cbCheck3);
        btnProceed = findViewById(R.id.btnProceedShare);

        ivBack.setOnClickListener(v -> finish());
        tvName.setText(docTitle != null ? docTitle : "Document");
        tvCategory.setText("Category: " + (docCategory != null ? docCategory : "General"));

        View.OnClickListener checkListener = v -> {
            boolean allChecked = cb1.isChecked() && cb2.isChecked() && cb3.isChecked();
            btnProceed.setEnabled(allChecked);
        };

        cb1.setOnClickListener(checkListener);
        cb2.setOnClickListener(checkListener);
        cb3.setOnClickListener(checkListener);

        btnProceed.setOnClickListener(v -> executeSecureShare());
    }

    private void executeSecureShare() {
        try {
            Uri shareUri;
            if (docUri != null && docUri.startsWith("/")) {
                File file = new File(docUri);
                shareUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            } else {
                shareUri = Uri.parse(docUri);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Document: " + docTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Securely shared from DocLocker: " + docTitle);
            startActivity(Intent.createChooser(shareIntent, "Share Document via"));
        } catch (Exception e) {
            Toast.makeText(this, "Unable to share document securely", Toast.LENGTH_SHORT).show();
        }
    }
}
