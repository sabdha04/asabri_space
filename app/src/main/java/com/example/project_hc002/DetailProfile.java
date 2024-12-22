package com.example.project_hc002;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProfile extends AppCompatActivity {

    private EditText nameEditText, emailEditText, KtpaeditText, bioEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;

    private TextView reset_pass;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);

        // Initialize views
        nameEditText = findViewById(R.id.profile_name_input);
        emailEditText = findViewById(R.id.profile_email_input);
        KtpaeditText = findViewById(R.id.ktpa);
        bioEditText = findViewById(R.id.profile_bio_input);
        maleRadioButton = findViewById(R.id.male_radio);
        femaleRadioButton = findViewById(R.id.female_radio);
        cancel = findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke frag profile
                onBackPressed();
            }
        });
        reset_pass = findViewById(R.id.tv_reset_password);
        reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProfile.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Load existing data from SharedPreferences
        loadExistingData();

        // Button to save changes
        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void loadExistingData() {
        // Get data from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String name = sharedPref.getString("pesertaName", "");
        String email = sharedPref.getString("pesertaEmail", "");
        String ktpa = sharedPref.getString("pesertaKtpa", "");
        String bio = sharedPref.getString("pesertaBio", "");

        // Set the values to EditTexts
        nameEditText.setText(name);
        emailEditText.setText(email);
        KtpaeditText.setText(ktpa);
        bioEditText.setText(bio);
    }

//    private void saveChanges() {
//        // Get updated data from EditTexts
//        String updatedName = nameEditText.getText().toString();
//        String updatedEmail = emailEditText.getText().toString();
//        String updatedKtpa = KtpaeditText.getText().toString();
//        String updatedBio = bioEditText.getText().toString();
//        String gender = getSelectedGender();
//
//        // Validate input
//        if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedKtpa.isEmpty()) {
//            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Log the data being sent
//        Log.d("DetailProfile", "Updated Name: " + updatedName);
//        Log.d("DetailProfile", "Updated Email: " + updatedEmail);
//        Log.d("DetailProfile", "Updated KTPA: " + updatedKtpa);
//        Log.d("DetailProfile", "Updated Bio: " + updatedBio);
//        Log.d("DetailProfile", "Selected Gender: " + gender);
//
//        // Create user data map
//        Map<String, String> userData = new HashMap<>();
//        userData.put("ktpa", updatedKtpa);
//        userData.put("nama", updatedName);
//        userData.put("email", updatedEmail);
//        userData.put("jenis_kelamin", gender);
//        userData.put("bio", updatedBio);
//
//        // Call API to update data
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        Call<Void> call = apiService.updatePeserta(userData);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(DetailProfile.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                    finish(); // Close the activity
//                } else {
//                    Log.e("DetailProfile", "Error: " + response.code() + " - " + response.message());
//                    Toast.makeText(DetailProfile.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(DetailProfile.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void saveChanges() {
        // Get updated data from EditTexts
        String updatedName = nameEditText.getText().toString();
        String updatedEmail = emailEditText.getText().toString();
        String updatedKtpa = KtpaeditText.getText().toString();
        String updatedBio = bioEditText.getText().toString();
        String gender = getSelectedGender();

        // Validate input
        if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedKtpa.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save updated data to SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pesertaName", updatedName);
        editor.putString("pesertaEmail", updatedEmail);
        editor.putString("pesertaKtpa", updatedKtpa);
        editor.putString("pesertaBio", updatedBio);
        editor.putString("pesertaGender", gender);  // Save gender as well
        editor.apply();

        // Log the data being sent
        Log.d("DetailProfile", "Updated Name: " + updatedName);
        Log.d("DetailProfile", "Updated Email: " + updatedEmail);
        Log.d("DetailProfile", "Updated KTPA: " + updatedKtpa);
        Log.d("DetailProfile", "Updated Bio: " + updatedBio);
        Log.d("DetailProfile", "Selected Gender: " + gender);

        Map<String, String> userData = new HashMap<>();
        userData.put("ktpa", updatedKtpa);
        userData.put("nama", updatedName);
        userData.put("email", updatedEmail);
        userData.put("jenis_kelamin", gender);
        userData.put("bio", updatedBio);
        // Call API to update data
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updatePeserta(userData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailProfile.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Log.e("DetailProfile", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(DetailProfile.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailProfile.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedGender() {
        // Determine selected gender
        if (maleRadioButton.isChecked()) {
            return "l";
        } else if (femaleRadioButton.isChecked()) {
            return "P";
        } else {
            return null; // If none selected
        }
    }
}
