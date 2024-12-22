package com.example.project_hc002;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class BottomNavigationView extends FrameLayout {
    private TabLayout bottomTabs;
    private ViewPager2 viewPager;
    private OnTabSelectedListener tabSelectedListener;

    public void setOnItemSelectedListener(Object o) {
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public BottomNavigationView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_navigation_layout, this, true);
        bottomTabs = findViewById(R.id.bottomTabs);

        setupTabListener();
        // Set initial tab color
        TabLayout.Tab tab = bottomTabs.getTabAt(0);
        if (tab != null && tab.getIcon() != null) {
            tab.getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupTabListener() {
        bottomTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) {
                    tab.getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                }
                if (tabSelectedListener != null) {
                    tabSelectedListener.onTabSelected(tab.getPosition());
                }
                if (viewPager != null) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) {
                    tab.getIcon().setColorFilter(Color.parseColor("#4F78D0"), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
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
                    return new PostFragment();
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
    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.tabSelectedListener = listener;
    }

    public void selectTab(int position) {
        bottomTabs.selectTab(bottomTabs.getTabAt(position));
    }
}