package com.example.project_hc002.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_hc002.R;
import com.example.project_hc002.models.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context context;
    private final List<Message> messageList;
    private final String loggedInUser;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;

        // Ambil pengguna login dari SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        this.loggedInUser = sharedPreferences.getString("pesertaName", "Anonymous");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Format waktu untuk ditampilkan
        String formattedTime = formatTimestamp(message.getCreatedAt());

        // Debugging untuk memastikan format ulang berjalan
        Log.d("DebugTime", "Pesan: " + message.getMessage() +
                ", Waktu asli (UTC): " + message.getCreatedAt() +
                ", Waktu setelah format (WIB): " + formattedTime);

        // Tampilkan pesan dan waktu
        holder.content.setText(message.getMessage() != null ? message.getMessage() : "Pesan kosong");
        holder.timestamp.setText(formattedTime);

        // Posisi pesan
        setCardPosition(holder, message.getSender());

        // Header tanggal
        if (shouldShowDateHeader(position)) {
            holder.dateHeader.setText(getDayLabel(message.getCreatedAt()));
            holder.dateHeader.setVisibility(View.VISIBLE);
        } else {
            holder.dateHeader.setVisibility(View.GONE);
        }
    }







    @Override
    public int getItemCount() {
        return messageList.size();
    }



    // Konversi waktu dari UTC ke WIB
    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "Invalid time";
        }

        try {
            // Tentukan format input
            SimpleDateFormat inputFormat;
            if (timestamp.contains("T") && timestamp.contains("Z")) {
                // Format ISO 8601 (UTC)
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Pastikan input di UTC
            } else if (timestamp.contains("T")) {
                // Format ISO 8601 tanpa 'Z'
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Input di UTC
            } else {
                // Format waktu lokal (misalnya: "yyyy-MM-dd HH:mm:ss")
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Input tetap di UTC
            }

            // Format output ke waktu lokal
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta")); // Zona waktu lokal (WIB)

            // Parsing dan format ulang
            Date date = inputFormat.parse(timestamp);

            // Debugging untuk memastikan konversi berjalan
            Log.d("DebugTime", "Waktu asli dari server: " + timestamp);
            Log.d("DebugTime", "Waktu setelah parsing (Date): " + date);
            Log.d("DebugTime", "Waktu setelah konversi ke WIB: " + outputFormat.format(date));

            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid time"; // Jika parsing gagal
        }
    }















    // Menentukan apakah header tanggal perlu ditampilkan
    private boolean shouldShowDateHeader(int position) {
        if (position == 0) return true;

        String currentTimestamp = messageList.get(position).getCreatedAt();
        String previousTimestamp = messageList.get(position - 1).getCreatedAt();

        return !isSameDay(currentTimestamp, previousTimestamp);
    }

    // Periksa apakah dua timestamp berada di hari yang sama
    private boolean isSameDay(String timestamp1, String timestamp2) {
        if (timestamp1 == null || timestamp2 == null || timestamp1.isEmpty() || timestamp2.isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = sdf.parse(timestamp1);
            Date date2 = sdf.parse(timestamp2);
            return date1.equals(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Label tanggal untuk header
    private String getDayLabel(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "Tanggal Tidak Valid";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date messageDate = sdf.parse(timestamp);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(messageDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Tanggal Tidak Valid";
        }
    }

    // Atur posisi pesan (kiri atau kanan)
    private void setCardPosition(ViewHolder holder, String sender) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.constraintLayout);

        if ("admin".equalsIgnoreCase(sender)) {
            // Pesan dari admin (di kiri)
            constraintSet.clear(R.id.card_view, ConstraintSet.END);
            constraintSet.connect(R.id.card_view, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.other_message));
        } else if (sender.equals(loggedInUser)) {
            // Pesan dari pengguna login (di kanan)
            constraintSet.clear(R.id.card_view, ConstraintSet.START);
            constraintSet.connect(R.id.card_view, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.user_message));
        }

        constraintSet.applyTo(holder.constraintLayout);
    }

    // ViewHolder untuk RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, timestamp, dateHeader;
        CardView cardView;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.message_content);
            timestamp = itemView.findViewById(R.id.timestamp);
            cardView = itemView.findViewById(R.id.card_view);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
            dateHeader = itemView.findViewById(R.id.date_header);
        }
    }
}