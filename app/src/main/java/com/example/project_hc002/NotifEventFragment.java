package com.example.project_hc002;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotifEventFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView rvnotifev;
    private List<Notif> notifs;
    private NotifAdapter notifAdapter;

    public NotifEventFragment() {
        // Required empty public constructor
    }

    public static NotifEventFragment newInstance(String param1, String param2) {
        NotifEventFragment fragment = new NotifEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notif_event, container, false);

        rvnotifev = view.findViewById(R.id.notifrvev);
        notifs = new ArrayList<>();

        notifAdapter = new NotifAdapter(notifs, getContext());
        rvnotifev.setAdapter(notifAdapter);
        rvnotifev.setLayoutManager(new LinearLayoutManager(getContext()));
        notifAdapter.setOnItemLongClickListener(position -> showDeleteConfirmationDialog(position));

        loadFcmNotifications();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadFcmNotifications();
            swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }

    private void loadFcmNotifications() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("NotificationData",
                Context.MODE_PRIVATE);

        String fcmJson = sharedPref.getString("notifications_fcm", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Notif>>() {}.getType();
        List<Notif> fcmNotifs = gson.fromJson(fcmJson, listType);

        Collections.reverse(fcmNotifs);
        if (fcmNotifs.size() > 10) {
            fcmNotifs = fcmNotifs.subList(0, 10);
        }

        if (fcmNotifs != null) {
            notifs.clear();
            notifs.addAll(fcmNotifs);
            notifAdapter.notifyDataSetChanged();
        }
    }

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