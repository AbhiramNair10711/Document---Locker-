package com.example.doclocker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ChecklistDetailActivity extends AppCompatActivity {

    private long checklistId;
    private DatabaseHelper dbHelper;
    private TextView tvName, tvProgress;
    private ProgressBar progressBar;
    private LinearLayout containerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_detail);

        dbHelper = new DatabaseHelper(this);

        checklistId = getIntent().getLongExtra("CHECKLIST_ID", -1);
        String checklistName = getIntent().getStringExtra("CHECKLIST_NAME");

        ImageView ivBack = findViewById(R.id.ivBackChkDetail);
        tvName = findViewById(R.id.tvChkDetailName);
        tvProgress = findViewById(R.id.tvChkDetailProgress);
        progressBar = findViewById(R.id.progressBarChk);
        containerItems = findViewById(R.id.containerChkDetailItems);
        MaterialButton btnAddReq = findViewById(R.id.btnAddChkRequirement);

        ivBack.setOnClickListener(v -> finish());
        tvName.setText(checklistName != null ? checklistName : "Checklist");

        btnAddReq.setOnClickListener(v -> showAddRequirementDialog());

        loadChecklistDetails();
    }

    private void loadChecklistDetails() {
        containerItems.removeAllViews();
        Cursor cursor = dbHelper.getChecklistItems(checklistId);

        int total = 0;
        int completed = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_ID));
                String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_TITLE));
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_COMPLETED));
                long linkedDocId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DOC_ID));

                total++;
                if (isCompleted == 1) completed++;

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0, 8, 0, 8);

                CheckBox cb = new CheckBox(this);
                cb.setText(itemTitle);
                cb.setChecked(isCompleted == 1);
                cb.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    dbHelper.updateItemCompleted(itemId, isChecked ? 1 : 0);
                    loadChecklistDetails();
                });
                row.addView(cb);

                if (linkedDocId != -1) {
                    MaterialButton btnView = new MaterialButton(this, null, com.google.android.material.R.style.Widget_Material3_Button_TextButton);
                    btnView.setText("View Doc");
                    btnView.setTextSize(12);
                    btnView.setOnClickListener(v -> openLinkedDocument(linkedDocId));
                    row.addView(btnView);
                } else {
                    MaterialButton btnLink = new MaterialButton(this, null, com.google.android.material.R.style.Widget_Material3_Button_TextButton);
                    btnLink.setText("Link Doc");
                    btnLink.setTextSize(12);
                    btnLink.setOnClickListener(v -> showLinkDocumentDialog(itemId));
                    row.addView(btnLink);
                }

                containerItems.addView(row);
            } while (cursor.moveToNext());
            cursor.close();
        }

        int pending = total - completed;
        int percent = total > 0 ? (completed * 100) / total : 0;
        tvProgress.setText(String.format(Locale.getDefault(), "Progress: %d of %d completed (%d%%) · %d pending", completed, total, percent, pending));
        if (progressBar != null) {
            progressBar.setProgress(percent);
        }
    }

    private void showAddRequirementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Requirement");

        final EditText input = new EditText(this);
        input.setHint("Requirement Title");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                dbHelper.addItemToChecklist(checklistId, title, 0, -1);
                Toast.makeText(this, "Requirement added", Toast.LENGTH_SHORT).show();
                loadChecklistDetails();
            } else {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showLinkDocumentDialog(long itemId) {
        Cursor cursor = dbHelper.getAllDocuments();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No documents in vault to link. Import a document first.", Toast.LENGTH_LONG).show();
            return;
        }

        int count = cursor.getCount();
        String[] titles = new String[count];
        long[] docIds = new long[count];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                docIds[i] = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_ID));
                titles[i] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_TITLE));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Link Vault Document");
        builder.setItems(titles, (dialog, which) -> {
            long selectedDocId = docIds[which];
            dbHelper.updateItemLinkedDoc(itemId, selectedDocId);
            Toast.makeText(this, "Document linked successfully!", Toast.LENGTH_SHORT).show();
            loadChecklistDetails();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openLinkedDocument(long docId) {
        Cursor cursor = dbHelper.getAllDocuments();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_ID));
                if (id == docId) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_TITLE));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_CATEGORY));
                    String uri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_URI));
                    String size = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_SIZE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_DATE));
                    String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOC_EXPIRY));

                    Intent intent = new Intent(this, DocumentDetailActivity.class);
                    intent.putExtra("DOC_ID", id);
                    intent.putExtra("DOC_TITLE", title);
                    intent.putExtra("DOC_CATEGORY", category);
                    intent.putExtra("DOC_URI", uri);
                    intent.putExtra("DOC_SIZE", size);
                    intent.putExtra("DOC_DATE", date);
                    intent.putExtra("DOC_EXPIRY", expiry);
                    startActivity(intent);
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
