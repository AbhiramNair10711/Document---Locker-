package com.example.doclocker;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ImageView ivBack = findViewById(R.id.ivBackHelp);
        ivBack.setOnClickListener(v -> finish());

        WebView webView = findViewById(R.id.webViewHelp);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/help.html");
    }
}
