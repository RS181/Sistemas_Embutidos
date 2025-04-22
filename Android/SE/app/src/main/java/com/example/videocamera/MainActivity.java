package com.example.videocamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String STOP_MOTION_URL = "http://192.168.165.229:4000/stop_stop_motion";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cria o canal de notificação
        NotificationUtils.createNotificationChannel(this);

        // Subscreve ao tópico 'all'
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Subscribed to topic 'all'");
                    } else {
                        Log.e("FCM", "Subscription failed", task.getException());
                    }
                });

        Button startStreamButton = findViewById(R.id.btnStartStream);
        startStreamButton.setOnClickListener(v -> {
            stopStopMotionAndStartStreaming();
        });

        Button accessStorageButton = findViewById(R.id.btnAccessStorage);
        accessStorageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StorageAcess.class);
            startActivity(intent);
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
    }

    private void stopStopMotionAndStartStreaming() {
        Request request = new Request.Builder().url(STOP_MOTION_URL).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao interromper Stop-Motion", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Stop-Motion interrompido", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, StreamingActivity.class);
                            startActivity(intent);
                        }, 5000); // Aguarda 5 seg antes de iniciar o streaming (para o stop motion libertar recursos da camera
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao interromper Stop-Motion", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}