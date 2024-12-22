package com.example.project_hc002;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class barter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barter);

        // Dummy data
        String itemName = "Contoh Barang";
        String itemDescription = "Ini adalah deskripsi barang.";
        String itemPrice = "Rp 100.000";

        // Menghubungkan TextView dengan ID yang sesuai
        TextView itemNameTextView = findViewById(R.id.item_name);
        TextView itemDescriptionTextView = findViewById(R.id.item_description);
        TextView itemPriceTextView = findViewById(R.id.item_price);

        // Mengisi TextView dengan data dummy
        itemNameTextView.setText("Nama Barang: " + itemName);
        itemDescriptionTextView.setText("Deskripsi: " + itemDescription);
        itemPriceTextView.setText("Harga: " + itemPrice);
    }
}