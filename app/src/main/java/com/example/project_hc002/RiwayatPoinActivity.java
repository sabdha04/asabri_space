package com.example.project_hc002;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiwayatPoinActivity extends AppCompatActivity {
    private ImageView backbutton;

    public RiwayatPoinActivity() {
        // Default constructor required by Android
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_poin);

        // Initialize views
        backbutton = findViewById(R.id.backButton);
        if (backbutton != null) {
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Close activity and return to previous
                }
            });
        } else {
            Log.e("RiwayatPoinActivity", "backButton view not found in layout");
        }

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Dummy data for events
            List<HashMap<String, String>> eventList = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                HashMap<String, String> event = new HashMap<>();
                event.put("title", "Ke event " + i);
                event.put("points", "+20 poin");
                eventList.add(event);
            }

            // Set adapter
            recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_riwayat_poin, parent, false);
                    return new EventViewHolder(view);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    EventViewHolder eventViewHolder = (EventViewHolder) holder;
                    HashMap<String, String> event = eventList.get(position);
                    eventViewHolder.eventTitle.setText(event.get("title"));
                    eventViewHolder.eventPoints.setText(event.get("points"));
                }

                @Override
                public int getItemCount() {
                    return eventList.size();
                }
            });
        }
    }

    // ViewHolder class
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventPoints;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventPoints = itemView.findViewById(R.id.eventPoints);
        }
    }
}
