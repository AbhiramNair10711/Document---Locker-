package com.example.doclocker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

public class ExpiryTrackerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry_tracker);

        dbHelper = new DatabaseHelper(this);

        ImageView ivBack = findViewById(R.id.ivBackExpiry);
        tvSummary = findViewById(R.id.tvExpirySummary);
        recyclerView = findViewById(R.id.recyclerViewExpiry);

        ivBack.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadExpiryData();
    }

    private void loadExpiryData() {
        Cursor cursor = dbHelper.getAllDocuments();
        int count = cursor != null ? cursor.getCount() : 0;
        tvSummary.setText("Tracking " + count + " documents. Tap any document to update its expiry date.");

        if (adapter == null) {
            adapter = new DocumentAdapter(cursor, false, (id, title, category, uri, size, date, expiry) -> {
                showUpdateExpiryDialog(id, title, expiry);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }
    }

    private void showUpdateExpiryDialog(long id, String title, String currentExpiry) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String newExpiry = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
            dbHelper.updateDocumentExpiry(id, newExpiry);
            Toast.makeText(this, "Expiry date updated for " + title, Toast.LENGTH_SHORT).show();
            loadExpiryData();
        }, year, month, day);
        datePickerDialog.setTitle("Set Expiry for " + title);
        datePickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpiryData();
    }
}
