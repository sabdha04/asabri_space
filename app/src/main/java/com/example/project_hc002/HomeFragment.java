package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView optionevents, optioncom, optionnews;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    private NewsAdapter newsAdapter;
    List<News> newsList;

    private Button btnReward;

    private CommunityAdapter communityAdapter;
    private List<Community> listCom;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflating the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Handling insets to apply padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Set OnRefreshListener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh the data when user swipes to refresh
            refreshData();
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("pesertaName", "");
        TextView profileName = view.findViewById(R.id.welcomegreet);
        profileName.setText("Hai "+nama);

        Button btnnewsall = view.findViewById(R.id.btnnwall);
        Button btnevall = view.findViewById(R.id.btnevall);
        Button btncomall = view.findViewById(R.id.btncommall);
        btnnewsall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewsAll.class);
                intent.putExtra("news_list", (Serializable) newsList);
                startActivity(intent);
            }
        });
        btnevall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EventAll.class);
//                intent.putExtra("event_list", (Serializable) eventList);
                startActivity(intent);
            }
        });
        btncomall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent it = new Intent(view.getContext(), CommunityAll.class);
//                startActivity(it);
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.pager.setCurrentItem(1);
                }
            }
        });

        Button btnReward = view.findViewById(R.id.btn_reward);
        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PointSayaActivity.class);
                startActivity(intent);
            }
        });

        //News Section
//        optionnews = view.findViewById(R.id.optionnews);
//        newsList = new ArrayList<>();
//        populatenews();
//
//        LinearLayoutManager linearLayoutManagerNews = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        optionnews.setLayoutManager(linearLayoutManagerNews);
//        optionnews.setHasFixedSize(true);
//
//        NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsList, "home");
//        optionnews.setAdapter(newsAdapter);
//
//        SnapHelper snapHelperNews = new LinearSnapHelper();
//        snapHelperNews.attachToRecyclerView(optionnews);
//
//        final Handler handlerNews = new Handler();
//        handlerNews.postDelayed(() -> {
//            RecyclerView.ViewHolder viewHolderDef = optionnews.findViewHolderForAdapterPosition(0);
//        }, 100);
//
//        optionnews.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    View view = snapHelperNews.findSnapView(linearLayoutManagerNews);
//                    int pos = linearLayoutManagerNews.getPosition(view);
//
//                    RecyclerView.ViewHolder viewHolder = optionnews.findViewHolderForAdapterPosition(pos);
//                } else {
//                    View view = snapHelperNews.findSnapView(linearLayoutManagerNews);
//                    int pos = linearLayoutManagerNews.getPosition(view);
//
//                    RecyclerView.ViewHolder viewHolder = optionnews.findViewHolderForAdapterPosition(pos);
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        optionnews = view.findViewById(R.id.optionnews);
        newsList = new ArrayList<>();

        // Initialize API Service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Fetch news from API
        apiService.getAllNews().enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body());

                    LinearLayoutManager linearLayoutManagerNews = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    optionnews.setLayoutManager(linearLayoutManagerNews);
                    optionnews.setHasFixedSize(true);

                    NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsList, "home");
                    optionnews.setAdapter(newsAdapter);

                    SnapHelper snapHelperNews = new LinearSnapHelper();
                    snapHelperNews.attachToRecyclerView(optionnews);
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching news: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Event Section
        optionevents = view.findViewById(R.id.optionevents);
        eventList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        optionevents.setLayoutManager(linearLayoutManager);
        optionevents.setHasFixedSize(true);

        eventAdapter = new EventAdapter(getContext(), eventList, "home");
        optionevents.setAdapter(eventAdapter);

        fetchdata();

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(optionevents);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            RecyclerView.ViewHolder viewHolderDef = optionevents.findViewHolderForAdapterPosition(0);

        }, 100);

        optionevents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder = optionevents.findViewHolderForAdapterPosition(pos);

