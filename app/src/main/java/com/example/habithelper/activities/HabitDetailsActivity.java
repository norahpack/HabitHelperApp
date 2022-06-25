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
import com.example.habithelper.models.TrackDay;
import com.example.habithelper.utilities.LinearRegressionCalculator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class HabitDetailsActivity extends AppCompatActivity {

    private ImageView ivHabit;
    private TextView tvHabit;
    private Habit habit;
    private Button btnBack;
    private ParseUser currentUser;
    public int numDaysTracked;
    private int numHabits;
    LinearRegressionCalculator lrc;
    public List<Object> habitsList;
    public double moodSum = 0;
    public double habitCoefficient;
    public double percentChange;
    public double averageMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        ivHabit = findViewById(R.id.ivHabit);
        tvHabit = findViewById(R.id.tvHabit);
        btnBack = findViewById(R.id.btnBack);
        habit = (Habit) Parcels.unwrap(getIntent().getParcelableExtra(Habit.class.getSimpleName()));

        currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");
        numHabits = habitsList.size();

        //TODO: delete
        getMostImpactfulHabits();

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

    /**
     * Determines whether the user has been tracking habits long enough to analyze their past data
     * If so, calls a method to determine and display their most impactful habits
     * If not, displays a card to tell the user to continue tracking.
     *
     */
    private void getMostImpactfulHabits() {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.include(TrackDay.KEY_MOOD);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.addDescendingOrder(TrackDay.KEY_DATE_NUMBER);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                numDaysTracked = daysTracked.size();

                // ensures the number of days the user has tracked is greater than the number of habits they are tracking
                // required for LeastSquaresRegression
                if (numDaysTracked > numHabits) {
                    averageMood = getAverageMood(daysTracked);
                    //TODO: set visibility of percentage calculator!
                    lrc = new LinearRegressionCalculator();
                    // calculates the array of correlation coefficients of each habit on the user's mood
                    lrc.performLeastSquares(daysTracked, numDaysTracked, numHabits);
                    double[] resultsArray = lrc.leastSquaresResult;
                    int habitIndex = findIndexOfHabit();
                    for (int i = 0; i< resultsArray.length; i++){
                        System.out.println(resultsArray[i]);
                    }
                    habitCoefficient = resultsArray[habitIndex];
                    percentChange = ((int) Math.round(100 * ((double) habitCoefficient / averageMood)));
                    System.out.println(percentChange);
                } else {
                    //TODO: what to do if the user hasn't tracked enough days yet
                }
            }
        });
    }

    /**
     * Finds the index of the desired habit in the habit list
     * @return the habit's index
     */
    private int findIndexOfHabit() {
        int index = -1;
        for (int i = 0; i<numHabits; i++){
            if(habitsList.get(i).equals(habit.getHabitName())){
                return i;
            }
        }
        return index;
    }

    /**
     * Determines and displays the user's average mood
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     */
    private double getAverageMood(List<TrackDay> daysTracked) {
        for (int i = 0; i < daysTracked.size(); i++) {
            moodSum += daysTracked.get(i).getMood();
        }
        return (moodSum / (daysTracked.size()));
    }
}