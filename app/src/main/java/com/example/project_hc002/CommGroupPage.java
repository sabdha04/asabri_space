package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommGroupPage extends AppCompatActivity {

    private KomunitasAdapter komunitasAdapter;
    private List<Komunitas> komunitasList;
    private RecyclerView rvcomm;

    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imgcom;
    TextView comtitle, comdesc;
    Button joincom;
    private int komunitasId;
    private boolean isJoined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comm_group_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgcom = findViewById(R.id.imggroupforum);
        comtitle = findViewById(R.id.comtitle);
        comdesc = findViewById(R.id.comdescription);

        Intent intent = getIntent();
        comtitle.setText(intent.getStringExtra("komun_name"));
        comdesc.setText(intent.getStringExtra("komun_desc"));
        joincom = findViewById(R.id.joincom);
        komunitasId = getIntent().getIntExtra("komun_id", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("Komun_id", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("komunitasId", komunitasId);
        editor.apply();
        Log.d("CommGroup", "komunitasId: " + komunitasId);

        checkJoinStatus();

//        joincom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                joinKomunitas();
//            }
//        });
        joincom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isJoined) {
                    showLeaveDialog();
//                    unjoinKomunitas();
                } else {
                    joinKomunitas();
                }
            }
        });
        Toolbar tb = findViewById(R.id.toolbar_komun);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setElevation(40);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), PostKomunPage.class);
                it.putExtra("komun_id", komunitasId);
                v.getContext().startActivity(it);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        rvcomm = findViewById(R.id.rvcommunityfr);
        rvcomm.setLayoutManager(new LinearLayoutManager(this));
        komunitasList = new ArrayList<>();
        komunitasAdapter = new KomunitasAdapter(komunitasList, this, komunitasId);
        rvcomm.setAdapter(komunitasAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        loadPosts();


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    private void checkJoinStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String user_ktpa = sharedPreferences.getString("pesertaKtpa", "");

        if (!user_ktpa.isEmpty()) {
            JoinRequest joinRequest = new JoinRequest(user_ktpa, komunitasId);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Boolean> call = apiService.isUserJoined(joinRequest);

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        isJoined = response.body(); // Set the join status
                        updateJoinButton(); // Update the button UI
                    } else {
                        Toast.makeText(CommGroupPage.this, "Error checking join status", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(CommGroupPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateJoinButton() {
        if (isJoined) {
            joincom.setText("Leave");
            joincom.setBackgroundColor(getResources().getColor(R.color.grey));
        } else {
            joincom.setText("Join");
            joincom.setBackgroundColor(getResources().getColor(R.color.blueasabri));
        }
    }

    void joinKomunitas() {
        if (komunitasId != -1) {
//            String user_ktpa = "100100";
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String user_ktpa = sharedPreferences.getString("pesertaKtpa", "");

            if (user_ktpa.isEmpty()) {
                Toast.makeText(CommGroupPage.this, "Data KTPA tidak ditemukan!", Toast.LENGTH_SHORT).show();
                return;
            }
            JoinRequest joinRequest = new JoinRequest(user_ktpa, komunitasId);
//            Log.d("CommGroupreq", "dataReq: " + user_ktpa + komunitasId);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<JoinResponse> call = apiService.joinKomunitas(joinRequest);
            call.enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JoinResponse apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Toast.makeText(CommGroupPage.this, "Bergabung dengan komunitas berhasil!", Toast.LENGTH_SHORT).show();
                            isJoined = true;
                            updateJoinButton();
                        } else {
                            Toast.makeText(CommGroupPage.this, "Gagal bergabung dengan komunitas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.code() == 400) {
                            Toast.makeText(CommGroupPage.this, "Anda sudah bergabung dengan komunitas ini", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CommGroupPage.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JoinResponse> call, Throwable t) {
                    Toast.makeText(CommGroupPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CommGroupPage.this, "ID komunitas tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLeaveDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_leavekomun, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(CommGroupPage.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnLogout = dialogView.findViewById(R.id.btn_logout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            unjoinKomunitas();
            finish();
        });

        dialog.show();
    }


    void unjoinKomunitas() {
        // Logic to leave the community
        if (komunitasId != -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String user_ktpa = sharedPreferences.getString("pesertaKtpa", "");

            if (user_ktpa.isEmpty()) {
                Toast.makeText(CommGroupPage.this, "Data KTPA tidak ditemukan!", Toast.LENGTH_SHORT).show();
                return;
            }
            JoinRequest joinRequest = new JoinRequest(user_ktpa, komunitasId);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<JoinResponse> call = apiService.leaveKomunitas(joinRequest);
            call.enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JoinResponse apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Toast.makeText(CommGroupPage.this, "Berhasil meninggalkan komunitas", Toast.LENGTH_SHORT).show();
                            isJoined = false; // Update the join status
                            updateJoinButton(); // Update the button
                        } else {
                            Toast.makeText(CommGroupPage.this, "Gagal meninggalkan komunitas", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JoinResponse> call, Throwable t) {
                    Toast.makeText(CommGroupPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void loadPosts() {
        showLoading(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        komunitasId = getIntent().getIntExtra("komun_id", -1);

        Call<List<PostKomun>> call = apiService.getPostKomunById(komunitasId);

        call.enqueue(new Callback<List<PostKomun>>() {
            @Override
            public void onResponse(Call<List<PostKomun>> call, Response<List<PostKomun>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    komunitasList.clear();
                    for (PostKomun postKomun : response.body()) {
                        Komunitas community = new Komunitas(
                                postKomun.getNama(),
                                postKomun.getContent(),
                                postKomun.getMedia_url(),
                                getRelativeTime(postKomun.getCreated_at()),
                                postKomun.getMedia_type()
                        );
                        community.setId(postKomun.getId());
                        community.setKtpa(postKomun.getUser_ktpa());
                        community.setCommentCount(postKomun.getComment_count());
                        komunitasList.add(community);
                    }
                    komunitasAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CommGroupPage.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostKomun>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CommGroupPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRelativeTime(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date utcDate = format.parse(createdAt);

            if (utcDate == null) {
                return "Tanggal tidak valid";
            }

            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            localFormat.setTimeZone(TimeZone.getDefault());
            String localTimeString = localFormat.format(utcDate);
            Date localDate = localFormat.parse(localTimeString);

            long diffInMillis = System.currentTimeMillis() - localDate.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (minutes < 60) {
                return minutes + " menit lalu";
            } else if (hours < 24) {
                return hours + " jam lalu";
            } else {
                return days + " hari lalu";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Tanggal tidak valid";
        }
    }

}