package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etNomorKTPA, etEmail, etKataSandi, etKonfirmasiKataSandi;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ImageView ivBack;
    private RadioButton maleRadioButton, femaleRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNama = findViewById(R.id.et_nama);
        etNomorKTPA = findViewById(R.id.et_nomor_ktpa);
        etEmail = findViewById(R.id.et_email);
        etKataSandi = findViewById(R.id.et_kata_sandi);
        etKonfirmasiKataSandi = findViewById(R.id.et_konfirmasi_kata_sandi);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
        ivBack = findViewById(R.id.iv_back);
        maleRadioButton = findViewById(R.id.male_radio);
        femaleRadioButton = findViewById(R.id.female_radio);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, loginpeserta.class));
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, koneksi.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String nomorKTPA = etNomorKTPA.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String kataSandi = etKataSandi.getText().toString().trim();
        String konfirmasiKataSandi = etKonfirmasiKataSandi.getText().toString().trim();
        String jenisKelamin = maleRadioButton.isChecked() ? "L" : "P";

        if (nama.isEmpty() || nomorKTPA.isEmpty() || email.isEmpty() || kataSandi.isEmpty() || konfirmasiKataSandi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
        } else if (!kataSandi.equals(konfirmasiKataSandi)) {
            Toast.makeText(this, "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show();
        } else {
            // Buat request pendaftaran
            Map<String, String> userData = new HashMap<>();
            userData.put("ktpa", nomorKTPA);
            userData.put("nama", nama);
            userData.put("jenis_kelamin", jenisKelamin);
            userData.put("password", kataSandi);
            userData.put("email", email);

            // Kirim data ke server
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.registerPeserta(userData);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, SuccessActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}