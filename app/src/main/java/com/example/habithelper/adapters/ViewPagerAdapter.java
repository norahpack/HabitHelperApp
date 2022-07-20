package com.example.habithelper.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.habithelper.fragments.HabitListFragment;
import com.example.habithelper.fragments.HomeFragment;
import com.example.habithelper.fragments.MoodFragment;
import com.example.habithelper.fragments.ProfileFragment;
import com.example.habithelper.fragments.TrackFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_FRAGMENT_COUNT = 5;

    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment =  new ProfileFragment();
        }
        else if (position == 1) {
            fragment =  new TrackFragment();
        }
        else if (position == 2){
            fragment =  new HomeFragment();
        }
        else if (position == 3){
            fragment = new MoodFragment();
        } else {
            fragment = new HabitListFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENT_COUNT;
    }
}
