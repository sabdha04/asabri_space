package com.example.project_hc002;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostEvFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText evtitle, evdesc, evdate, evloc, evkuota;
    private Button btnSubmit;
    ImageView imgevpic;
    private Uri imageUri;

    private static final int PERMISSION_REQUEST_CODE = 200;
    @SuppressLint("InlinedApi")
    private static final String[] PERMISSIONS_33_ABOVE = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    private static final String[] PERMISSIONS_BELOW_33 = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public PostEvFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_ev, container, false);
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.addevent), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed" : "Subscription failed";
                    Log.d("FCM", msg);
                });

//        checkStoragePermissions();
        imgevpic = view.findViewById(R.id.upeventpic);

        imgevpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
//                openFileChooser();
            }
        });

        evtitle = view.findViewById(R.id.feventname);
        evdesc = view.findViewById(R.id.feventdesc);
        evdate = view.findViewById(R.id.feventdate);
        evloc = view.findViewById(R.id.feventloc);
        evkuota = view.findViewById(R.id.feventquota);
        btnSubmit = view.findViewById(R.id.btnaddevent);

        evdate.setOnClickListener(v -> {
            showDatePicker();
        });
        btnSubmit.setOnClickListener(v -> {
            submitpost();
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imgevpic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addPhoto() {
        if (checkPermission()) {
            openFileChooser();
        } else {
            requestPermission();
        }
    }

    private void submitpost() {
        if (checkNotificationPermission()) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String ktpa = sharedPreferences.getString("pesertaKtpa", "");
            String created_at = getCurrentTimestamp();

            String title = evtitle.getText().toString().trim();
            String description = evdesc.getText().toString().trim();
            String date = evdate.getText().toString().trim();
            String loc = evloc.getText().toString().trim();
            String kuota = evkuota.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                String image = imageUri != null ? imageUri.toString() : "";
                sendDataToServer(ktpa, title, description, date, loc, kuota, image, created_at);
            }
        } else {
            requestNotificationPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
//            Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Storage permissions are required to continue", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 2) { // Untuk izin notifikasi
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Notification permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Notification permissions are required to send notifications", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }


//    private void checkStoragePermissions() {
//        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    1);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getActivity(), "Storage permissions granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getActivity(), "Storage permissions are required to continue", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void sendDataToServer(String ktpa, String title, String description, String date, String loc, String kuota, String image, String created_at) {
//        String backendUrl = "http://192.168.70.60:3000/api/postev"; // local sa
//        String backendUrl = "http://192.168.202.88:3000/api/postev"; // rakha punya
        String backendUrl = "http://192.168.2.212:3000/api/postev"; // sa di asabri

        File imageFile = new File(getRealPathFromURI(imageUri));
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                .addFormDataPart("ktpa", ktpa)
                .addFormDataPart("title", title)
                .addFormDataPart("description", description)
                .addFormDataPart("date", date)
                .addFormDataPart("loc", loc)
                .addFormDataPart("kuota", kuota)
                .addFormDataPart("created_at", created_at)
                .build();

        Request request = new Request.Builder()
                .url(backendUrl)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Failed to send data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Post sent successfully!", Toast.LENGTH_SHORT).show();
                        clearForm();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return uri.getPath();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    showTimePicker(year1, month1, dayOfMonth);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute1) -> {
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(year, month, day, hourOfDay, minute1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    evdate.setText(sdf.format(selectedDateTime.getTime()));
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    private void clearForm() {
        EditText evname = getView().findViewById(R.id.feventname);
        EditText evdesc = getView().findViewById(R.id.feventdesc);
        EditText evdate = getView().findViewById(R.id.feventdate);
        EditText evloc = getView().findViewById(R.id.feventloc);
        EditText evq = getView().findViewById(R.id.feventquota);

        evname.setText("");
        evdesc.setText("");
        evdate.setText("");
        evloc.setText("");
        evq.setText("");
        ImageView imgevpic = getView().findViewById(R.id.upeventpic);
        imgevpic.setImageResource(R.drawable.addimage);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
