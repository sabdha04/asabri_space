package com.example.project_hc002;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        // Initialize Back Button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
//            Toast.makeText(this, "Kembali ditekan", Toast.LENGTH_SHORT).show();
            finish(); // Tutup aktivitas
        });


        // Initialize Level Data
        Level bronzeLevel = new Level("Perunggu (Level 1)", "100 Points to Perak", 70, R.drawable.medal);
        Level silverLevel = new Level("Perak (Level 2)", "500 Points to Emas", 30, R.drawable.dua);
        Level goldLevel = new Level("Emas (Level 3)", "1000 Points to Platinum", 50, R.drawable.tiga);
        Level platinumLevel = new Level("Platinum (Level 4)", "Promosi ke Safir 2000", 20, R.drawable.empat);

        // Populate Level Data
//        populateLevel(R.id.card_bronze, bronzeLevel);
//        populateLevel(R.id.card_silver, silverLevel);
//        populateLevel(R.id.card_gold, goldLevel);
//        populateLevel(R.id.card_platinum, platinumLevel);
    }
//
//    private void populateLevel(int cardId, Level level) {
//        View cardView = findViewById(cardId);
//
//        if (cardView != null) {
//            // Set icon
//            ImageView levelIcon = cardView.findViewById(R.id.level_icon);
//            levelIcon.setImageResource(level.getIconResId());
//
//            // Set title
//            TextView levelTitle = cardView.findViewById(R.id.level_title);
//            levelTitle.setText(level.getTitle());
//
//            // Set progress description
//            TextView levelDescription = cardView.findViewById(R.id.level_description);
//            levelDescription.setText(level.getDescription());
//
//            // Set progress bar
//            ProgressBar progressBar = cardView.findViewById(R.id.level_progress);
//            progressBar.setProgress(level.getProgress());
//        }
//    }

    // Model for Level Data
    static class Level {
        private final String title;
        private final String description;
        private final int progress;
        private final int iconResId;

        public Level(String title, String description, int progress, int iconResId) {
            this.title = title;
            this.description = description;
            this.progress = progress;
            this.iconResId = iconResId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getProgress() {
            return progress;
        }

        public int getIconResId() {
            return iconResId;
        }
    }
}
