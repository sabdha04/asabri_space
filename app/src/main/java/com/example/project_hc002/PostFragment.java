package com.example.project_hc002;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import android.content.ContentValues;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.database.Cursor;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import android.media.MediaMetadataRetriever;
import android.provider.OpenableColumns;
import android.media.ThumbnailUtils;
import android.graphics.BitmapFactory.Options;
import android.util.Size;
import com.bumptech.glide.Glide;
import android.media.MediaCodec;
import android.media.MediaFormat;
import java.nio.ByteBuffer;
import com.arthenica.mobileffmpeg.FFmpeg;
import android.util.Log;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class PostFragment extends Fragment {

    private EditText etPost;
    private Button btnPost;
    private ImageView ivBack, ivAddPhoto;
    private TextView tvAddPhoto, profileName;

    private static final String ARG_USER_NAME = "name";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 3;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Uri selectedImageUri = null;

    @SuppressLint("InlinedApi")
    private static final String[] PERMISSIONS_33_ABOVE = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA
    };

    private static final String[] PERMISSIONS_BELOW_33 = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int MAX_VIDEO_DURATION_SECONDS = 30;
    private static final long MAX_VIDEO_SIZE_BYTES = 10 * 1024 * 1024; // 10MB limit
    private Uri selectedVideoUri = null;
    private String mediaType = ""; // "image" or "video"

    public static PostFragment newInstance(String userName) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.postFragment), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        etPost = view.findViewById(R.id.et_post);
        btnPost = view.findViewById(R.id.btn_post);
        ivAddPhoto = view.findViewById(R.id.iv_add_photo);
        tvAddPhoto = view.findViewById(R.id.tv_add_photo);
        profileName = view.findViewById(R.id.tv_username);
        String nama = sharedPreferences.getString("pesertaName", "");
        TextView profileName = view.findViewById(R.id.tv_username);
        profileName.setText(nama);
        // Set the user's name from the same source as ProfileActivity
        if (getArguments() != null) {
            String userName = getArguments().getString(ARG_USER_NAME, "Tegar");
            profileName.setText(userName);
        }

