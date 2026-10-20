package com.example.doclocker;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

public class CourseDemoActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private SecurityAuditReceiver auditReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_demo);

        ImageView ivBack = findViewById(R.id.ivBackDemo);
        ivBack.setOnClickListener(v -> finish());

        MaterialButton btnCamera = findViewById(R.id.btnCameraIntent);
        MaterialButton btnDatePicker = findViewById(R.id.btnDatePicker);
        MaterialButton btnBroadcast = findViewById(R.id.btnBroadcast);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        // 1. Camera Intent Demo
        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Camera not available on emulator", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. DatePicker Demo
        btnDatePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
                String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
                tvSelectedDate.setText("Selected Expiry: " + dateStr);
                Toast.makeText(this, "Expiry date set: " + dateStr, Toast.LENGTH_SHORT).show();
            }, year, month, day);
            datePickerDialog.show();
        });

        // 3. BroadcastReceiver Demo
        auditReceiver = new SecurityAuditReceiver();
        IntentFilter filter = new IntentFilter("com.example.doclocker.SECURITY_AUDIT_EVENT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(auditReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(auditReceiver, filter);
        }

        btnBroadcast.setOnClickListener(v -> {
            Intent broadcastIntent = new Intent("com.example.doclocker.SECURITY_AUDIT_EVENT");
            broadcastIntent.setPackage(getPackageName());
            broadcastIntent.putExtra("EVENT_MESSAGE", "Vault security audit scan triggered manually.");
            sendBroadcast(broadcastIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (auditReceiver != null) {
            unregisterReceiver(auditReceiver);
        }
    }

    public static class SecurityAuditReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("EVENT_MESSAGE");
            Toast.makeText(context, "Broadcast Received: " + (msg != null ? msg : "Security Event"), Toast.LENGTH_LONG).show();
        }
    }
}
