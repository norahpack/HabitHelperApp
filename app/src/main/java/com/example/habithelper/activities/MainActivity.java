package com.example.habithelper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.habithelper.R;
import com.example.habithelper.adapters.ViewPagerAdapter;
import com.example.habithelper.utilities.CustomViewPager;
import com.example.habithelper.utilities.FragmentEnum;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public static MainActivity self;
    protected BottomNavigationView bottomNavigation;
    public CustomViewPager viewPager;
    public ParseUser currentUser;
    public ViewPagerAdapter adapter;
    public CustomViewPager.OnPageChangeListener onPageChangeListener;
    List<String> badges;
    List<Integer> fragmentOrder = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = ParseUser.getCurrentUser();
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        viewPager = findViewById(R.id.viewPager);
        swipeBetweenViews(viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        setUpBottomViewNavigation();
        fragmentOrder.clear();
        setViewPager();
    }

    /**
     * Sets the viewPager's current item to the item stored in the bundle passed in.
     */
    private void setViewPager() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String tabGoTo = (extras.getString("tab"));
            if (tabGoTo.equals("habits")) {
                viewPager.setCurrentItem(FragmentEnum.HABIT_LIST_FRAGMENT.getIndex());
            } else if (tabGoTo.equals("profile")){
                viewPager.setCurrentItem(FragmentEnum.PROFILE_FRAGMENT.getIndex());
            } else {
                viewPager.setCurrentItem(FragmentEnum.HOME_FRAGMENT.getIndex());
            }
        } else {
            if (bottomNavigation.getSelectedItemId() != R.id.itemHome){
                viewPager.setCurrentItem(FragmentEnum.HOME_FRAGMENT.getIndex());
            } else if (fragmentOrder.size() < 1){
                fragmentOrder.add(2);
            }
        }
    }

    /**
     * Determines whether the user has earned the badge in question already
     * @param badgeName the badge to search for
     * @return true if the user has already earned the badge
     */
    public boolean checkForBadge(String badgeName){
        badges = currentUser.getList("badgesEarned");
        return badges.contains(badgeName);
    }

    /**
     * disables swiping between fragments
     */
    public void disableSwipe(){
        viewPager.setPagingEnabled(false);
    }

    /**
     * enables swiping between fragments
     */
    public void enableSwipe(){
        viewPager.setPagingEnabled(true);
    }

    /**
     * When the user clicks on a navItem in the bottomNavigationBar,
     * Tells the ViewPager to swipe to that navItem's corresponding fragment
     * @param item the menu item that has been selected
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemProfile:
                fragmentOrder.add(FragmentEnum.PROFILE_FRAGMENT.getIndex());
                viewPager.setCurrentItem(FragmentEnum.PROFILE_FRAGMENT.getIndex());
                break;
            case R.id.itemTrack:
                fragmentOrder.add(FragmentEnum.TRACK_FRAGMENT.getIndex());
                viewPager.setCurrentItem(FragmentEnum.TRACK_FRAGMENT.getIndex());
                break;
            case R.id.itemMood:
                fragmentOrder.add(FragmentEnum.MOOD_FRAGMENT.getIndex());
                viewPager.setCurrentItem(FragmentEnum.MOOD_FRAGMENT.getIndex());
                break;
            case R.id.itemList:
                fragmentOrder.add(FragmentEnum.HABIT_LIST_FRAGMENT.getIndex());
                viewPager.setCurrentItem(FragmentEnum.HABIT_LIST_FRAGMENT.getIndex());
                break;
            default:
                fragmentOrder.add(FragmentEnum.HOME_FRAGMENT.getIndex());
                viewPager.setCurrentItem(FragmentEnum.HOME_FRAGMENT.getIndex());
                break;
        }
        return true;
    }

    /**
     * Allows the user to swipe to move between fragments
     * @param viewPager the ViewPager we are using to swipe between fragments.
     */
    private void swipeBetweenViews(ViewPager viewPager) {
        onPageChangeListener = new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                fragmentOrder.add(position);
                switch (position) {
                    case 0:
                        bottomNavigation.getMenu().findItem(R.id.itemProfile).setChecked(true);
                        break;
                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.itemTrack).setChecked(true);
                        break;
                    case 3:
                        bottomNavigation.getMenu().findItem(R.id.itemMood).setChecked(true);
                        break;
                    case 4:
                        bottomNavigation.getMenu().findItem(R.id.itemList).setChecked(true);
                        break;
                    default:
                        bottomNavigation.getMenu().findItem(R.id.itemHome).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        };

        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * Defaults to the home fragment and sets up correspondence between the icons in the bottom navigation bar
     * and the indices corresponding to each fragment.
     */
    @SuppressLint("NonConstantResourceId")
    private void setUpBottomViewNavigation() {

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int selection;
            switch (item.getItemId()) {
                case R.id.itemProfile:
                    selection = FragmentEnum.PROFILE_FRAGMENT.getIndex();
                    break;
                case R.id.itemTrack:
                    selection = FragmentEnum.TRACK_FRAGMENT.getIndex();
                    break;
                case R.id.itemMood:
                    selection = FragmentEnum.MOOD_FRAGMENT.getIndex();
                    break;
                case R.id.itemList:
                    selection = FragmentEnum.HABIT_LIST_FRAGMENT.getIndex();
                    break;
                default:
                    selection = FragmentEnum.HOME_FRAGMENT.getIndex();
                    break;
            }
            viewPager.setCurrentItem(selection);
            return true;
        });
        // Defaults to the home fragment
        bottomNavigation.setSelectedItemId(R.id.itemHome);
    }

    /**
     * Handles the 'back button' events by navigating users to the fragment they were previously on
     */
    @Override
    public void onBackPressed() {
        if (fragmentOrder == null) {
            super.onBackPressed();
        } else if (fragmentOrder.size() == 1){
            fragmentOrder.clear();
            viewPager.setCurrentItem(2);
        } else {
            // handles users clicking on the same fragment multiple times
            if(fragmentOrder.get(fragmentOrder.size() - 2) == fragmentOrder.get(fragmentOrder.size()-1)){
                fragmentOrder.remove(fragmentOrder.size()-1);
                onBackPressed();
            } else {
                viewPager.setCurrentItem(fragmentOrder.get(fragmentOrder.size()-2));
                fragmentOrder.remove(fragmentOrder.size() - 2);
                fragmentOrder.remove(fragmentOrder.size() - 1);
            }
        }
    }
}