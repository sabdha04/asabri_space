package com.example.project_hc002;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PlaceholderFragment extends Fragment {

    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar tb = view.findViewById(R.id.toolbar_postpl);
        TextView toolbarTitle = tb.findViewById(R.id.toolbar_title);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(tb);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
//        Button showFragmentAButton = view.findViewById(R.id.showFragmentAButton);
//        showFragmentAButton.setOnClickListener(v -> viewPager.setCurrentItem(0));
//
//        Button showFragmentBButton = view.findViewById(R.id.showFragmentBButton);
//        showFragmentBButton.setOnClickListener(v -> viewPager.setCurrentItem(1));

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    toolbarTitle.setText("Buat Postingan");
                    tb.setNavigationOnClickListener(v -> {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            mainActivity.pager.setCurrentItem(1);
                        }
                    });
                } else if (position == 1) {
                    toolbarTitle.setText("Buat Event");
                    tb.setNavigationOnClickListener(v -> {
                        viewPager.setCurrentItem(0);
                    });
                }
            }
        });

//        PlaceholderPagerAdapter adapter = new PlaceholderPagerAdapter(this);
//        viewPager.setAdapter(adapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        viewPager.setAdapter(new PlaceholderPagerAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new PostFragment();
                    case 1:
                        return new PostEvFragment();
                    default:
                        return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Forum Post");
                    break;
                case 1:
                    tab.setText("Event Post");
                    break;
            }
        }).attach();

        return view;
    }
}


//package com.example.project_hc002;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//public class PlaceholderFragment extends Fragment {
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        Button showFragmentAButton = view.findViewById(R.id.showFragmentAButton);
//        showFragmentAButton.setOnClickListener(v -> loadFragment(new PostFragment()));
//
//        Button showFragmentBButton = view.findViewById(R.id.showFragmentBButton);
//        showFragmentBButton.setOnClickListener(v -> loadFragment(new PostEvFragment()));
//
//        if (savedInstanceState == null) {
//            loadFragment(new PostFragment());
//        }
//
//        return view;
//    }
//
//    private void loadFragment(Fragment fragment) {
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmentContainer, fragment);
//        transaction.addToBackStack(null); // Optionally add to backstack
//        transaction.commit();
//    }
//}
