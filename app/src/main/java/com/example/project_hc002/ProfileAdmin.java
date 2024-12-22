package com.example.project_hc002;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileAdmin extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Cek status login
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isStaffLoggedIn", false);
        
        if (!isLoggedIn) {
            // Jika belum login, kembali ke halaman login
            Intent intent = new Intent(this, loginstaff.class);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_admin);

        // Tombol Logout
        //LinearLayout logoutButton = findViewById(R.id.logout_button);
        Button logout = findViewById(R.id.logout_button2);
        logout.setOnClickListener(v -> {
            // Hapus data login
            SharedPreferences SharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileAdmin.this, "You have been logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileAdmin.this, koneksi.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}