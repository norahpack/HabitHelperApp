package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.habithelper.R;
import com.example.habithelper.models.Habit;

import org.parceler.Parcels;

public class HabitDetailsActivity extends AppCompatActivity {

    private ImageView ivHabit;
    private TextView tvHabit;
    private Habit habit;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        ivHabit = findViewById(R.id.ivHabit);
        tvHabit = findViewById(R.id.tvHabit);
        btnBack = findViewById(R.id.btnBack);
        habit = (Habit) Parcels.unwrap(getIntent().getParcelableExtra(Habit.class.getSimpleName()));

        tvHabit.setText(habit.getHabitName());
        if(habit.get("habitImage") != null){
            ivHabit.setBackground(AppCompatResources.getDrawable(HabitDetailsActivity.this, (Integer) habit.getHabitImageKey()));
        } else {
            ivHabit.setBackground(AppCompatResources.getDrawable(HabitDetailsActivity.this, R.drawable.starslarge));
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HabitDetailsActivity.this, MainActivity.class);
                i.putExtra("tab", "habits");
                startActivity(i);
                finish();
            }
        });
    }
}