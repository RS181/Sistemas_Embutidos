package com.example.videocamera;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class StorageAcess extends AppCompatActivity {
    private static final String BASE_URL = "http://192.168.165.229:4000/";
    private LinearLayout imageContainer;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        imageContainer = new LinearLayout(this);
        imageContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(imageContainer);
        setContentView(scrollView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchImages();
    }

    private void fetchImages() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder().url(BASE_URL + "images").build();
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    List<String> images = new Gson().fromJson(response.body().string(), List.class);
                    runOnUiThread(() -> displayImages(images));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayImages(List<String> images) {
        imageContainer.removeAllViews();
        for (String image : images) {
            View view = getLayoutInflater().inflate(R.layout.activity_storage_acess, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            Button deleteButton = view.findViewById(R.id.deleteButton);

            Glide.with(this).load(BASE_URL + "images/" + image).into(imageView);
            deleteButton.setOnClickListener(v -> deleteImage(image));

            imageContainer.addView(view);
        }
    }

    private void deleteImage(String imageName) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "images/" + imageName)
                        .delete()
                        .build();
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Imagem removida", Toast.LENGTH_SHORT).show();
                        fetchImages();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    interface ApiService {
        @GET("images")
        retrofit2.Call<List<String>> getImages();

        @DELETE("images/{filename}")
        retrofit2.Call<Void> deleteImage(@Path("filename") String filename);
    }
}