package com.example.project_hc002;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);  // Referencing your answer screen layout XML

        // Get the data passed through the Intent
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        // Setup TextViews to display the question and answer
        TextView questionText = findViewById(R.id.question_text);
        TextView answerText = findViewById(R.id.answer_text);

        // Set the question and answer into the TextViews
        questionText.setText(question);
        answerText.setText(answer);
    }
}
