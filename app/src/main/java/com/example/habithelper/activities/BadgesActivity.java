package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.habithelper.R;
import com.example.habithelper.adapters.BadgesAdapter;
import com.example.habithelper.adapters.HabitAdapter;
import com.example.habithelper.models.Habit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BadgesActivity extends AppCompatActivity {

    RecyclerView rvBadges;
    ParseUser currentUser;
    List<String> badgesList;
    BadgesAdapter adapter;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);
        currentUser = ParseUser.getCurrentUser();
        rvBadges = findViewById(R.id.rvBadges);
        btnBack = findViewById(R.id.btnBack);

        badgesList = currentUser.getList("badgesEarned");
        final LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new BadgesAdapter(this, badgesList);
        rvBadges.setAdapter(adapter);
        rvBadges.setLayoutManager(layout);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BadgesActivity.this, MainActivity.class);
                i.putExtra("tab", "profile");
                startActivity(i);
                finish();
            }
        });
    }
}