package com.example.project_hc002;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PlaceholderPagerAdapter extends FragmentStateAdapter {

    public PlaceholderPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PostFragment();
            case 1:
                return new PostEvFragment();
            default:
                return new PostFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
