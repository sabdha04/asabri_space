package com.example.project_hc002;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CommunityFragment extends Fragment {
    private static final String TAG = "CommunityFragment";
    
    private RecyclerView rvcomm, rvcomgroup;
    private CommunityAdapter communityAdapter;
    private List<Community> communityList;
    private ComGroupAdapter comGroupAdapter;
    private List<ComGroup> comGroups;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.commFragment), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Initialize views
        rvcomm = view.findViewById(R.id.rvcommunityfr);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

//        Toolbar tb = view.findViewById(R.id.toolbar_com);
//        if (getActivity() instanceof AppCompatActivity) {
//            AppCompatActivity activity = (AppCompatActivity) getActivity();
//            activity.setSupportActionBar(tb);
//            if (activity.getSupportActionBar() != null) {
//                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
////                activity.getSupportActionBar().setTitle("Notification");
////                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            }
//        }

//        rvcomgroup = view.findViewById(R.id.rvcommunityfr2);
//        rvcomgroup.setLayoutManager(new LinearLayoutManager(getContext()));
//        comGroups = new ArrayList<>();
//        populatecomm();
//        comGroupAdapter = new ComGroupAdapter(comGroups, getContext());
//        rvcomgroup.setAdapter(comGroupAdapter);

        // Setup RecyclerView
        rvcomm.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(communityList, getContext());
        rvcomm.setAdapter(communityAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        // Initial load
        loadPosts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }
//    private void populatecomm() {
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//        comGroups.add(new ComGroup("Family Gathering 2025",
//                "Alumni 95 Semarang", R.drawable.vector003));
//    }

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

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
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