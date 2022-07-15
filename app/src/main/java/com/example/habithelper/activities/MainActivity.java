package com.example.habithelper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.habithelper.R;
import com.example.habithelper.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public static MainActivity self;
    protected BottomNavigationView bottomNavigation;
    private ViewPager viewPager;
    public ParseUser currentUser;
    List<String> badges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = ParseUser.getCurrentUser();
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        swipeBetweenViews(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        setUpBottomViewNavigation();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String tabGoTo = (extras.getString("tab"));
            if (tabGoTo.equals("habits")) {
                viewPager.setCurrentItem(4);
            } else if (tabGoTo.equals("profile")){
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(2);
            }
        } else {
            if (bottomNavigation.getSelectedItemId() != R.id.itemHome){
                viewPager.setCurrentItem(2);
            }
        }
    }

    /**
     * Navigates to a new fragment
     *
     * @param fragment     the fragment to navigate to
     * @param selectedItem the item in the toolbar to select (and change color of)
     */
    public void setTab(Fragment fragment, int selectedItem) {
        bottomNavigation.setSelectedItemId(selectedItem);
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
     * When the user clicks on a navItem in the bottomNavigationBar,
     * Tells the ViewPager to swipe to that navItem's corresponding fragment
     * @param item the menu item that has been selected
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemProfile:
                viewPager.setCurrentItem(0);
                break;
            case R.id.itemTrack:
                viewPager.setCurrentItem(1);
                break;
            case R.id.itemHome:
                viewPager.setCurrentItem(2);
                break;
            case R.id.itemMood:
                viewPager.setCurrentItem(3);
                break;
            case R.id.itemList:
                viewPager.setCurrentItem(4);
                break;
            default:
                viewPager.setCurrentItem(2);
                break;
        }
        return true;
    }

    /**
     * Allows the user to swipe to move between fragments
     * @param viewPager the ViewPager we are using to swipe between fragments.
     */
    private void swipeBetweenViews(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.getMenu().findItem(R.id.itemProfile).setChecked(true);
                        break;
                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.itemTrack).setChecked(true);
                        break;
                    case 2:
                        bottomNavigation.getMenu().findItem(R.id.itemHome).setChecked(true);
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
            public void onPageScrollStateChanged(int state) { }
        });
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
                    selection = 0;
                    break;
                case R.id.itemTrack:
                    selection = 1;
                    break;
                case R.id.itemHome:
                    selection = 2;
                    break;
                case R.id.itemMood:
                    selection = 3;
                    break;
                case R.id.itemList:
                    selection = 4;
                    break;
                default:
                    selection = 2;
                    break;
            }
            viewPager.setCurrentItem(selection);
            return true;
        });

        // Defaults to the home fragment
        bottomNavigation.setSelectedItemId(R.id.itemHome);
    }

}