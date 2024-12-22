package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.notifikasi> {
    private List<Notif> notifs;
    Context context;

    private OnItemLongClickListener longClickListener;

    public NotifAdapter(List<Notif> notifs, Context context) {
        this.notifs = notifs;
        this.context = context;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    @NonNull
    @Override
    public notifikasi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View view = layoutInflater.inflate(R.layout.item_comments, null);
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);
        return new notifikasi(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notifikasi holder, int position) {
        Notif notif = notifs.get(position);
        holder.titlenotif.setText(notif.getNotiftitle());
        holder.descnotif.setText(notif.getNotifdesc());
//        String getcount = getTimeAgo(notif.getNotifdate());
//        holder.countdown.setText(getcount);
        holder.notiflay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EventPage.class);
                intent.putExtra("event_title", notifs.get(position).getNotiftitle());
                intent.putExtra("event_desc", notifs.get(position).getNotifdesc());
                intent.putExtra("event_kuota", notifs.get(position).getNotifkuota());
                intent.putExtra("event_date", notifs.get(position).getNotifdate());
                intent.putExtra("event_location", notifs.get(position).getNotifloc());
                intent.putExtra("event_pic_uri", notifs.get(position).getNotifpic());
                view.getContext().startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });
//        holder.notiflay.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                notifs.remove(position);
//                notifyItemRemoved(position);
//
//                saveNotificationsToSharedPreferences();
//                return true;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    public class notifikasi extends RecyclerView.ViewHolder{
        TextView titlenotif, descnotif, countdown;
        public ConstraintLayout notiflay;
        public notifikasi(@NonNull View itemView) {
            super(itemView);
            titlenotif = itemView.findViewById(R.id.notiftitle);
            descnotif = itemView.findViewById(R.id.notifdesc);
            countdown = itemView.findViewById(R.id.countdown);
            notiflay = itemView.findViewById(R.id.notif_item);
//            itemView.setOnLongClickListener(v -> {
//                if (onItemLongClickListener != null) {
//                    onItemLongClickListener.onItemLongClick(getAdapterPosition());
//                }
//                return true;
//            });
        }
    }

    private String getTimeAgo(String notifDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if (notifDate.length() == 10) {
                notifDate = notifDate + " 10:52";
            }
            Date notifTime = format.parse(notifDate);
            long diffInMillis = System.currentTimeMillis() - notifTime.getTime();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (seconds < 60) {
                return seconds + " detik lalu";
            } else
            if (minutes < 60) {
                return minutes + " menit lalu";
            } else if (hours < 24) {
                return hours + " jam lalu";
            } else {
                return days + " hari lalu";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void saveNotificationsToSharedPreferences() {
        SharedPreferences sharedPref = context.getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(notifs);
        editor.putString("notifications", json);
        editor.apply();
    }
}
