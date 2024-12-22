package com.example.project_hc002;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project_hc002.adapters.MessageAdapter;
import com.example.project_hc002.models.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatAdminActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton;
    private ApiService apiService;

    private String loggedInUsername;
    private Socket socket;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_admin);

        // Mendapatkan username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        loggedInUsername = sharedPreferences.getString("pesertaName", "Anonymous");

        // Inisialisasi SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::refreshMessages);

        // Inisialisasi Socket.IO
        try {
            socket = IO.socket("http://192.168.2.212:3000");
            socket.connect();

            // Mendaftarkan pengguna ke server
            socket.emit("register", loggedInUsername);

            // Menerima pesan real-time
            socket.on("receive_message", onNewMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error connecting to Socket.IO", Toast.LENGTH_SHORT).show();
        }

        messageList = loadMessagesFromLocal();

        // Debugging semua pesan yang dimuat
        for (Message msg : messageList) {
            Log.d("DebugTime", "Pesan: " + msg.getMessage() +
                    ", Waktu asli: " + msg.getCreatedAt() +
                    ", Waktu setelah format: " + formatTimestamp(msg.getCreatedAt()));
        }

        // Inisialisasi tampilan
        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.commentadd);
        sendButton = findViewById(R.id.sendButton);
        ImageView btnBack = findViewById(R.id.btn_back);

        // Setup RecyclerView
        messageList = loadMessagesFromLocal();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Inisialisasi API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Memuat pesan lama dari server
        fetchMessagesFromDatabase();

        // Tombol kembali
        btnBack.setOnClickListener(v -> onBackPressed());

        // Tombol kirim pesan
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            } else {
                Toast.makeText(ChatAdminActivity.this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshMessages() {
        fetchMessagesFromDatabase();
    }
    // Fetch pesan lama dari server
    private void fetchMessagesFromDatabase() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getMessages(loggedInUsername).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());

                    // Debugging untuk memastikan data diterima
                    for (Message msg : messageList) {
                        Log.d("DebugTime", "Pesan dari server: " + msg.getMessage() +
                                ", Waktu asli: " + msg.getCreatedAt());
                    }

                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);

                    // Simpan ke local storage
                    saveMessagesToLocal(messageList);
                } else {
                    Toast.makeText(ChatAdminActivity.this, "Gagal memuat pesan.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // Log kesalahan untuk debugging
                Log.e("ChatDebug", "Kesalahan jaringan: " + t.getMessage(), t);
                Toast.makeText(ChatAdminActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Mengirim pesan baru
    // Mendapatkan waktu sekarang dalam format ISO 8601
    private String getCurrentTimestamp() {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC")); // Set ke UTC
        return iso8601Format.format(new Date());
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Muat ulang pesan dari local storage dan update tampilan
        messageList.clear();
        messageList.addAll(loadMessagesFromLocal());

        // Debugging semua pesan yang dimuat
        for (Message msg : messageList) {
            Log.d("DebugTime", "Pesan setelah kembali ke halaman: " + msg.getMessage() +
                    ", Waktu asli: " + msg.getCreatedAt() +
                    ", Waktu setelah format: " + formatTimestamp(msg.getCreatedAt()));
        }

        messageAdapter.notifyDataSetChanged(); // Refresh RecyclerView
        recyclerView.scrollToPosition(messageList.size() - 1); // Scroll ke pesan terakhir
    }





    private void sendMessage(String messageContent) {
        if (messageContent.trim().isEmpty()) {
            Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil waktu sekarang dalam UTC
        String createdAt = getCurrentTimestamp();

        // Buat pesan baru
        Message newMessage = new Message(loggedInUsername, "admin", messageContent, createdAt);

        // Kirim pesan ke server
        apiService.sendMessage(newMessage).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Tambahkan pesan ke RecyclerView
                    messageList.add(response.body());
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    messageInput.setText(""); // Kosongkan input teks

                    // Debug waktu
                    Log.d("DebugTime", "Pesan dikirim: " + response.body().getMessage() +
                            ", Waktu asli: " + response.body().getCreatedAt() +
                            ", Waktu setelah format: " + formatTimestamp(response.body().getCreatedAt()));

                    // Simpan pesan ke local storage
                    saveMessagesToLocal(messageList);
                } else {
                    Toast.makeText(ChatAdminActivity.this, "Gagal mengirim pesan: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(ChatAdminActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



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


    // Menangani pesan real-time
    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        try {
            JSONObject data = (JSONObject) args[0];
            String sender = data.getString("sender");
            String receiver = data.getString("receiver");
            String message = data.getString("message");
            String createdAt = data.getString("created_at"); // Waktu asli dari server

            // Format waktu ke WIB
            String formattedTime = formatTimestamp(createdAt);

            // Debugging waktu asli dan setelah format
            Log.d("DebugTime", "Pesan diterima: " + message +
                    ", Waktu asli: " + createdAt +
                    ", Waktu setelah format: " + formattedTime);

            // Tambahkan pesan baru ke list
            Message newMessage = new Message(sender, receiver, message, createdAt);
            messageList.add(newMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            // Simpan pesan ke lokal storage
            saveMessagesToLocal(messageList);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ChatDebug", "Error di onNewMessage: " + e.getMessage());
        }
    });




    // Menyimpan pesan ke local storage
    private void saveMessagesToLocal(List<Message> messages) {
        SharedPreferences sharedPreferences = getSharedPreferences("ChatPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(messages); // Waktu tetap dalam UTC
        editor.putString("messages_" + loggedInUsername, json);
        editor.apply();
    }





    // Memuat pesan dari local storage
    private List<Message> loadMessagesFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChatPrefs", MODE_PRIVATE);

        String json = sharedPreferences.getString("messages_" + loggedInUsername, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Message>>() {}.getType();
            List<Message> messages = gson.fromJson(json, type);

            // Debugging untuk memastikan data tetap dalam UTC
            for (Message message : messages) {
                Log.d("DebugTime", "Pesan dari local: " + message.getMessage() +
                        ", Waktu asli (UTC): " + message.getCreatedAt());
            }

            return messages;
        }
        return new ArrayList<>();
    }

}
