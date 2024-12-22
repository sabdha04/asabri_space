package com.example.project_hc002;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EventPage extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView eventpic;
    TextView eventtitle, eventdesc, eventdate, eventloc, eventkuota;
    private boolean isReminderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eventDet), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

//        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (!Settings.canDrawOverlays(this)) {
//                // Request the permission if not granted
//                requestExactAlarmPermission();
//            }
//        }

        eventtitle = findViewById(R.id.txtEvName);
        eventpic = findViewById(R.id.imgGambar);
        eventdesc = findViewById(R.id.txtEvDesc);
        eventdate = findViewById(R.id.eventdate);
        eventloc = findViewById(R.id.eventloc);
        eventkuota = findViewById(R.id.eventkuota);

        Intent intent = getIntent();

        String dateev = intent.getStringExtra("event_date");
        eventtitle.setText(intent.getStringExtra("event_title"));
        eventdesc.setText(intent.getStringExtra("event_desc"));
        eventdate.setText(getRelativeTime(dateev));
        eventloc.setText(intent.getStringExtra("event_location"));
        eventkuota.setText(intent.getStringExtra("event_kuota"));

//        Log.d("EventPage", "event_pic_uri: " + imageBase64);
//        if (imageBase64 != null) {
//            Bitmap bitmap = decodeBase64(imageBase64);
//            if (bitmap != null) {
//                eventpic.setImageBitmap(bitmap);
//            } else {
//                Log.e("EventPage", "Failed to decode image.");
//            }
//        } else {
//            Log.e("EventPage", "No image found in intent.");
//        }
        String imagePath = intent.getStringExtra("event_pic_uri");
        Log.d("EventPage", "Event Pic: " + imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        eventpic.setImageBitmap(bitmap);


        Toolbar tb = findViewById(R.id.toolbar_det);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnNotif = findViewById(R.id.btnNotify);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifylater();
                makeNotif();
            }
        });

    }

    private void notifylater() {
//        if (isReminderSet) {
////            Toast.makeText(this, "Pengingat sudah diaktifkan!", Toast.LENGTH_SHORT).show();
//            Button btnNotif = findViewById(R.id.btnNotify);
//            btnNotif.setBackgroundColor(Color.GRAY);
//            btnNotif.setText("Pengingat Aktif");
//            btnNotif.setEnabled(false);
//            return;
//        }
        if (checkPermission()) {
            makeNotif();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!this.getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "No permission needed for this version", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (checkPermission()) {
//                makeNotif();
//            } else {
//                Toast.makeText(this, "Permission denied. Cannot schedule exact alarms.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.detail_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//        if (id == R.id.comments){
//            Intent intent = new Intent(this, EventComments.class);
//            startActivity(intent);
//            return true;
//        }
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    public void makeNotif() {
        String eventTitle = getIntent().getStringExtra("event_title");
        String eventDet = getIntent().getStringExtra("event_det");
        String eventDesc = getIntent().getStringExtra("event_desc");
        String eventLoc = getIntent().getStringExtra("event_location");
        String eventDate = getIntent().getStringExtra("event_date");
//        String eventDate = "2024-12-09 13:30";
        String eventKuota = getIntent().getStringExtra("event_kuota");
        String imagePath = getIntent().getStringExtra("event_pic_uri");

//        Integer eventPic = getIntent().getIntExtra("event_pic", 0);

        Log.d("Evdate", "EventDate:"+ eventDate);
        long triggerTime = parseDateToMillis(eventDate);
        Log.d("Evdate", "Trigger Time: " + triggerTime);
        Log.d("Evdate", "Current Time: " + System.currentTimeMillis());
        if (triggerTime > System.currentTimeMillis()) {
            scheduleNotification(triggerTime, eventTitle, eventDet, eventDesc, eventLoc, eventDate, eventKuota, imagePath);
            Toast.makeText(this, "Pengingat Event sudah aktif!", Toast.LENGTH_SHORT).show();
            Button btnNotif = findViewById(R.id.btnNotify);
            btnNotif.setBackgroundColor(Color.GRAY);
            btnNotif.setText("Pengingat Aktif");
            btnNotif.setEnabled(false);
//            Log.d("Evdate", "PendingIntent triggered at " + triggerTime);
        } else {
            Toast.makeText(this, "Maaf, event sudah selesai", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleNotification(long triggerTime, String eventTitle, String eventDet, String eventDesc, String eventLoc, String eventDate, String eventKuota, String imagePath) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("event_det", eventDet);
        intent.putExtra("event_desc", eventDesc);
        intent.putExtra("event_location", eventLoc);
        intent.putExtra("event_date", eventDate);
        intent.putExtra("event_kuota", eventKuota);
        intent.putExtra("event_pic_uri", imagePath);
        Log.d("Evdate", "EventDate:"+ eventDate);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
    private long parseDateToMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if (dateString.length() == 10) {
                dateString = dateString + " 08:00";
            }
            Date date = sdf.parse(dateString);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getRelativeTime(String date) {
        // Parse the input date from UTC
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date utcDate = utcFormat.parse(date);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return outputFormat.format(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Tanggal tidak valid";
        }
    }

//    untuk decode image
//    private Bitmap decodeBase64(String base64Str) {
//        try {
//            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
//            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        } catch (Exception e) {
//            Log.e("EventPage", "Error decoding base64 image", e);
//            return null;
//        }
//    }

}