package com.example.project_hc002;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityAll extends AppCompatActivity {

    private List<ComGroup> comGroups;
    private RecyclerView rvcomgroup, rvcomm;
    private ComGroupAdapter comGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar tb = findViewById(R.id.toolbar_komun);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvcomgroup = findViewById(R.id.cardComAll);
        rvcomgroup.setLayoutManager(new LinearLayoutManager(this));
        comGroups = new ArrayList<>();
        fetchkomun();
        comGroupAdapter = new ComGroupAdapter(comGroups, this);
        rvcomgroup.setAdapter(comGroupAdapter);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    private void fetchkomun() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ComGroup>> call = apiService.getComGroup();

        call.enqueue(new Callback<List<ComGroup>>() {
            @Override
            public void onResponse(Call<List<ComGroup>> call, Response<List<ComGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comGroups.clear();
                    comGroups.addAll(response.body());
                    comGroupAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CommunityAll.this, "Failed to load community groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ComGroup>> call, Throwable t) {
                Toast.makeText(CommunityAll.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}