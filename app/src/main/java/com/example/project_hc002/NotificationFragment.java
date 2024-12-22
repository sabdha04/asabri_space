package com.example.project_hc002;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView rvnotif;
    private List<Notif> notifs;
    private NotifAdapter notifAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvnotif = view.findViewById(R.id.notifrv);
        notifs = new ArrayList<>();

        notifAdapter = new NotifAdapter(notifs, getContext());
        rvnotif.setAdapter(notifAdapter);
        rvnotif.setLayoutManager(new LinearLayoutManager(getContext()));

        notifAdapter.setOnItemLongClickListener(position -> showDeleteConfirmationDialog(position));
        loadCombinedNotifications();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCombinedNotifications();  // Reload the data
            swipeRefreshLayout.setRefreshing(false);
        });

//        Button hapus = view.findViewById(R.id.hapus);
//        hapus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteAllNotifications();
//            }
//        });
        return view;
    }

    private void loadNotifications() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
        String notificationsJson = sharedPref.getString("notifications", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Notif>>() {}.getType();
        List<Notif> loadedNotifs = gson.fromJson(notificationsJson, listType);

        notifs.clear();
        Collections.reverse(loadedNotifs);
        notifs.addAll(loadedNotifs);
        notifAdapter.notifyDataSetChanged();
    }

    private void loadCombinedNotifications() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("NotificationData", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Notif>>() {}.getType();
//        String fcmJson = sharedPref.getString("notifications_fcm", "[]");
//        List<Notif> fcmNotifs = gson.fromJson(fcmJson, listType);

        String laterJson = sharedPref.getString("notifications_later", "[]");
        List<Notif> laterNotifs = gson.fromJson(laterJson, listType);

        List<Notif> allNotifs = new ArrayList<>();
//        if (fcmNotifs != null) {
//            allNotifs.addAll(fcmNotifs);
//        }
        if (laterNotifs != null) {
            allNotifs.addAll(laterNotifs);
        }

        Collections.reverse(allNotifs);

        if (allNotifs.size() > 10) {
            allNotifs = allNotifs.subList(0, 10);
        }

        notifs.clear();
        notifs.addAll(allNotifs);

        if (notifAdapter != null) {
            notifAdapter.notifyDataSetChanged();
        }
    }
//    private void deleteAllNotifications() {
//        SharedPreferences sharedPref = getActivity().getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.clear();
//        editor.apply();
//
//        notifs.clear();
//        notifAdapter.notifyDataSetChanged();
//    }

    private void showDeleteConfirmationDialog(int position) {
        // Create a custom dialog using the layout you provided
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_delete_notif);

        // Set up the buttons and actions
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(v -> {
            deleteNotification(position);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteNotification(int position) {
        notifs.remove(position);
        notifAdapter.notifyItemRemoved(position);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String updatedJson = gson.toJson(notifs);

        editor.putString("notifications_fcm", updatedJson);
        editor.apply();
    }

}
