package com.example.project_hc002;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ForumAdapterHelper {
    private RecyclerView recyclerView;
    private List<Community> communityList;
    private CommunityAdapter communityAdapter;

    public ForumAdapterHelper(RecyclerView recyclerView, Context context, int limit) {
        this.recyclerView = recyclerView;
        this.communityList = new ArrayList<>();


        setupRecyclerView(context);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Post>> call = apiService.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    communityList.clear();
                    int count = 0;

                    for (Post post : response.body()) {
                        if (count >= limit) {
                            break;
                        }
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
                        count++;
                    }
                    communityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(recyclerView.getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(recyclerView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void initializeData(int limit) {
//        communityList.add(new Community("runaway", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut libero mauris, semper a nulla sit amet, luctus tincidunt dolor. Nulla facilisi. Vivamus nec auctor lectus.", R.drawable.vector003, R.drawable.vector003));
//        communityList.add(new Community("user325", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...", null, R.drawable.vector002));
//        communityList.add(new Community("makansiang", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi. Vivamus nec auctor lectus.", R.drawable.vector002, R.drawable.vector003));
//        communityList.add(new Community("powerbank", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", R.drawable.vector002, R.drawable.vector002));
//        communityList.add(new Community("lixie", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi. Vivamus nec auctor lectus.", null, R.drawable.vector003));
//        communityList.add(new Community("pixie", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", null, R.drawable.vector002));
//
//        if (communityList.size() > limit) {
//            communityList = communityList.subList(0, limit);
//        }
//    }

    private void setupRecyclerView(Context context) {
        communityAdapter = new CommunityAdapter(communityList, context);
        recyclerView.setAdapter(communityAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
