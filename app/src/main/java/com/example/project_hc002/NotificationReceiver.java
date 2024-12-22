package com.example.project_hc002;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventTitle = intent.getStringExtra("event_title");
        //String eventDet = intent.getStringExtra("event_det");
        String eventDesc = intent.getStringExtra("event_desc");
        String eventLoc = intent.getStringExtra("event_location");
        String eventDate = intent.getStringExtra("event_date");
        String eventKuota = intent.getStringExtra("event_kuota");
        String imagePath = intent.getStringExtra("event_pic_uri");

        Notif notif = new Notif(eventTitle, eventDesc, eventLoc, eventDate, eventKuota, imagePath);

        SharedPreferences sharedPref = context.getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
        String notificationsJson = sharedPref.getString("notifications_later", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Notif>>() {}.getType();
        List<Notif> notifs = gson.fromJson(notificationsJson, listType);

        if (notifs == null) {
            notifs = new ArrayList<>();
        }
        notifs.add(notif);

        String updatedNotificationsJson = gson.toJson(notifs);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("notifications_later", updatedNotificationsJson);
        editor.apply();

//        SharedPreferences sharedPref = context.getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
//        String notificationsJson = sharedPref.getString("notifications", "[]");

//        Gson gson = new Gson();
//        Type listType = new TypeToken<List<Notif>>() {}.getType();
//        List<Notif> notifs = gson.fromJson(notificationsJson, listType);

//        Notif newNotif = new Notif(eventTitle, eventDesc, eventLoc, eventDate, eventKuota, imagePath);
//        notifs.add(newNotif);

//        String updatedNotificationsJson = gson.toJson(notifs);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("notifications", updatedNotificationsJson);
//        editor.apply();

        String chanelID = "CHANEL_NOTIF";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, chanelID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle("Event yang kamu tunggu sudah mulai!")
                        .setContentText("Jangan sampai ketinggalan "+eventTitle)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(context, EventPage.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("event_title", eventTitle);
        notificationIntent.putExtra("event_desc", eventDesc);
        notificationIntent.putExtra("event_date", eventDate);
        notificationIntent.putExtra("event_location", eventLoc);
        notificationIntent.putExtra("event_kuota", eventKuota);
        notificationIntent.putExtra("event_pic_uri", imagePath);
        Log.d("NotifReceiver", "Event Pic: " + imagePath);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(chanelID);

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID,
                        "Some description here", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }
}
