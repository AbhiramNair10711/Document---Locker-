package com.example.doclocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String PREF_NAME = "DocLockerVaultPrefs";
    private static final String KEY_PASSWORD = "vault_password";

    private TextInputEditText etCurrent, etNew, etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrent = findViewById(R.id.etCurrentPassword);
        etNew = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdatePassword);

        btnUpdate.setOnClickListener(v -> {
            String current = etCurrent.getText() != null ? etCurrent.getText().toString().trim() : "";
            String newPass = etNew.getText() != null ? etNew.getText().toString().trim() : "";
            String confirmPass = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";

            SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String savedPassword = prefs.getString(KEY_PASSWORD, "");

            if (!current.equals(savedPassword)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.isEmpty()) {
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit().putString(KEY_PASSWORD, newPass).apply();
            Toast.makeText(this, "Vault password updated successfully!", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
