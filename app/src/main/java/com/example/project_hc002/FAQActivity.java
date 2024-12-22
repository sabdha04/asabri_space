package com.example.project_hc002;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_screen); // Layout untuk FAQ

        // Pertanyaan 1
        LinearLayout question1Layout = findViewById(R.id.question_1_layout);
        TextView answer1Text = findViewById(R.id.answer_1_text);

        question1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAnswerVisibility(answer1Text);
            }
        });

        // Pertanyaan 2
        LinearLayout question2Layout = findViewById(R.id.question_2_layout);
        TextView answer2Text = findViewById(R.id.answer_2_text);

        question2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAnswerVisibility(answer2Text);
            }
        });

        // Pertanyaan 3
        LinearLayout question3Layout = findViewById(R.id.question_3_layout);
        TextView answer3Text = findViewById(R.id.answer_3_text);

        question3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAnswerVisibility(answer3Text);
            }
        });

        // Pertanyaan 4
        LinearLayout question4Layout = findViewById(R.id.question_4_layout);
        TextView answer4Text = findViewById(R.id.answer_4_text);

        question4Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAnswerVisibility(answer4Text);
            }
        });
    }

    // Metode untuk menampilkan atau menyembunyikan jawaban dengan animasi
    private void toggleAnswerVisibility(final TextView answerText) {
        if (answerText.getVisibility() == View.GONE) {
            // Gunakan TransitionManager untuk animasi bawaan
            TransitionManager.beginDelayedTransition((ViewGroup) answerText.getParent());
            answerText.setVisibility(View.VISIBLE);
        } else {
            // Sembunyikan dengan animasi bawaan
            TransitionManager.beginDelayedTransition((ViewGroup) answerText.getParent());
            answerText.setVisibility(View.GONE);
        }

        // Debug untuk memastikan tinggi yang diukur
        answerText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetHeight = answerText.getMeasuredHeight();
        Log.d("FAQActivity", "Measured height: " + targetHeight);
    }
}
