package com.example.habithelper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.habithelper.R;
import com.example.habithelper.fragments.HomeFragment;
import com.example.habithelper.fragments.ListFragment;
import com.example.habithelper.fragments.MoodFragment;
import com.example.habithelper.fragments.ProfileFragment;
import com.example.habithelper.fragments.TrackFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static MainActivity self;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    protected BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        fragment = new ListFragment();
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
        if (extras!=null) {
            String tabGoTo = (extras.getString("tab"));
            if(tabGoTo.equals("habits")){
                bottomNavigation.setSelectedItemId(R.id.itemList);
            } else {
                bottomNavigation.setSelectedItemId(R.id.itemHome);
            }
        } else {
            // sets the default fragment to be the home fragment
            bottomNavigation.setSelectedItemId(R.id.itemHome);
        }
    }

    /**
     * Navigates to a new fragment
     * @param fragment     the fragment to navigate to
     * @param selectedItem the item in the toolbar to select (and change color of)
     */
    public void setTab(Fragment fragment, int selectedItem) {
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        bottomNavigation.setSelectedItemId(selectedItem);
    }
}