package com.example.habithelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.habithelper.fragments.HomeFragment;
import com.example.habithelper.fragments.ListFragment;
import com.example.habithelper.fragments.MoodFragment;
import com.example.habithelper.fragments.ProfileFragment;
import com.example.habithelper.fragments.TrackFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    Button btnLogout;
    public static MainActivity self;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    protected BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation=findViewById(R.id.bottomNavigation);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                    case R.id.itemHome:
                        //change this
                        fragment = new HomeFragment();
                        break;
                    case R.id.itemMood:
                        //change this
                        fragment = new MoodFragment();
                        break;
                    case R.id.itemList:
                        //change this
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

        // sets the default fragment to be the home fragment
        bottomNavigation.setSelectedItemId(R.id.itemHome);
    }
    public void setTab(Fragment fragment, int selectedItem){
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        bottomNavigation.setSelectedItemId(selectedItem);
    }
}