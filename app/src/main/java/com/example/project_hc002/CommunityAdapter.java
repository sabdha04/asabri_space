package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.comAdapter> {
    private List<Community> listCom;
    Context context;
    private static VideoView currentlyPlayingVideo = null; // Track current playing video

    public CommunityAdapter(List<Community> listCom, Context context) {
        this.listCom = listCom;
        this.context = context;
    }

    @NonNull
    @Override
    public comAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_post, parent, false);
        return new comAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull comAdapter holder, int position) {
        Community com = listCom.get(position);

        holder.username.setText(com.getNama());
        holder.post.setText(com.getPost());
        holder.date.setText(com.getCreated_at());
        holder.pfpuser.setImageResource(R.drawable.avatar);
        holder.obrol.setText("AYO Ikut Obrolan");

        if (holder.commentCount != null) {
            int count = com.getCommentCount();
            holder.commentCount.setText(String.valueOf(count));
        }

        if (com.getImgpost() != null && !com.getImgpost().isEmpty()) {
            String mediaType = com.getMediaType();

            if ("video".equals(mediaType)) {
                holder.imgpost.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.postparent.setVisibility(View.VISIBLE);
                holder.videoLoadingIndicator.setVisibility(View.VISIBLE);

                try {
                    byte[] decodedBytes = Base64.decode(com.getImgpost(), Base64.DEFAULT);
                    File outputDir = context.getCacheDir();
                    File videoFile = new File(outputDir, "post_" + com.getId() + ".mp4");
                    FileOutputStream fos = new FileOutputStream(videoFile);
                    fos.write(decodedBytes);
                    fos.close();
                    Uri videoUri = Uri.fromFile(videoFile);
                    holder.videoView.setVideoURI(videoUri);
                    holder.videoView.setMediaController(null);  // Remove MediaController

                    holder.videoView.setOnPreparedListener(mp -> {
                        holder.videoLoadingIndicator.setVisibility(View.GONE);
                        holder.videoView.seekTo(1);
                    });

                    holder.videoView.setOnClickListener(v -> {
                        if (holder.videoView.isPlaying()) {
                            holder.videoView.pause();
                        } else {
                            if (currentlyPlayingVideo != null && currentlyPlayingVideo != holder.videoView) {
                                currentlyPlayingVideo.pause();
                                currentlyPlayingVideo.seekTo(1);
                            }
                            holder.videoView.start();
                            currentlyPlayingVideo = holder.videoView;
                        }
                    });

                    holder.videoView.setOnCompletionListener(mp -> {
                        holder.videoView.seekTo(1);
                        currentlyPlayingVideo = null;
                    });

                    holder.videoView.seekTo(1);

                } catch (Exception e) {
                    e.printStackTrace();
                    holder.postparent.setVisibility(View.GONE);
                    holder.videoLoadingIndicator.setVisibility(View.GONE);
                }

            } else { // Image
                holder.imgpost.setVisibility(View.VISIBLE);
                holder.videoView.setVisibility(View.GONE);
                holder.postparent.setVisibility(View.VISIBLE);

                try {
                    // Existing image handling code
                    byte[] decodedString = Base64.decode(com.getImgpost(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Bitmap compressedBitmap = getResizedBitmap(bitmap, 1080);
                    holder.imgpost.setImageBitmap(compressedBitmap);
                    holder.imgpost.setAdjustViewBounds(true);
                    holder.imgpost.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.postparent.setVisibility(View.GONE);
                }
            }
        } else {
            holder.postparent.setVisibility(View.GONE);
            holder.imgpost.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
        }

        holder.mainlay.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityPage.class);
            intent.putExtra("post_id", com.getId());
            intent.putExtra("username", com.getNama());
            intent.putExtra("post", com.getPost());
            intent.putExtra("created_at", com.getCreated_at());
            intent.putExtra("media_type", com.getMediaType());
            intent.putExtra("creator_ktpa", com.getKtpa());

            if (com.getImgpost() != null && !com.getImgpost().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(com.getImgpost(), Base64.DEFAULT);
                    File outputDir = context.getCacheDir();
                    String fileName = "temp_media_" + com.getId() +
                                    (com.getMediaType().equals("video") ? ".mp4" : ".jpg");
                    File mediaFile = new File(outputDir, fileName);

                    FileOutputStream fos = new FileOutputStream(mediaFile);
                    fos.write(decodedBytes);
                    fos.close();

                    intent.putExtra("media_file_path", mediaFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading media", Toast.LENGTH_SHORT).show();
                }
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listCom != null ? listCom.size() : 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull comAdapter holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.videoView.getVisibility() == View.VISIBLE) {
            holder.videoView.pause();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull comAdapter holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.videoView.getVisibility() == View.VISIBLE) {
            holder.videoView.pause();
            holder.videoView.seekTo(1);
            if (currentlyPlayingVideo == holder.videoView) {
                currentlyPlayingVideo = null;
            }
        }
    }

    static class comAdapter extends RecyclerView.ViewHolder {
        public ConstraintLayout mainlay;
        public CardView postparent;
        TextView username, post, date, obrol, commentCount;
        ImageView imgpost, pfpuser, edit;
        VideoView videoView;
        ProgressBar videoLoadingIndicator;

        public comAdapter(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            post = itemView.findViewById(R.id.posting);
            obrol = itemView.findViewById(R.id.obrol);
            imgpost = itemView.findViewById(R.id.imgpost);
            videoView = itemView.findViewById(R.id.videoView);
            pfpuser = itemView.findViewById(R.id.pfpuser);
            postparent = itemView.findViewById(R.id.postparent);
            mainlay = itemView.findViewById(R.id.postLayout);
            date = itemView.findViewById(R.id.datetime);
            videoLoadingIndicator = itemView.findViewById(R.id.videoLoadingIndicator);
            commentCount = itemView.findViewById(R.id.commentcount);
        }
    }

    // Helper method to resize bitmap
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
