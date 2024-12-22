package com.example.project_hc002;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityPage extends AppCompatActivity {

    ImageView pfp, imgpost, likeButton, edit, kom;
    TextView username, post, comment, likeCount, obrol, datecom,commentcount;
    RecyclerView rvcommentpost;
    private CommentPostAdapter commentPostAdapter;
    private List<CommentPost> commentPosts;
    private int postId;
    private boolean isLiked = false;
    private String userKtpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar tb = findViewById(R.id.toolbar_post);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.pusername);
        pfp = findViewById(R.id.pfpuser);
        datecom = findViewById(R.id.pdatetime);

        imgpost = findViewById(R.id.pimgpost);
        post = findViewById(R.id.pposting);
        likeButton = findViewById(R.id.postlike);
        likeCount = findViewById(R.id.likeCount);
        commentcount = findViewById(R.id.commentcount);
        edit = findViewById(R.id.editcomment);
        edit.setOnClickListener(v -> showPostOptions());
        String userp = getIntent().getStringExtra("username");
        String postp = getIntent().getStringExtra("post");
        String imgpostp = getIntent().getStringExtra("imgpost");
        String datepost = getIntent().getStringExtra("created_at");
       

        username.setText(userp);
        post.setText(postp);
        pfp.setImageResource(R.drawable.avatar);
        datecom.setText(datepost);

        String mediaFilePath = getIntent().getStringExtra("media_file_path");
        String mediaType = getIntent().getStringExtra("media_type");

        if (mediaFilePath != null) {
            File mediaFile = new File(mediaFilePath);
            if (mediaFile.exists()) {
                if ("video".equals(mediaType)) {
                    imgpost.setVisibility(View.GONE);
                    VideoView videoView = findViewById(R.id.pvideoView);
                    videoView.setVisibility(View.VISIBLE);
                    
                    try {
                        Uri videoUri = Uri.fromFile(mediaFile);
                        videoView.setVideoURI(videoUri);
                        
                        // Remove MediaController
                        videoView.setMediaController(null);
                        
                        videoView.setOnPreparedListener(mp -> {
                            // Set video to fit screen width while maintaining aspect ratio
                            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                            float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                            
                            float scale = videoRatio / screenRatio;
                            if (scale >= 1f) {
                                videoView.setScaleX(1f);
                                videoView.setScaleY(1f / scale);
                            } else {
                                videoView.setScaleX(scale);
                                videoView.setScaleY(1f);
                            }
                            
                            // Start video when in CommunityPage
                            videoView.start();
                        });
                        
                        // When video completes, show first frame and allow replay on tap
                        videoView.setOnCompletionListener(mp -> {
                            videoView.seekTo(1);
                        });
                        
                        // Add click listener for replay
                        videoView.setOnClickListener(v -> {
                            if (videoView.isPlaying()) {
                                videoView.pause();
                            } else {
                                videoView.seekTo(0);
                                videoView.start();
                            }
                        });
                        
                        videoView.setOnErrorListener((mp, what, extra) -> {
                            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
                            return true;
                        });
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle image display
                    imgpost.setVisibility(View.VISIBLE);
                    findViewById(R.id.pvideoView).setVisibility(View.GONE);
                    Glide.with(this)
                        .load(mediaFile)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(imgpost);
                }
            }
        }

        rvcommentpost = findViewById(R.id.rvcommentpost);
        commentPosts = new ArrayList<>();

        commentPostAdapter = new CommentPostAdapter(commentPosts, this);
        rvcommentpost.setAdapter(commentPostAdapter);
        rvcommentpost.setLayoutManager(new LinearLayoutManager(this));

        EditText commentInput;
        commentInput = findViewById(R.id.commentadd);
        ImageView sendButton;
        sendButton = findViewById(R.id.sendButton);

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId <= 0) {
            Toast.makeText(this, "Error: Invalid post ID: " + postId, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("CommunityPage", "Post ID: " + postId);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userKtpa = sharedPreferences.getString("pesertaKtpa", "");

        Log.d("CommunityPage", "User KTPA: " + userKtpa);

        EditText finalCommentInput = commentInput;
        sendButton.setColorFilter(ContextCompat.getColor(this, R.color.grey));

        // Add text change listener to EditText
        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Change color of send button based on whether text is entered
                if (charSequence.length() > 0) {
                    sendButton.setColorFilter(ContextCompat.getColor(CommunityPage.this, R.color.hanblue));
                } else {
                    sendButton.setColorFilter(ContextCompat.getColor(CommunityPage.this, R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        sendButton.setOnClickListener(v -> {
            String content = finalCommentInput.getText().toString().trim();
            if (!content.isEmpty() && !userKtpa.isEmpty()) {
                CommentRequest request = new CommentRequest(postId, userKtpa, content);
                createNewComment(request);
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            }
        });

        // Load comments for this post
        loadComments();

        // Setup like button
        likeButton.setOnClickListener(v -> toggleLike());

        // Load initial like status
        loadLikeStatus();

        // Get current user and post creator info
        String currentUserKtpa = sharedPreferences.getString("pesertaKtpa", "");
        String postCreatorKtpa = getIntent().getStringExtra("creator_ktpa");

        // Show edit button only if current user is the creator
        if (currentUserKtpa != null) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(v -> showPostOptions());
        } else {
            edit.setVisibility(View.GONE);
        }
    }

    private void loadComments() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<CommentResponse>> call = apiService.getComments(postId);

        call.enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentPosts.clear();
                    for (CommentResponse comment : response.body()) {
                        CommentPost commentPost = new CommentPost(
                            comment.getContent(),
                            comment.getNama(),
//                            comment.getCreatedAt(),
                            getRelativeTimeComment(comment.getCreatedAt()),
                            R.drawable.avatar // Default avatar
                        );
                        commentPosts.add(commentPost);
                    }
                    commentPostAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CommunityPage.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewComment(CommentRequest request) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<CommentResponse> call = apiService.createComment(request);

        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CommunityPage.this, "Komentar berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.commentadd)).setText("");
                    // Refresh comments
                    loadComments();
                } else {
                    Toast.makeText(CommunityPage.this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLikeStatus() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LikeStatusResponse> call = apiService.getLikeStatus(postId);

        call.enqueue(new Callback<LikeStatusResponse>() {
            @Override
            public void onResponse(Call<LikeStatusResponse> call, Response<LikeStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LikeStatusResponse status = response.body();
                    updateLikeUI(status.getLikeCount());
                    SharedPreferences sharedPreferences = getSharedPreferences("LikePrefs", MODE_PRIVATE);
                    boolean isLiked = sharedPreferences.getBoolean("isLiked_" + postId, false);

                    if (isLiked) {
                        likeButton.setImageResource(R.drawable.likefilled);
                        likeButton.setColorFilter(getResources().getColor(R.color.blue));
                    } else {
                        likeButton.setImageResource(R.drawable.likeoutline);
                        likeButton.clearColorFilter();
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeStatusResponse> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error loading like status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleLike() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LikeResponse> call = apiService.toggleLike(postId, new LikeRequest(userKtpa));

        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LikeResponse likeResponse = response.body();

                    boolean isLiked = likeResponse.isLiked();
                    SharedPreferences sharedPreferences = getSharedPreferences("LikePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLiked_" + postId, isLiked);
                    editor.apply();

                    if (likeResponse.isLiked()) {
                        likeButton.setImageResource(R.drawable.likefilled);
                        likeButton.setColorFilter(getResources().getColor(R.color.blue));
                    } else {
                        likeButton.setImageResource(R.drawable.likeoutline);
                        likeButton.clearColorFilter();
                    }
                    loadLikeStatus();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error toggling like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeUI(int count) {
        likeButton.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(100)
                .withEndAction(() -> {
                    likeButton.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                })
                .start();

        if (likeCount != null) {
            likeCount.animate()
                    .alpha(0f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        likeCount.setText(String.valueOf(count));
                        likeCount.animate()
                                .alpha(1f)
                                .setDuration(100)
                                .start();
                    })
                    .start();
        }
//        Log.d("CommunityPage", "Like status: " + liked + ", Like count: " + count);
    }
//    private void loadLikeStatus() {
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        Call<LikeStatusResponse> call = apiService.getLikeStatus(postId);
//
//        call.enqueue(new Callback<LikeStatusResponse>() {
//            @Override
//            public void onResponse(Call<LikeStatusResponse> call, Response<LikeStatusResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    LikeStatusResponse status = response.body();
//                    updateLikeUI(status.isUserLiked(), status.getLikeCount());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LikeStatusResponse> call, Throwable t) {
//                Toast.makeText(CommunityPage.this, "Error loading like status", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void toggleLike() {
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        Call<LikeResponse> call = apiService.toggleLike(postId, new LikeRequest(userKtpa));
//
//        call.enqueue(new Callback<LikeResponse>() {
//            @Override
//            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Refresh like status after toggling
//                    loadLikeStatus();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LikeResponse> call, Throwable t) {
//                Toast.makeText(CommunityPage.this, "Error toggling like", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateLikeUI(boolean liked, int count) {
//        isLiked = liked;
//
//        // Animate the like button
//        likeButton.animate()
//            .scaleX(0.7f)
//            .scaleY(0.7f)
//            .setDuration(100)
//            .withEndAction(() -> {
//                // Change the image
//                if (liked) {
//                    likeButton.setImageResource(R.drawable.likefilled);
//                    likeButton.setColorFilter(getResources().getColor(R.color.blue));
//                } else {
//                    likeButton.setImageResource(R.drawable.likeoutline);
//                    likeButton.clearColorFilter();
//                }
//
//                // Animate back to original size
//                likeButton.animate()
//                    .scaleX(1f)
//                    .scaleY(1f)
//                    .setDuration(100)
//                    .start();
//            })
//            .start();
//
//        // Update like count with animation
//        if (likeCount != null) {
//            likeCount.animate()
//                .alpha(0f)
//                .setDuration(100)
//                .withEndAction(() -> {
//                    likeCount.setText(String.valueOf(count));
//                    likeCount.animate()
//                        .alpha(1f)
//                        .setDuration(100)
//                        .start();
//                })
//                .start();
//        }
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    private void showPostOptions() {
        PopupMenu popup = new PopupMenu(this, edit);
        popup.inflate(R.menu.post_option_menu);
        
        // Get current user KTPA
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String currentUserKtpa = sharedPreferences.getString("pesertaKtpa", "");
        String postCreatorKtpa = getIntent().getStringExtra("creator_ktpa");
        
        // Only show options if current user is the creator
        if (!currentUserKtpa.equals(postCreatorKtpa)) {
            return;
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit_post) {
                showEditPostDialog();
                return true;
            } else if (itemId == R.id.menu_delete_post) {
                showDeleteConfirmation();
                return true;
            }
            return false;
        });
        
        popup.show();
    }

    private void showEditPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_post, null);
        EditText editText = dialogView.findViewById(R.id.edit_post_content);
        editText.setText(post.getText());

        builder.setView(dialogView)
               .setTitle("Edit Post")
               .setPositiveButton("Simpan", (dialog, which) -> {
                   String newContent = editText.getText().toString().trim();
                   if (!newContent.isEmpty()) {
                       updatePost(newContent);
                   } else {
                       Toast.makeText(this, "Konten post tidak boleh kosong", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Batal", null)
               .show();
    }

    private void updatePost(String newContent) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        UpdatePostRequest request = new UpdatePostRequest(newContent);
        
        Call<PostResponse> call = apiService.updatePost(postId, request);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CommunityPage.this, "Post berhasil diupdate", Toast.LENGTH_SHORT).show();
                    // Update UI
                    post.setText(newContent);
                    // Refresh current page
                    loadComments();
                    // Set result to refresh CommunityFragment
                    setResult(RESULT_OK);

                } else {
                    Toast.makeText(CommunityPage.this, "Gagal update post: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Hapus Post")
            .setMessage("Apakah anda yakin ingin menghapus post ini?")
            .setPositiveButton("Hapus", (dialog, which) -> deletePost())
            .setNegativeButton("Batal", null)
            .show();
    }

    private void deletePost() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        // First delete all likes for this post
        Call<Void> deleteLikesCall = apiService.deleteLikesForPost(postId);
        deleteLikesCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // After likes are deleted, delete all comments
                    Call<Void> deleteCommentsCall = apiService.deleteCommentsForPost(postId);
                    deleteCommentsCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Finally delete the post itself
                                Call<Void> deletePostCall = apiService.deletePost(postId);
                                deletePostCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(CommunityPage.this, "Post berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(CommunityPage.this, "Gagal menghapus post", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(CommunityPage.this, "Error menghapus post: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(CommunityPage.this, "Gagal menghapus komentar", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(CommunityPage.this, "Error menghapus komentar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CommunityPage.this, "Gagal menghapus likes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CommunityPage.this, "Error menghapus likes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRelativeTime(String createdAt) {
        // Parse the input date from UTC
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date utcDate = utcFormat.parse(createdAt); // Parse UTC date

            // Get time difference
            long diffInMillis = System.currentTimeMillis() - utcDate.getTime();

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

    private String getRelativeTimeComment(String createdAt) {
        // Parse the input date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date utcDate = dateFormat.parse(createdAt);

            long timeInMillisWithOffset = utcDate.getTime() + TimeUnit.HOURS.toMillis(7);

            long diffInMillis = System.currentTimeMillis() - timeInMillisWithOffset;

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up temporary files
        cleanupTempFiles();
    }

    private void cleanupTempFiles() {
        File cacheDir = getCacheDir();
        File[] files = cacheDir.listFiles((dir, name) -> 
            name.startsWith("temp_media_") && (name.endsWith(".mp4") || name.endsWith(".jpg")));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

}