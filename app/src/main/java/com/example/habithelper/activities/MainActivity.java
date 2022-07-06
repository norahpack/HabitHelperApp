package com.example.habithelper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.habithelper.R;
import com.example.habithelper.fragments.HomeFragment;
import com.example.habithelper.fragments.HabitListFragment;
import com.example.habithelper.fragments.MoodFragment;
import com.example.habithelper.fragments.ProfileFragment;
import com.example.habithelper.fragments.TrackFragment;
import com.example.habithelper.models.Habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity self;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    protected BottomNavigationView bottomNavigation;
    public ParseUser currentUser;
    List<String> badges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = ParseUser.getCurrentUser();

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            /**
             *
             * @param item the MenuItem that was selected
             * @return true to display the item as the selected item
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.itemProfile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.itemTrack:
                        fragment = new TrackFragment();
                        break;
                    case R.id.itemMood:
                        fragment = new MoodFragment();
                        break;
                    case R.id.itemList:
                        fragment = new HabitListFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String tabGoTo = (extras.getString("tab"));
            if (tabGoTo.equals("habits")) {
                bottomNavigation.setSelectedItemId(R.id.itemList);
            } else if (tabGoTo.equals("profile")){
                bottomNavigation.setSelectedItemId(R.id.itemProfile);
            } else {
                bottomNavigation.setSelectedItemId(R.id.itemHome);
            }
        } else {
            // sets the default fragment to be the home fragment
            if (bottomNavigation.getSelectedItemId() != R.id.itemHome){
                bottomNavigation.setSelectedItemId(R.id.itemHome);
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
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
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
}