package com.example.doclocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class VaultAuthActivity extends AppCompatActivity {

    private static final String PREF_NAME = "DocLockerVaultPrefs";
    private static final String KEY_PASSWORD = "vault_password";

    private TextInputEditText etPassword;
    private TextView tvTitle, tvSubtitle;
    private MaterialButton btnSubmit;
    private boolean isPasswordSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_auth);

        tvTitle = findViewById(R.id.tvAuthTitle);
        tvSubtitle = findViewById(R.id.tvAuthSubtitle);
        etPassword = findViewById(R.id.etVaultPassword);
        btnSubmit = findViewById(R.id.btnAuthSubmit);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedPassword = prefs.getString(KEY_PASSWORD, null);

        if (savedPassword == null || savedPassword.isEmpty()) {
            isPasswordSet = false;
            tvTitle.setText("Create Vault Password");
            tvSubtitle.setText("Set a PIN or password to protect your secure documents");
            btnSubmit.setText("Set Password");
        } else {
            isPasswordSet = true;
            tvTitle.setText("Unlock DocLocker Vault");
            tvSubtitle.setText("Enter your vault password to continue");
            btnSubmit.setText("Unlock Vault");
        }

        btnSubmit.setOnClickListener(v -> {
            String enteredPassword = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordSet) {
                // Save new password
                prefs.edit().putString(KEY_PASSWORD, enteredPassword).apply();
                Toast.makeText(this, "Vault password created successfully!", Toast.LENGTH_SHORT).show();
                openVault();
            } else {
                // Verify password
                if (enteredPassword.equals(savedPassword)) {
                    Toast.makeText(this, "Vault unlocked successfully", Toast.LENGTH_SHORT).show();
                    openVault();
                } else {
                    Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }
            }
        });
    }

    private void openVault() {
        Intent intent = new Intent(VaultAuthActivity.this, DocLockerActivity.class);
        startActivity(intent);
        finish();
    }
}
