package com.example.project_hc002;

import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Find views
        ImageView backButton = findViewById(R.id.backButton);
        ImageView newsImage = findViewById(R.id.newsImage);
        TextView newsTitle = findViewById(R.id.newsTitle);
        TextView newsDescription = findViewById(R.id.newsDescription);
        TextView tanggal = findViewById(R.id.tanggal);
        TextView author = findViewById(R.id.author);

        // Get data from intent
        String title = getIntent().getStringExtra("news_title");
        String description = getIntent().getStringExtra("news_desc");
        String dateStr = getIntent().getStringExtra("news_date");
        String imageBase64 = getIntent().getStringExtra("news_image");
        String authorName = getIntent().getStringExtra("news_author");

        // Format the date
        String formattedDate = formatDate(dateStr);

        // Set data
        newsTitle.setText(title != null ? title : "");
        newsDescription.setText(description != null ? description : "");
        tanggal.setText("Tanggal Artikel: " + formattedDate);
        author.setText("Pembuat Artikel: " + (authorName != null ? authorName : "Anonymous"));

        // Set image from base64
        if (imageBase64 != null) {
            try {
                byte[] decodedString = Base64.decode(imageBase64.split(",")[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                newsImage.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                newsImage.setImageResource(R.drawable.vector002);
            }
        } else {
            newsImage.setImageResource(R.drawable.vector002);
        }

        // Back button action
        backButton.setOnClickListener(v -> finish());
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr;
        }
    }
}