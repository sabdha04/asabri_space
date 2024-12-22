package com.example.project_hc002;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TabLayout apptabs;
    ViewPager2 pager;
    ViewPagerFragmentAdapter adapter;
    EventViewModel eventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Window window = getWindow();
//
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blueasabri));

        initvar();
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        prefetchData();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setViewPager(pager);
        pager.setUserInputEnabled(false);
        
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.selectTab(position);
            }
        });
    }

    void initvar()
    {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        pager = findViewById(R.id.pager);
        adapter = new ViewPagerFragmentAdapter(this, 5);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        
        bottomNav.setViewPager(pager);
        pager.setUserInputEnabled(false);
        
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.selectTab(position);
            }
        });
    }

    public static class ViewPagerFragmentAdapter extends FragmentStateAdapter {
        int size;
        ProfileFragment profileFragment;

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, int size) {
            super(fragmentActivity);
            this.size = size;
            profileFragment = new ProfileFragment();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new HomeFragment();
                case 1:
//                    return new CommGroupFragment();
                    return new CommunityHolderFragment();
                case 2:
                    return new PlaceholderFragment();
//                    return new PostFragment();
                case 3:
                    return new NotifHolderFragment();
//                    return new NotificationFragment();
                case 4:
                    return new ProfileFragment();
            }
            return new HomeFragment();
        }

        @Override
        public int getItemCount() {
            return size;
        }
    }

    public void refreshCommunityFragment() {
        CommunityFragment communityFragment = 
            (CommunityFragment) getSupportFragmentManager().findFragmentByTag("community_fragment");
        if (communityFragment != null) {
            communityFragment.loadPosts();
        }
    }

    private void prefetchData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<List<Event>> call = apiService.getEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();

                    eventViewModel.setEvents(events);
                } else {
                    Toast.makeText(MainActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}