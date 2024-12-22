package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    ViewPager2 pager;
    ViewPagerFragmentAdapter adapter;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        setupNewsItems();
        initvar();
    }

    private void setupNewsItems() {
        LinearLayout newsContainer = findViewById(R.id.newsContainer);
        View newsItem1 = findViewById(R.id.newsItemContainer);
        View newsItem2 = findViewById(R.id.newsItemContainer);
        View newsItem3 = findViewById(R.id.newsItemContainer);

        View.OnClickListener newsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = (LinearLayout) v;
                TextView titleView = (TextView) ((LinearLayout) container.getChildAt(1)).getChildAt(0);
                TextView descView = (TextView) ((LinearLayout) container.getChildAt(1)).getChildAt(1);

                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                intent.putExtra("title", titleView.getText().toString());
                intent.putExtra("description", descView.getText().toString());
                startActivity(intent);
            }
        };

        if (newsItem1 != null) newsItem1.setOnClickListener(newsClickListener);
        if (newsItem2 != null) newsItem2.setOnClickListener(newsClickListener);
        if (newsItem3 != null) newsItem3.setOnClickListener(newsClickListener);
    }

    void initvar()
    {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        pager = findViewById(R.id.pager);
        adapter = new ViewPagerFragmentAdapter(this,5);
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
                    return new CommunityFragment();
                case 2:
                    return new HomeFragment();
                case 3:
                    return new CommunityFragment();
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
}