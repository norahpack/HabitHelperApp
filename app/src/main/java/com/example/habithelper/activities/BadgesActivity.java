package com.example.habithelper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.habithelper.R;
import com.example.habithelper.adapters.BadgesAdapter;
import com.parse.ParseUser;
import java.util.List;

public class BadgesActivity extends AppCompatActivity {

    private RecyclerView rvBadges;
    private ParseUser currentUser;
    private List<String> badgesList;
    private BadgesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);
        currentUser = ParseUser.getCurrentUser();
        rvBadges = findViewById(R.id.rvBadges);
        badgesList = currentUser.getList("badgesEarned");
        final LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new BadgesAdapter(this, badgesList);
        rvBadges.setAdapter(adapter);
        rvBadges.setLayoutManager(layout);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * OnClick of an item in the navBar, performs the action specified
     * @param item the item the user clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent(BadgesActivity.this, MainActivity.class);
        i.putExtra("tab", "profile");
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }
}