package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etKtpa, etNewPassword;
    private Button btnResetPassword;
    private static final String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etKtpa = findViewById(R.id.et_ktpa);
        etNewPassword = findViewById(R.id.et_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String ktpa = etKtpa.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (ktpa.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to hold the parameters
        Map<String, String> body = new HashMap<>();
        body.put("ktpa", ktpa);
        body.put("newPassword", newPassword);

        // Call the API to reset the password
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResetPasswordResponse> call = apiService.resetPassword(body);
        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful()) {
                    // Check if the response body indicates success
                    if (response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, "Kata sandi berhasil diganti", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this, successpass.class));
                        finish(); // Kembali ke activity sebelumnya
                    } else {
                        // Handle the case where ktpa is not found
                        Toast.makeText(ResetPasswordActivity.this, "Maaf, KTPA yang Anda masukkan tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle other unsuccessful responses
                    Toast.makeText(ResetPasswordActivity.this, "Gagal mengubah kata sandi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(ResetPasswordActivity.this, "Kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

