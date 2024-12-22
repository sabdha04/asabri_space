package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail, profileKtpa, profileBio;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.profileFr), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProfileData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        // Initialize TextViews
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileKtpa = view.findViewById(R.id.ktpa);
        profileBio = view.findViewById(R.id.profile_bio);

        // Load existing data from SharedPreferences
        loadProfileData();

        // Logout Listener
        ImageView logoutButton = view.findViewById(R.id.img_logout);
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Profile Detail Link
        View detailLink = view.findViewById(R.id.detail_link);
        detailLink.setOnClickListener(v -> navigateToDetailProfile());

        // Chat Admin Button
        LinearLayout chatAdminButton = view.findViewById(R.id.chat_admin_button);
        chatAdminButton.setOnClickListener(v -> {
            Log.d("ProfileFragment", "Chat Admin Button clicked");
            startActivity(new Intent(getActivity(), ChatAdminActivity.class));
        });

        // FAQ Button
        LinearLayout faqButton = view.findViewById(R.id.faq_button);
        faqButton.setOnClickListener(v -> {
            Log.d("ProfileFragment", "FAQ Button clicked");
            startActivity(new Intent(getActivity(), FAQActivity.class));
        });

        // Archive Button
//        LinearLayout arsipButton = view.findViewById(R.id.arsip_buttom);
//        arsipButton.setOnClickListener(v ->
//                startActivity(new Intent(getActivity(), ArsipBeritaActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("pesertaName", "Nama tidak tersedia");
        String email = sharedPreferences.getString("pesertaEmail", "Email tidak tersedia");
        String ktpa = sharedPreferences.getString("pesertaKtpa", "KTPA tidak tersedia");
        String bio = sharedPreferences.getString("pesertaBio", "Bio tidak tersedia");

        // Set the values to TextViews
        profileName.setText(name);
        profileEmail.setText(email);
        profileKtpa.setText(ktpa);
        profileBio.setText(bio);
    }

    private void navigateToDetailProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        Intent intent = new Intent(getActivity(), DetailProfile.class);
        intent.putExtra("name", sharedPreferences.getString("pesertaName", ""));
        intent.putExtra("ktpa", sharedPreferences.getString("pesertaKtpa", ""));
        intent.putExtra("email", sharedPreferences.getString("pesertaEmail", ""));
        intent.putExtra("bio", sharedPreferences.getString("pesertaBio", ""));
        startActivity(intent);
    }

    private void showLogoutDialog() {
        // Inflate layout dari XML
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);

        // Buat dialog menggunakan layout yang di-inflate
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
            // Clear SharedPreferences saat logout
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            dialog.dismiss();
            // Navigasi ke halaman login
            Intent intent = new Intent(getActivity(), loginpeserta.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Tampilkan dialog
        dialog.show();
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), koneksi.class);
        startActivity(intent);
        requireActivity().finish();
    }
}