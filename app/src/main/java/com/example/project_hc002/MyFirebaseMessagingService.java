package com.example.project_hc002;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FCM", "FirebaseMessagingService created");
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Message received: " + remoteMessage.getNotification().getTitle() + " - " + remoteMessage.getNotification().getBody());

//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();

        String eventTitle = remoteMessage.getData().get("event_title");
        String eventDesc = remoteMessage.getData().get("event_desc");
        String eventDate = remoteMessage.getData().get("event_date");
        String eventLoc = remoteMessage.getData().get("event_location");
        String eventKuota = remoteMessage.getData().get("event_kuota");
        String eventPicUri = remoteMessage.getData().get("event_pic_uri");

//        Log.d("FCM", "event_pic_uri: " + eventPicUri);

        showNotification(eventTitle, eventDesc, eventDate, eventLoc, eventKuota);
//        showNotification(eventTitle, eventDesc, eventDate, eventLoc, eventKuota, eventPicUri);
        saveNotification(eventTitle, eventDesc, eventDate, eventLoc, eventKuota, eventPicUri);
    }
    private void showNotification(String eventTitle, String eventDesc, String eventDate, String eventLoc, String eventKuota) {
        String channelId = "default_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Informasi Event Terbaru!")
                .setContentText(eventTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

//        if (eventPicUri != null && !eventPicUri.isEmpty()) {
//            Bitmap eventImage = decodeBase64(eventPicUri);
//            if (eventImage != null) {
//                builder.setLargeIcon(eventImage);
//                builder.setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(eventImage)
//                        .bigLargeIcon(null));
//            }
//        }
        Intent intent = new Intent(this, EventPage.class);
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("event_desc", eventDesc);
        intent.putExtra("event_date", eventDate);
        intent.putExtra("event_location", eventLoc);
        intent.putExtra("event_kuota", eventKuota);
//        intent.putExtra("event_pic_uri", eventPicUri);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }

//    private void showNotification(String eventTitle, String eventDesc, String eventDate, String eventLoc, String eventKuota, String eventPicUri) {
//        String channelId = "default_channel";
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_notifications)
//                .setContentTitle("Informasi Event Terbaru!")
//                .setContentText(eventTitle)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
////        if (eventPicUri != null && !eventPicUri.isEmpty()) {
////            Bitmap eventImage = decodeBase64(eventPicUri);
////            if (eventImage != null) {
////                builder.setLargeIcon(eventImage);
////                builder.setStyle(new NotificationCompat.BigPictureStyle()
////                        .bigPicture(eventImage)
////                        .bigLargeIcon(null));
////            }
////        }
//        if (eventPicUri != null && !eventPicUri.isEmpty()) {
//            Bitmap eventImage = decodeBase64(eventPicUri);
//            if (eventImage != null) {
//                builder.setLargeIcon(eventImage);
//                builder.setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(eventImage)
//                        .bigLargeIcon(null));
//            }
//        }
//        Intent intent = new Intent(this, EventPage.class);
//        intent.putExtra("event_title", eventTitle);
//        intent.putExtra("event_desc", eventDesc);
//        intent.putExtra("event_date", eventDate);
//        intent.putExtra("event_location", eventLoc);
//        intent.putExtra("event_kuota", eventKuota);
//        intent.putExtra("event_pic_uri", eventPicUri);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
//            manager.createNotificationChannel(channel);
//        }
//
//        manager.notify(0, builder.build());
//    }

    private void saveNotification(String eventTitle, String eventDesc, String eventDate, String eventLoc, String eventKuota, String eventPicUri) {
        Notif notif = new Notif(eventTitle, eventDesc, eventLoc, eventDate, eventKuota, eventPicUri);

        SharedPreferences sharedPref = getSharedPreferences("NotificationData", MODE_PRIVATE);
        String notificationsJson = sharedPref.getString("notifications_fcm", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Notif>>() {}.getType();
        List<Notif> notifs = gson.fromJson(notificationsJson, listType);

        if (notifs == null) {
            notifs = new ArrayList<>();
        }
        notifs.add(notif);

        String updatedNotificationsJson = gson.toJson(notifs);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("notifications_fcm", updatedNotificationsJson);
        editor.apply();

//        Notif notif = new Notif(eventTitle, eventDesc, eventLoc, eventDate, eventKuota, eventPicUri);
//
//        SharedPreferences sharedPref = getSharedPreferences("NotificationData", MODE_PRIVATE);
//        String notificationsJson = sharedPref.getString("notifications", "[]");
//
//        Gson gson = new Gson();
//        Type listType = new TypeToken<List<Notif>>() {}.getType();
//        List<Notif> notifs = gson.fromJson(notificationsJson, listType);
//
//        if (notifs == null) {
//            notifs = new ArrayList<>();
//        }
//        notifs.add(notif);
//
//        String updatedNotificationsJson = gson.toJson(notifs);
//
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("notifications", updatedNotificationsJson);
//        editor.apply();
    }


//    private Bitmap decodeBase64(String base64Str) {
//        try {
//            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
//            InputStream inputStream = new ByteArrayInputStream(decodedString);
//            return BitmapFactory.decodeStream(inputStream);
//        } catch (Exception e) {
//            Log.e("FCM", "Error decoding base64 image", e);
//            return null;
//        }
//    }
}
