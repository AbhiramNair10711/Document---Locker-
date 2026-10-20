package com.example.doclocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvVaultDocCount, tvVaultStorageSize;
    private RecyclerView recyclerViewRecentDocs;
    private LinearLayout layoutEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        ImageView ivSettings = findViewById(R.id.ivSettings);
        CardView cardDocLocker = findViewById(R.id.cardDocLocker);
        CardView cardExpiry = findViewById(R.id.cardExpiry);
        CardView cardRadar = findViewById(R.id.cardRadar);
        TextView tvViewAll = findViewById(R.id.tvViewAll);
        recyclerViewRecentDocs = findViewById(R.id.recyclerViewRecentDocs);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        MaterialButton btnImportFirst = findViewById(R.id.btnImportFirst);

        tvVaultDocCount = findViewById(R.id.tvVaultDocCount);
        tvVaultStorageSize = findViewById(R.id.tvVaultStorageSize);

        ivSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        cardDocLocker.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VaultAuthActivity.class);
            startActivity(intent);
        });

        cardExpiry.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpiryTrackerActivity.class);
            startActivity(intent);
        });

        cardRadar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReadinessRadarActivity.class);
            startActivity(intent);
        });

        tvViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VaultAuthActivity.class);
            startActivity(intent);
        });

        btnImportFirst.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VaultAuthActivity.class);
            startActivity(intent);
        });

        recyclerViewRecentDocs.setLayoutManager(new LinearLayoutManager(this));
        loadDashboardData();
    }

    private void loadDashboardData() {
        Cursor cursor = dbHelper.getAllDocuments();
        int docCount = cursor != null ? cursor.getCount() : 0;

        tvVaultDocCount.setText(docCount + " Document" + (docCount == 1 ? "" : "s") + " Stored");
        double estimatedSizeMb = docCount * 0.8;
        tvVaultStorageSize.setText(String.format(Locale.getDefault(), "Encrypted Storage: %.1f MB / 1 GB", estimatedSizeMb));

        if (docCount == 0) {
            recyclerViewRecentDocs.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewRecentDocs.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);

            // hideName = true for Home dashboard recent docs list
            DocumentAdapter adapter = new DocumentAdapter(cursor, true, (id, title, category, uri, size, date, expiry) -> {
                showPasswordPromptDialog(id, title, category, uri, size, date, expiry);
            });
            recyclerViewRecentDocs.setAdapter(adapter);
        }
    }

    private void showPasswordPromptDialog(long id, String title, String category, String uri, String size, String date, String expiry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vault Authentication Required");
        builder.setMessage("Enter your vault password to open this document from Home:");

        final EditText input = new EditText(this);
        input.setHint("Enter Vault Password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Unlock", (dialog, which) -> {
            String enteredPassword = input.getText() != null ? input.getText().toString().trim() : "";
            SharedPreferences prefs = getSharedPreferences("DocLockerVaultPrefs", Context.MODE_PRIVATE);
            String savedPassword = prefs.getString("vault_password", "");

            if (enteredPassword.equals(savedPassword)) {
                Intent intent = new Intent(MainActivity.this, DocumentDetailActivity.class);
                intent.putExtra("DOC_ID", id);
                intent.putExtra("DOC_TITLE", title);
                intent.putExtra("DOC_CATEGORY", category);
                intent.putExtra("DOC_URI", uri);
                intent.putExtra("DOC_SIZE", size);
                intent.putExtra("DOC_DATE", date);
                intent.putExtra("DOC_EXPIRY", expiry);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Incorrect vault password. Access denied.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}
