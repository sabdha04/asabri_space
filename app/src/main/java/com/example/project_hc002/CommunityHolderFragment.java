package com.example.project_hc002;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityHolderFragment extends Fragment {

    private ViewPager2 viewPager;

    public CommunityHolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_holder, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.comholder), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar tb = view.findViewById(R.id.toolbar_comholder);
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(tb);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        viewPager = view.findViewById(R.id.viewPager);
//        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        viewPager.setAdapter(new NotifPagerAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                return new CommGroupFragment();
            }

            @Override
            public int getItemCount() {
                return 1; // Hanya satu item (fragment)
            }
        });
//        viewPager.setAdapter(new NotifPagerAdapter(this) {
//            @Override
//            public Fragment createFragment(int position) {
//                switch (position) {
//                    case 0:
//                        return new CommunityFragment();
//                    case 1:
//                        return new CommGroupFragment();
//                    default:
//                        return new CommunityFragment();
//                }
//            }
//
//            @Override
//            public int getItemCount() {
//                return 2;
//            }
//        });

//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            switch (position) {
//                case 0:
//                    tab.setText("Semua");
//                    break;
//                case 1:
//                    tab.setText("Komunitas");
//                    break;
//            }
//        }).attach();

        return view;
    }
}