package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 slideViewPager;
    LinearLayout dotsLayout;
    Button nextBtn, skipBtn;
    TextView[] dots;
    SlideViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        slideViewPager = findViewById(R.id.slideViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);

        // Isi konten untuk slides
        int[] images = {
                R.drawable.maly, // Ganti dengan gambar Anda
                R.drawable.doly,
                R.drawable.holy
        };

        String[] headings = {
                "Selamat Datang",
                "Fitur Kami",
                "Mulai Sekarang"
        };

        String[] descriptions = {
                "Mari Berpartisipasi dalam kegiatan Event yang diadakan oleh ASABRI",
                "Gabung sekarang, tweet cerita serumu, dan nikmati keseruan berkomentar di event-event pilihan!",
                "Dapatkan notifikasi eksklusif, event dan jadilah bagian dari komunitas penuh reward dan hiburan."
        };

        viewPagerAdapter = new SlideViewPagerAdapter(this, images, headings, descriptions);
        slideViewPager.setAdapter(viewPagerAdapter);

        addDots(0);

        slideViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == images.length - 1) {
                    nextBtn.setText("Mulai");
                } else {
                    nextBtn.setText("Next");
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideViewPager.getCurrentItem() + 1 < viewPagerAdapter.getItemCount()) {
                    slideViewPager.setCurrentItem(slideViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(OnboardingActivity.this, koneksi.class));
                    finish();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnboardingActivity.this, koneksi.class));
                finish();
            }
        });
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.black)); // Ganti dengan warna yang diinginkan
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.purple_500)); // Ganti dengan warna yang diinginkan
        }
    }
}