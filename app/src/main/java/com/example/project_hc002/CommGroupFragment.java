package com.example.project_hc002;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
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

public class CommGroupFragment extends Fragment {

    private List<ComGroup> comGroups;
    private RecyclerView rvcomgroup, rvcomm;
    private ComGroupAdapter comGroupAdapter;
    private CommunityAdapter communityAdapter;
    private List<Community> communityList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private KomunitasAdapter komunitasAdapter;
    private List<Komunitas> komunitasList;
    private int komunitasId;
    Button comAll;

    public CommGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comm_group, container, false);
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.swipeRefreshLayout), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        Toolbar tb = view.findViewById(R.id.toolbar_comholder);
//        if (getActivity() instanceof AppCompatActivity) {
//            AppCompatActivity activity = (AppCompatActivity) getActivity();
//            activity.setSupportActionBar(tb);
//            if (activity.getSupportActionBar() != null) {
//                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//            }
//        }

        rvcomgroup = view.findViewById(R.id.rvcommunitygr);

        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvcomgroup.setLayoutManager(new LinearLayoutManager(getContext()));
        comGroups = new ArrayList<>();
//        populatecomm();
        fetchkomun();
        comGroupAdapter = new ComGroupAdapter(comGroups, getContext());
        rvcomgroup.setAdapter(comGroupAdapter);

        rvcomm = view.findViewById(R.id.rvcommunityfr);
        rvcomm.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(communityList, getContext());
        rvcomm.setAdapter(communityAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        loadPosts();

        comAll = view.findViewById(R.id.btncommall);
        comAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), CommunityAll.class);
//                intent.putExtra("com_list", (Serializable) comGroups);
                startActivity(intent);
            }
        });

        return view;

    }

//    private void populatecomm() {
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//    }

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
    private void fetchkomun() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ComGroup>> call = apiService.getComGroup();

        call.enqueue(new Callback<List<ComGroup>>() {
            @Override
            public void onResponse(Call<List<ComGroup>> call, Response<List<ComGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    comGroups.clear();
//                    comGroups.addAll(response.body());
                    if (response.body().size() > 3) {
                        comGroups.clear();
                        comGroups.addAll(response.body().subList(0, 3));
                    } else {
                        comGroups.clear();
                        comGroups.addAll(response.body());
                    }
                    comGroupAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load community groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ComGroup>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void loadPosts() {
        showLoading(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Post>> call = apiService.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    communityList.clear();
                    for (Post post : response.body()) {
                        Community community = new Community(
                                post.getNama(),
                                post.getContent(),
                                post.getImageUrl(),
                                getRelativeTime(post.getCreatedAt()),
                                post.getMediaType()
                        );
                        community.setId(post.getId());
                        community.setKtpa(post.getKtpa());
                        community.setCommentCount(post.getCommentCount());
                        communityList.add(community);
                    }
                    communityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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