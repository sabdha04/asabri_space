package com.example.project_hc002;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostKomunPage extends AppCompatActivity {

    private EditText etPost;
    private Button btnPost;
    private ImageView ivBack, ivAddPhoto;
    private TextView tvAddPhoto, profileName;

    private static final String ARG_USER_NAME = "name";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Uri selectedImageUri = null;

    @SuppressLint("InlinedApi")
    private static final String[] PERMISSIONS_33_ABOVE = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    private static final String[] PERMISSIONS_BELOW_33 = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int MAX_VIDEO_DURATION_SECONDS = 30;
    private static final long MAX_VIDEO_SIZE_BYTES = 10 * 1024 * 1024; // 10MB limit
    private Uri selectedVideoUri = null;
    private String mediaType = ""; // "image" or "video"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_komun_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        etPost = findViewById(R.id.et_post);
        btnPost = findViewById(R.id.btn_post);
//        ivBack = view.findViewById(R.id.iv_back);
        ivAddPhoto = findViewById(R.id.iv_add_photo);
        tvAddPhoto = findViewById(R.id.tv_add_photo);
        profileName = findViewById(R.id.tv_username);
        String nama = sharedPreferences.getString("pesertaName", "");
        TextView profileName = findViewById(R.id.tv_username);
        profileName.setText(nama);
//        String userName = "Tegar";
//        profileName.setText(userName);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postContent();
            }
        });

        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

        tvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

    }

    private void postContent() {
        String postContent = etPost.getText().toString().trim();
        if (postContent.isEmpty()) {
            Toast.makeText(PostKomunPage.this, "Silakan tulis postingan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Encode content dengan UTF-8
            postContent = new String(postContent.getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String ktpa = sharedPreferences.getString("pesertaKtpa", "");
        int komunitasId = getIntent().getIntExtra("komun_id", -1);
        String currentTime = getCurrentTimestamp();
        String base64Media = "";

        if (selectedImageUri != null || selectedVideoUri != null) {
            try {
                Uri mediaUri = mediaType.equals("image") ? selectedImageUri : selectedVideoUri;
                if (mediaType.equals("image")) {
                    InputStream inputStream = getContentResolver().openInputStream(mediaUri);
                    byte[] mediaBytes = getBytes(inputStream);
                    base64Media = compressAndConvertToBase64(mediaBytes);
                    inputStream.close();
                } else if (mediaType.equals("video")) {
                    base64Media = compressVideo(mediaUri);
                }

                // Split payload if it's too large
                if (base64Media != null && base64Media.length() > 100000) { // 100KB chunks
                    List<String> chunks = splitString(base64Media, 100000);
                    for (int i = 0; i < chunks.size(); i++) {
                        PostComRequest postComRequest = new PostComRequest(
                                ktpa,
                                komunitasId,
                                postContent,
                                chunks.get(i),
                                mediaType + "_chunk_" + i + "_of_" + chunks.size(),
                                currentTime,
                                currentTime
                        );
                        sendPostRequest(postComRequest, i == chunks.size() - 1);
                    }
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(PostKomunPage.this, "Error processing media", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Send as single request if small enough
        PostComRequest postComRequest = new PostComRequest(
                ktpa,
                komunitasId,
                postContent,
                base64Media,
                mediaType,
                currentTime,
                currentTime
        );
        sendPostRequest(postComRequest, true);
    }

    private List<String> splitString(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(text.length(), i + chunkSize)));
        }
        return chunks;
    }
    private void sendPostRequest(PostComRequest postComRequest, boolean isLastChunk) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createPostKomun(postComRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (isLastChunk) {
                        Toast.makeText(PostKomunPage.this, "Postingan berhasil dibuat", Toast.LENGTH_SHORT).show();
                        etPost.setText("");
                        resetImageView();
                        finish();
//                        int komunitasId = getIntent().getIntExtra("komun_id", -1);
//                        MainActivity mainActivity = (MainActivity) getActivity();
//                        if (mainActivity != null) {
//                            mainActivity.pager.setCurrentItem(1);
//                        }
                    }
                } else {
                    Toast.makeText(PostKomunPage.this, "Anda belum bergabung dengan Komunitas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(PostKomunPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return "";

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void addPhoto() {
        if (checkPermission()) {
            openGallery();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
//            Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(PostKomunPage.this,
                    PERMISSIONS_33_ABOVE,
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(PostKomunPage.this,
                    PERMISSIONS_BELOW_33,
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    //handle result from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            String detectedType = getMimeType(selectedUri);

            if (detectedType.equals("image")) {
                selectedImageUri = selectedUri;
                selectedVideoUri = null;
                mediaType = "image";
                try {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(ivAddPhoto);
                    ivAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    tvAddPhoto.setText("Foto dipilih");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    resetImageView();
                }
            } else if (detectedType.equals("video")) {
                if (checkVideoConstraints(selectedUri)) {
                    selectedVideoUri = selectedUri;
                    selectedImageUri = null;
                    mediaType = "video";
                    try {
                        // Generate thumbnail from video
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(this, selectedUri);
                        Bitmap thumbnail = retriever.getFrameAtTime(1000000); // Get frame at 1 second

                        if (thumbnail != null) {
                            ivAddPhoto.setImageBitmap(thumbnail);
                        } else {
                            // Fallback to Glide if thumbnail extraction fails
                            Glide.with(this)
                                    .asBitmap()
                                    .load(selectedUri)
                                    .placeholder(R.drawable.youtube)
                                    .error(R.drawable.youtube)
                                    .into(ivAddPhoto);
                        }

                        ivAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        tvAddPhoto.setText("Video dipilih");
                        retriever.release();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading video thumbnail",
                                Toast.LENGTH_SHORT).show();
                        resetImageView();
                    }
                }
            } else {
                Toast.makeText(this, "Format file tidak didukung",
                        Toast.LENGTH_SHORT).show();
                resetImageView();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Add this new method for image compression
    private String compressAndConvertToBase64(byte[] imageBytes) {
        try {
            // Only process if we're dealing with an image
            if (mediaType.equals("image")) {
                Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (originalBitmap == null) {
                    throw new IllegalArgumentException("Failed to decode bitmap");
                }

                // Calculate new dimensions while maintaining aspect ratio
                int maxWidth = 800;
                int maxHeight = 800;
                float scale = Math.min(
                        ((float) maxWidth / originalBitmap.getWidth()),
                        ((float) maxHeight / originalBitmap.getHeight())
                );

                int newWidth = Math.round(originalBitmap.getWidth() * scale);
                int newHeight = Math.round(originalBitmap.getHeight() * scale);

                // Create scaled bitmap
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

                // Compress to file
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            } else {
                // If it's not an image, return the original bytes encoded
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(PostKomunPage.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Update resetImageView method
    private void resetImageView() {
        ivAddPhoto.setImageResource(R.drawable.image);
        ivAddPhoto.setScaleType(ImageView.ScaleType.CENTER);
        tvAddPhoto.setText("Tambah Foto/Video");
        selectedImageUri = null;
        selectedVideoUri = null;
        mediaType = "";
    }

    private void addVideo() {
        if (checkPermission()) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
        } else {
            requestPermission();
        }
    }

    private boolean checkVideoConstraints(Uri videoUri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = Long.parseLong(durationStr);
            long durationSeconds = durationMs / 1000;
            retriever.release();

            if (durationSeconds > MAX_VIDEO_DURATION_SECONDS) {
                Toast.makeText(PostKomunPage.this,
                        "Video terlalu panjang. Maksimal " + MAX_VIDEO_DURATION_SECONDS + " detik",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check file size
            Cursor cursor = getContentResolver().query(videoUri, null, null, null, null);
            if (cursor != null) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                long fileSize = cursor.getLong(sizeIndex);
                cursor.close();

                if (fileSize > MAX_VIDEO_SIZE_BYTES) {
                    Toast.makeText(PostKomunPage.this,
                            "Ukuran video terlalu besar. Maksimal 10MB",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PostKomunPage.this, "Error checking video constraints",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String getMimeType(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return "image";
            } else if (mimeType.startsWith("video/")) {
                return "video";
            }
        }
        return "";
    }

    private String compressVideo(Uri videoUri) {
        try {
            File inputFile = new File(PostKomunPage.this.getCacheDir(), "temp_input_video.mp4");
            File outputFile = new File(PostKomunPage.this.getCacheDir(), "temp_output_video.mp4");

            // Copy video to temp file
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            FileOutputStream fos = new FileOutputStream(inputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            inputStream.close();
            fos.close();

            // More aggressive compression settings
            String[] command = {
                    "-i", inputFile.getAbsolutePath(),
                    "-vf", "scale=240:180",    // Even smaller resolution
                    "-r", "15",                // Reduce framerate to 15fps
                    "-b:v", "100k",            // Very low video bitrate
                    "-c:v", "mpeg4",           // Use MPEG4 codec
                    "-ac", "1",                // Mono audio
                    "-ar", "22050",            // Lower audio sample rate
                    "-b:a", "32k",             // Very low audio bitrate
                    "-y",                      // Overwrite output file
                    outputFile.getAbsolutePath()
            };

            int rc = FFmpeg.execute(command);
            if (rc == RETURN_CODE_SUCCESS) {
                byte[] compressedBytes = Files.readAllBytes(outputFile.toPath());

                // Clean up temp files
                inputFile.delete();
                outputFile.delete();

                return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
            } else {
                Log.e("FFmpeg", "Compression failed with return code: " + rc);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}