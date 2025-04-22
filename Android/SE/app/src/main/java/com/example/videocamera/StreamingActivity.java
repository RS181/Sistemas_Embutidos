package com.example.videocamera;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
public class StreamingActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://192.168.165.229:5000/video_feed";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilita JavaScript

        webView.setWebViewClient(new WebViewClient());

        // Substitua com o IP do seu servidor Flask
        webView.loadUrl(VIDEO_URL);

        // Botão "Voltar" para retornar à MainActivity
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha esta Activity e retorna para MainActivity
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("about:blank"); // Limpa a WebView
        webView.clearHistory();
        webView.destroy(); // Destrói a WebView para liberar recursos
    }
}