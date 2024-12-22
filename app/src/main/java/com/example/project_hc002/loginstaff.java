package com.example.project_hc002;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_hc002.models.LoginResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginstaff extends AppCompatActivity {
    private static final String TAG = "LoginStaff";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ApiService apiService;

    private ImageView backButton;

    private TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginstaff);

        // Inisialisasi ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Inisialisasi views
        usernameEditText = findViewById(R.id.et_signin_email);
        passwordEditText = findViewById(R.id.et_signin_passwd);
        loginButton = findViewById(R.id.btn_signin);

        // Initialize the back button
        backButton = findViewById(R.id.iv_back);

        // Add back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This will handle the back navigation
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Log.d(TAG, "Username: " + username);
                Log.d(TAG, "Password: " + password);

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(loginstaff.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Buat request login
                Map<String, String> credentials = new HashMap<>();
                credentials.put("username", username);
                credentials.put("password", password);

                Log.d(TAG, "Making API call");

                Call<com.example.project_hc002.models.LoginResponse> call = apiService.loginStaff(credentials);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        Log.d(TAG, "Got API response: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            Log.d(TAG, "Response success: " + loginResponse.isSuccess());
                            
                            if (loginResponse.isSuccess()) {
                                // Simpan status login
                                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isStaffLoggedIn", true);
                                editor.putString("staffId", String.valueOf(loginResponse.getStaff().getId()));
                                editor.apply();

                                // Pindah ke ProfileAdmin
                                Intent intent = new Intent(loginstaff.this, ProfileAdmin.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(loginstaff.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(loginstaff.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "Network error", t);
                        Toast.makeText(loginstaff.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
