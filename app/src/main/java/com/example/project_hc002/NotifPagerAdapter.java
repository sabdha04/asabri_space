package com.example.project_hc002;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotifPagerAdapter extends FragmentStateAdapter {

    public NotifPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NotificationFragment();
            case 1:
                return new NotifEventFragment();
            default:
                return new NotificationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