//                    CardView eventparent = viewHolder.itemView.findViewById(R.id.eventparent);
//                    eventparent.animate().scaleY(1).scaleX(1).setDuration(150).setInterpolator(new AccelerateInterpolator()).start();

                    //LinearLayout eventcat = viewHolder.itemView.findViewById(R.id.eventbadge);
                    //eventcat.animate().alpha(1).setDuration(200).start();
                } else {
                    View view = snapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(view);

                    RecyclerView.ViewHolder viewHolder = optionevents.findViewHolderForAdapterPosition(pos);

//                    CardView eventparent = viewHolder.itemView.findViewById(R.id.eventparent);
//                    eventparent.animate().scaleY(0.7f).scaleX(0.7f).setDuration(150).setInterpolator(new AccelerateInterpolator()).start();

                    //LinearLayout eventcat = viewHolder.itemView.findViewById(R.id.eventbadge);
                    //eventcat.animate().alpha(1).setDuration(200).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //community Section
        optioncom = view.findViewById(R.id.rvcommunity);
        new ForumAdapterHelper(optioncom, getContext(), 4);

        return view;
    }

    private void refreshData() {
        fetchdata();
        new ForumAdapterHelper(optioncom, getContext(), 4);

        swipeRefreshLayout.setRefreshing(false);
    }
    private void fetchdata() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<List<Event>> call = apiService.getEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();

                    getActivity().runOnUiThread(() -> {
                        eventList.clear();
                        eventList.addAll(events);
                        eventAdapter.notifyDataSetChanged();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

//    private void fetchdata() {
//        String backendUrl = "http://192.168.69.137:3000/api/getev";  // bisa disesuaikan ya
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(backendUrl).get().build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(requireContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    ArrayList<Event> events = parsePosts(responseBody);
//
//                    getActivity().runOnUiThread(() -> {
//                        eventList.clear();
//                        eventList.addAll(events);
//                        eventAdapter.notifyDataSetChanged();
//                    });
//                } else {
//                    getActivity().runOnUiThread(() -> {
//                        Toast.makeText(requireContext(), "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        });
//    }

    private ArrayList<Event> parsePosts(String responseBody) {
        Gson gson = new Gson();
        return gson.fromJson(responseBody, new TypeToken<ArrayList<Event>>(){}.getType());
    }
    private void populatenews() {
        newsList.add(new News("Family Gathering 2025",
                "Alumni 95 Semarang", R.drawable.vector003));
        newsList.add(new News("Jalan Sehat Keluarga",
                "Agar badan sehat dan bugar",
                R.drawable.vector002));
        newsList.add(new News("Rajut With Us",
                "Event Merajut adalah acara yang mengajak para peserta untuk berkumpul dan belajar atau mengembangkan keterampilan merajut dalam suasana yang santai dan kreatif. Kegiatan ini tidak hanya bertujuan untuk mempelajari teknik merajut, tetapi juga untuk mempererat hubungan antar peserta melalui hobi yang menyenangkan dan bermanfaat. Dalam event ini, peserta dapat saling berbagi pengalaman, tips, serta hasil karya rajut mereka.\n" +
                        "\n" +
                        "Pada acara Merajut, berbagai tingkat keterampilan—dari pemula hingga mahir—dapat mengikuti dengan nyaman. Para peserta akan diajarkan cara dasar merajut, mulai dari memilih benang dan jarum rajut yang tepat hingga teknik merajut dasar seperti rantai, setengah tiang, atau tiang ganda. Untuk peserta yang lebih berpengalaman, biasanya disediakan sesi lanjutan untuk teknik yang lebih kompleks, seperti merajut motif atau membuat aksesori rajut.\n" +
                        "\n" +
                        "Selain belajar dan berlatih, acara ini sering kali juga dilengkapi dengan sesi berbagi ide, seperti pembuatan produk rajut yang dapat digunakan sehari-hari (seperti syal, topi, atau tas) atau untuk tujuan filantropi, misalnya membuat selimut rajut untuk anak-anak atau pasien rumah sakit.\n" +
                        "\n" +
                        "Event Merajut menjadi kesempatan untuk bersantai, mengasah kreativitas, serta meningkatkan keterampilan tangan dalam suasana yang penuh kehangatan. Acara ini juga dapat menjadi ajang bagi para pengrajin rajut untuk saling berkolaborasi, menginspirasi satu sama lain, dan membangun komunitas yang lebih solid. Selain itu, para peserta bisa mendapatkan berbagai bahan atau perlengkapan rajut yang ditawarkan di event ini, menjadikannya momen yang bermanfaat bagi mereka yang ingin lebih mendalami hobi merajut.\n" +
                        "\n" +
                        "Dengan atmosfer yang ramah dan menyenangkan, event Merajut memberikan pengalaman baru yang bermanfaat, sekaligus menciptakan karya-karya rajut yang bisa dibanggakan.",
                R.drawable.vector003));
        newsList.add(new News("Jalan Sehat Keluarga",
                "Agar badan sehat dan bugar",
                R.drawable.vector002));
        newsList.add(new News("Rajut With Us",
                "Event Merajut adalah acara yang mengajak para peserta untuk berkumpul dan belajar atau mengembangkan keterampilan merajut dalam suasana yang santai dan kreatif. Kegiatan ini tidak hanya bertujuan untuk mempelajari teknik merajut, tetapi juga untuk mempererat hubungan antar peserta melalui hobi yang menyenangkan dan bermanfaat. Dalam event ini, peserta dapat saling berbagi pengalaman, tips, serta hasil karya rajut mereka.\n" +
                        "\n" +
                        "Pada acara Merajut, berbagai tingkat keterampilan—dari pemula hingga mahir—dapat mengikuti dengan nyaman. Para peserta akan diajarkan cara dasar merajut, mulai dari memilih benang dan jarum rajut yang tepat hingga teknik merajut dasar seperti rantai, setengah tiang, atau tiang ganda. Untuk peserta yang lebih berpengalaman, biasanya disediakan sesi lanjutan untuk teknik yang lebih kompleks, seperti merajut motif atau membuat aksesori rajut.\n" +
                        "\n" +
                        "Selain belajar dan berlatih, acara ini sering kali juga dilengkapi dengan sesi berbagi ide, seperti pembuatan produk rajut yang dapat digunakan sehari-hari (seperti syal, topi, atau tas) atau untuk tujuan filantropi, misalnya membuat selimut rajut untuk anak-anak atau pasien rumah sakit.\n" +
                        "\n" +
                        "Event Merajut menjadi kesempatan untuk bersantai, mengasah kreativitas, serta meningkatkan keterampilan tangan dalam suasana yang penuh kehangatan. Acara ini juga dapat menjadi ajang bagi para pengrajin rajut untuk saling berkolaborasi, menginspirasi satu sama lain, dan membangun komunitas yang lebih solid. Selain itu, para peserta bisa mendapatkan berbagai bahan atau perlengkapan rajut yang ditawarkan di event ini, menjadikannya momen yang bermanfaat bagi mereka yang ingin lebih mendalami hobi merajut.\n" +
                        "\n" +
                        "Dengan atmosfer yang ramah dan menyenangkan, event Merajut memberikan pengalaman baru yang bermanfaat, sekaligus menciptakan karya-karya rajut yang bisa dibanggakan.",
                R.drawable.vector003));

    }

}