//        Toolbar tb = view.findViewById(R.id.toolbar_post);
//        if (getActivity() instanceof AppCompatActivity) {
//            AppCompatActivity activity = (AppCompatActivity) getActivity();
//            activity.setSupportActionBar(tb);
//            if (activity.getSupportActionBar() != null) {
//                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            }
//        }
//        tb.setNavigationOnClickListener(v -> {
//            MainActivity mainActivity = (MainActivity) getActivity();
//            if (mainActivity != null) {
//                mainActivity.pager.setCurrentItem(1); // Navigate to the second page (index 1) in the ViewPager
//            }
//        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postContent();
            }
        });

        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceOptions();
            }
        });

        tvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceOptions();
            }
        });

        return view;
    }

    private void postContent() {
        String postContent = etPost.getText().toString().trim();
        if (postContent.isEmpty()) {
            Toast.makeText(getActivity(), "Silakan tulis postingan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Encode content dengan UTF-8
            postContent = new String(postContent.getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String ktpa = sharedPreferences.getString("pesertaKtpa", "");
        String currentTime = getCurrentTimestamp();
        String base64Media = "";

        if (selectedImageUri != null || selectedVideoUri != null) {
            try {
                Uri mediaUri = mediaType.equals("image") ? selectedImageUri : selectedVideoUri;
                if (mediaType.equals("image")) {
                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(mediaUri);
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
                        PostRequest postRequest = new PostRequest(
                                ktpa,
                                postContent,
                                currentTime,
                                currentTime,
                                chunks.get(i),
                                mediaType + "_chunk_" + i + "_of_" + chunks.size()
                        );
                        sendPostRequest(postRequest, i == chunks.size() - 1);
                    }
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error processing media", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Send as single request if small enough
        PostRequest postRequest = new PostRequest(
                ktpa,
                postContent,
                currentTime,
                currentTime,
                base64Media,
                mediaType
        );
        sendPostRequest(postRequest, true);
    }

    private List<String> splitString(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; text != null && i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(text.length(), i + chunkSize)));
        }
        return chunks;
    }

    private void sendPostRequest(PostRequest postRequest, boolean isLastChunk) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createPost(postRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (isLastChunk) {
                        Toast.makeText(getActivity(), "Postingan berhasil dibuat", Toast.LENGTH_SHORT).show();
                        etPost.setText("");
                        resetImageView();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            mainActivity.pager.setCurrentItem(1);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Gagal membuat postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
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
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(),
                    PERMISSIONS_33_ABOVE,
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
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

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }
    private void handleSelectedMedia(Uri selectedUri) {
        String detectedType = getMimeType(selectedUri);

        if (detectedType.equals("image")) {
            selectedImageUri = selectedUri;
            selectedVideoUri = null;
            mediaType = "image";
            try {
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(ivAddPhoto);
                ivAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tvAddPhoto.setText("Foto dipilih");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                resetImageView();
            }
        } else if (detectedType.equals("video")) {
            if (checkVideoConstraints(selectedUri)) {
                selectedVideoUri = selectedUri;
                selectedImageUri = null;
                mediaType = "video";
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(requireContext(), selectedUri);
                    Bitmap thumbnail = retriever.getFrameAtTime(1000000); // Get frame at 1 second

                    if (thumbnail != null) {
                        ivAddPhoto.setImageBitmap(thumbnail);
                    } else {
                        Glide.with(requireContext())
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
                    Toast.makeText(requireContext(), "Error loading video thumbnail", Toast.LENGTH_SHORT).show();
                    resetImageView();
                }
            }
        } else {
            Toast.makeText(requireContext(), "Format file tidak didukung", Toast.LENGTH_SHORT).show();
            resetImageView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                Uri selectedUri = data.getData();
                handleSelectedMedia(selectedUri);
            } else if (requestCode == CAMERA_REQUEST_CODE && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                handleCapturedPhoto(photo);
            }
        }
    }

    private void handleCapturedPhoto(Bitmap photo) {
        try {
            Uri photoUri = savePhotoToInternalStorage(photo);
            selectedImageUri = photoUri;
            selectedVideoUri = null;
            mediaType = "image";
            ivAddPhoto.setImageBitmap(photo);
            ivAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tvAddPhoto.setText("Foto dipilih");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error processing photo", Toast.LENGTH_SHORT).show();
            resetImageView();
        }
    }

    private Uri savePhotoToInternalStorage(Bitmap photo) throws IOException {
        File photoFile = new File(requireContext().getFilesDir(), "captured_photo.jpg");
        FileOutputStream fos = new FileOutputStream(photoFile);
        photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return Uri.fromFile(photoFile);
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

    private String compressAndConvertToBase64(byte[] imageBytes) {
        try {
            if (mediaType.equals("image")) {
                Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (originalBitmap == null) {
                    throw new IllegalArgumentException("Failed to decode bitmap");
                }
                int maxWidth = 800;
                int maxHeight = 800;
                float scale = Math.min(
                        ((float) maxWidth / originalBitmap.getWidth()),
                        ((float) maxHeight / originalBitmap.getHeight())
                );

                int newWidth = Math.round(originalBitmap.getWidth() * scale);
                int newHeight = Math.round(originalBitmap.getHeight() * scale);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            } else {
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
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
            retriever.setDataSource(requireContext(), videoUri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = Long.parseLong(durationStr);
            long durationSeconds = durationMs / 1000;
            retriever.release();

            if (durationSeconds > MAX_VIDEO_DURATION_SECONDS) {
                Toast.makeText(requireContext(),
                        "Video terlalu panjang. Maksimal " + MAX_VIDEO_DURATION_SECONDS + " detik",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            Cursor cursor = requireContext().getContentResolver().query(videoUri, null, null, null, null);
            if (cursor != null) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                long fileSize = cursor.getLong(sizeIndex);
                cursor.close();

                if (fileSize > MAX_VIDEO_SIZE_BYTES) {
                    Toast.makeText(requireContext(),
                            "Ukuran video terlalu besar. Maksimal 10MB",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error checking video constraints",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String getMimeType(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
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
            File inputFile = new File(requireContext().getCacheDir(), "temp_input_video.mp4");
            File outputFile = new File(requireContext().getCacheDir(), "temp_output_video.mp4");

            InputStream inputStream = requireActivity().getContentResolver().openInputStream(videoUri);
            FileOutputStream fos = new FileOutputStream(inputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            inputStream.close();
            fos.close();

            String[] command = {
                    "-i", inputFile.getAbsolutePath(),
                    "-vf", "scale=240:180",
                    "-r", "15",
                    "-b:v", "100k",
                    "-c:v", "mpeg4",
                    "-ac", "1",
                    "-ar", "22050",
                    "-b:a", "32k",
                    "-y",
                    outputFile.getAbsolutePath()
            };

            int rc = FFmpeg.execute(command);
            if (rc == RETURN_CODE_SUCCESS) {
                byte[] compressedBytes = Files.readAllBytes(outputFile.toPath());
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

    private void showImageSourceOptions() {
        CharSequence[] options = {"Pilih dari Galeri", "Ambil Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pilih Sumber Gambar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    addPhoto();
                } else if (which == 1) {
                    openCamera();
                }
            }
        });
        builder.show();
    }
}
