package com.example.project_hc002;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

public class NewsAll extends AppCompatActivity {


    private RecyclerView cardNwAll;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private ApiService apiService;

    CardView cardnw;

    TextView nwTitle, nwDet;
    ImageView imgNw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardNwAll = findViewById(R.id.cardNwAll);
        nwTitle = findViewById(R.id.nwTitle);
        nwDet = findViewById(R.id.nwDet);
        imgNw = findViewById(R.id.imgNw);
        cardnw = findViewById(R.id.cardNw);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList, "newsAll");

        cardNwAll.setLayoutManager(new LinearLayoutManager(this));
        cardNwAll.setAdapter(newsAdapter);

        Toolbar tb = findViewById(R.id.toolbar_nwall);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchNews();
    }

    private void fetchNews() {
        apiService.getAllNews().enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body());
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(NewsAll.this, "Error fetching news: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}