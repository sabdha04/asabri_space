package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAll extends AppCompatActivity {

    private RecyclerView cardEvAll, recyclerViewEventAll;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private EventViewModel eventViewModel;
    private ProgressBar progressBar;

    CardView cardev;

    TextView evTitle, evDet;
    ImageView imgEv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        eventList = (List<Event>) getIntent().getSerializableExtra("event_list");

//        cardEvAll = findViewById(R.id.cardEvAll);
        recyclerViewEventAll = findViewById(R.id.cardEvAll);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEventAll.setLayoutManager(linearLayoutManager);
        recyclerViewEventAll.setHasFixedSize(true);

        progressBar = findViewById(R.id.progressBar);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, "eventAll");
        recyclerViewEventAll.setAdapter(eventAdapter);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        eventViewModel.getEvents().observe(this, events -> {
            if (events != null) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            } else {
                fetchdata();
            }
        });

        if (eventViewModel.getEvents().getValue() == null) {
            fetchdata();
        }
//        fetchdata();

        Toolbar tb = findViewById(R.id.toolbar_evall);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        fetchdata();
//    }

    private void fetchdata() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<List<Event>> call = apiService.getEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();
                    eventViewModel.setEvents(events);
                } else {
                    Toast.makeText(EventAll.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EventAll.this, "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void fetchdata() {
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        retrofit2.Call<List<Event>> call = apiService.getEvents();
//
//        call.enqueue(new Callback<List<Event>>() {
//            @Override
//            public void onResponse(retrofit2.Call<List<Event>> call, Response<List<Event>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Event> events = response.body();
//
//                    runOnUiThread(() -> {
//                        eventList.clear();
//                        eventList.addAll(events);
//                        eventAdapter.notifyDataSetChanged();
//                    });
//                } else {
//                    runOnUiThread(() -> {
//                        Toast.makeText(EventAll.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Event>> call, Throwable t) {
//                runOnUiThread(() -> {
//                    Toast.makeText(EventAll.this, "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//        });
//    }

}