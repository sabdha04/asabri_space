package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cardview.widget.CardView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.project_hc002.models.LoginResponse;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.TextView;

public class loginpeserta extends AppCompatActivity {

    private static final String TAG = "LoginPeserta";
    private EditText etEmail, etPassword;
    private Button btnSignIn;
    private ImageView ivBack;
    private CheckBox cbRememberMe;
    private TextView tvForgotPassword, tvRegisterText, tvRegister, tvloginlink;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpeserta);

        // Initialize ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Find views by ID
        etEmail = findViewById(R.id.et_signin_email);
        etPassword = findViewById(R.id.et_signin_passwd);
        btnSignIn = findViewById(R.id.btn_signin);
        ivBack = findViewById(R.id.iv_back);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegisterText = findViewById(R.id.tv_register_text);
        tvRegister = findViewById(R.id.tv_register);
        tvloginlink = findViewById(R.id.tv_login_link);

        // Back button click listener
        ivBack.setOnClickListener(v -> finish());

        // Forgot password click listener
        // Tambahkan ini di dalam onCreate
        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(loginpeserta.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Register click listener - Updated to match loginstaff logic
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(loginpeserta.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Set onClickListener for the login button
        btnSignIn.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");

            String ktpa = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Log.d(TAG, "ktpa: " + ktpa);
            Log.d(TAG, "Password: " + password);

            if (ktpa.isEmpty() || password.isEmpty()) {
                Toast.makeText(loginpeserta.this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save remember me preference if checked
            if (cbRememberMe.isChecked()) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savedKtpa", ktpa);
                editor.apply();
            }

            // Create login request
            Map<String, String> credentials = new HashMap<>();
            credentials.put("ktpa", ktpa);
            credentials.put("password", password);

            Log.d(TAG, "Making API call");

            Call<LoginResponse> call = apiService.loginPeserta(credentials);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.d(TAG, "Got API response: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.d(TAG, "Response success: " + loginResponse.isSuccess());

                        if (loginResponse.isSuccess()) {
                            // Save login status and user data
                            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("pesertaKtpa", loginResponse.getPeserta().getKtpa());
                            editor.putString("pesertaName", loginResponse.getPeserta().getName());
                            editor.putString("pesertaEmail", loginResponse.getPeserta().getEmail());
                            editor.putString("pesertaBio", loginResponse.getPeserta().getBio());
                            editor.apply();

                            // Navigate to MainActivity
                            Intent intent = new Intent(loginpeserta.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(loginpeserta.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(loginpeserta.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e(TAG, "Network error", t);
                    Toast.makeText(loginpeserta.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Load saved KTPA if remember me was checked
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedKtpa = sharedPreferences.getString("savedKtpa", "");
        if (!savedKtpa.isEmpty()) {
            etEmail.setText(savedKtpa);
            cbRememberMe.setChecked(true);
        }
    }
}