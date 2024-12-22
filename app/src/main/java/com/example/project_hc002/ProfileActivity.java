package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Menghubungkan ke layout activity_profile.xml

        // Logout Listener
        ImageView logoutButton = findViewById(R.id.img_logout);
        logoutButton.setOnClickListener(v -> showLogoutDialog()); // Menampilkan dialog konfirmasi logout

        // Tombol Lihat Detail Profil
        View detailLink = findViewById(R.id.detail_link);
        detailLink.setOnClickListener(v -> navigateToDetailProfile()); // Navigasi ke halaman detail profil

        // Tombol FAQ
        LinearLayout faqButton = findViewById(R.id.faq_button);
        faqButton.setOnClickListener(v -> {
            Log.d("ProfileActivity", "FAQ Button clicked"); // Logging untuk debugging
            Intent intent = new Intent(ProfileActivity.this, FAQActivity.class);
            startActivity(intent); // Membuka halaman FAQActivity
        });

        // Tombol Arsip_Berita
//        LinearLayout arsipButton = findViewById(R.id.arsip_buttom);
//        arsipButton.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, ArsipBeritaActivity.class);
//            startActivity(intent); // Membuka halaman ArsipBeritaActivity
//        });
    }

    // Menampilkan dialog konfirmasi logout
    private void showLogoutDialog() {
        // Inflate layout dari XML
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);

        // Buat dialog menggunakan layout yang di-inflate
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // Tidak bisa ditutup dengan klik di luar

        // Tombol di layout dialog
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnLogout = dialogView.findViewById(R.id.btn_logout);

        // Aksi tombol Batal
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Aksi tombol Keluar
        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            logout(); // Panggil metode logout
        });

        // Tampilkan dialog
        dialog.show();
    }

    // Navigasi ke halaman Detail Profil
    private void navigateToDetailProfile() {
        Intent intent = new Intent(ProfileActivity.this, DetailProfile.class);
        intent.putExtra("name", "Tegar"); // Contoh data pengguna
        intent.putExtra("email", "Tegar@gmail.com");
        intent.putExtra("phone", "08159833305");
        intent.putExtra("bio", "Hai");
        startActivity(intent);
    }

    // Fungsi logout
    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, koneksi.class);
        startActivity(intent);
        finish();
    }
}
