package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.content.res.Resources;
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
    private ImageView ivDayOne;
    private ImageView ivDayTwo;
    private ImageView ivDayThree;
    private ImageView ivDayFour;
    private ImageView ivDayFive;
    private ImageView ivDaySix;
    private ImageView ivDaySeven;
    private ImageView ivDayEight;
    private ImageView ivDayNine;
    private ImageView ivDayTen;
    private Habit habit;
    private Button btnBack;
    private ParseUser currentUser;
    private ConstraintLayout clLastTen;
    private ConstraintLayout clPercent;
    private ConstraintLayout clPercentNotEnough;
    private TextView tvLastTen;
    private TextView tvHabit;
    private TextView tvFirstDay;
    private TextView tvLastDay;
    private TextView tvPercent;
    private TextView tvPercentNotEnough;
    private TextView tvStreak;

    public int numDaysTracked;
    private int numHabits;
    LinearRegressionCalculator lrc;
    public List<Object> habitsList;
    public double moodSum = 0;
    public double habitCoefficient;
    public int percentChange;
    public double averageMood;
    public String increaseOrDecrease;
    public int habitStreak = 0;
    public boolean streakLost = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);
        initViews();
        if (getIntent().getParcelableExtra(Habit.class.getSimpleName()) != null) {
            habit = (Habit) Parcels.unwrap(getIntent().getParcelableExtra(Habit.class.getSimpleName()));
        }
        currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");
        numHabits = habitsList.size();

        getMostImpactfulHabits();
        tvHabit.setText(habit.getHabitName());
        setupIvHabit();
        calculateStreak();
        setupLastTenDays();

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
     * Displays the view showing the user's completion of this habit over the last ten days
     */
    private void setupLastTenDays() {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.orderByAscending(TrackDay.KEY_DATE_NUMBER);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                if (daysTracked.size() >= 10) {
                    int minDay = daysTracked.size() - 10;
                    int maxDay = daysTracked.size() - 1;
                    int habitIndex = findIndexOfHabit();
                    for (int i = 0; i < 10; i++) {
                        if (daysTracked.get(i + minDay).getTrackArray().get(habitIndex) == 1) {
                            // the user completed the habit on the specified day
                            setCompletedBubble(i);
                        }
                        tvLastTen.setVisibility(View.VISIBLE);
                        clLastTen.setVisibility(View.VISIBLE);
                        tvFirstDay.setText(String.valueOf(minDay + 1));
                        tvLastDay.setText(String.valueOf(maxDay + 1));
                    }
                } else {
                    // nothing yet - have the option to add a new view in the future
                }
            }
        });
    }

    /**
     * Fills in the specified bubble in the "last ten days" tracker
     *
     * @param i the index of the bubble to fill in
     */
    private void setCompletedBubble(int i) {
        ImageView ivToFillIn;
        if (i == 0) {
            ivToFillIn = ivDayOne;
        } else if (i == 1) {
            ivToFillIn = ivDayTwo;
        } else if (i == 2) {
            ivToFillIn = ivDayThree;
        } else if (i == 3) {
            ivToFillIn = ivDayFour;
        } else if (i == 4) {
            ivToFillIn = ivDayFive;
        } else if (i == 5) {
            ivToFillIn = ivDaySix;
        } else if (i == 6) {
            ivToFillIn = ivDaySeven;
        } else if (i == 7) {
            ivToFillIn = ivDayEight;
        } else if (i == 8) {
            ivToFillIn = ivDayNine;
        } else {
            ivToFillIn = ivDayTen;
        }
        ivToFillIn.setBackground(AppCompatResources.getDrawable(HabitDetailsActivity.this, R.drawable.track_sienna));
    }

    /**
     * Calculates and displays the number of days in a row the user has completed this habit
     */
    private void calculateStreak() {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.orderByDescending(TrackDay.KEY_DATE_NUMBER);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                int habitIndex = findIndexOfHabit();
                for (int i = 0; i < daysTracked.size(); i++) {
                    if (!streakLost) {
                        if (daysTracked.get(i).getTrackArray().get(habitIndex) == 1) {
                            // the user completed the habit on the specified day
                            habitStreak += 1;
                        } else {
                            streakLost = true;
                        }
                    }
                }
                tvStreak.setText("You have a " + habitStreak + "-day streak for this habit");
            }
        });
    }

    /**
     * Loads the drawable corresponding to the habit into the ivHabit ImageView
     */
    private void setupIvHabit() {
        if (habit.get("habitImageName") != null) {
            Resources resources = getResources();
            int resId = resources.getIdentifier(habit.getHabitImageKey(), "drawable", "com.example.habithelper");
            ivHabit.setBackground(AppCompatResources.getDrawable(HabitDetailsActivity.this, resId));
        } else {
            ivHabit.setBackground(AppCompatResources.getDrawable(HabitDetailsActivity.this, R.drawable.starslarge));
        }
    }

    /**
     * Determines whether the user has been tracking habits long enough to analyze their past data
     * If so, calls a method to determine and display their most impactful habits
     * If not, displays a card to tell the user to continue tracking.
     */
    private void getMostImpactfulHabits() {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.include(TrackDay.KEY_MOOD);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.orderByDescending(TrackDay.KEY_DATE_NUMBER);
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
                    lrc = new LinearRegressionCalculator();
                    // calculates the array of correlation coefficients of each habit on the user's mood
                    lrc.performLeastSquares(daysTracked, numDaysTracked, numHabits);
                    double[] resultsArray = lrc.leastSquaresResult;
                    int habitIndex = findIndexOfHabit();
                    if (habitIndex != -1) {
                        displayPercentChange(resultsArray, habitIndex);
                    } else {
                        // error has occurred
                        showNoDataView();
                    }
                } else {
                    // the user has not tracked enough data yet
                    showNoDataView();
                }
            }
        });
    }

    /**
     * Hides the card telling the user data about the specific habit
     * Shows a card telling the user they have not tracked enough data yet
     */
    private void showNoDataView() {
        clPercent.setVisibility(View.GONE);
        clPercentNotEnough.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the percent change completing the displayed habit has on the user's mood
     *
     * @param resultsArray the results matrix of coefficients from the MultipleLinearRegression
     * @param habitIndex   the index corresponding to the displayed habit in the results matrix
     */
    private void displayPercentChange(double[] resultsArray, int habitIndex) {
        habitCoefficient = resultsArray[habitIndex];
        percentChange = ((int) Math.round(100 * ((double) habitCoefficient / averageMood)));
        if (percentChange >= 0) {
            increaseOrDecrease = "increases";
        } else {
            increaseOrDecrease = "decreases";
        }
        tvPercent.setText("Completing this habit " + increaseOrDecrease + " your mood by " + Math.abs(percentChange) + " percent!");
        clPercent.setVisibility(View.VISIBLE);
        clPercentNotEnough.setVisibility(View.GONE);
    }

    /**
     * Finds the index of the desired habit in the habit list
     *
     * @return the habit's index
     */
    private int findIndexOfHabit() {
        int index = -1;
        for (int i = 0; i < numHabits; i++) {
            if (habitsList.get(i).equals(habit.getHabitName())) {
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

    /**
     * links the MoodFragment instance variables with the ContentView elements
     */
    private void initViews() {
        ivHabit = findViewById(R.id.ivHabit);
        tvHabit = findViewById(R.id.tvHabit);
        btnBack = findViewById(R.id.btnBack);
        clPercent = findViewById(R.id.clPercent);
        clPercentNotEnough = findViewById(R.id.clPercentNotEnough);
        tvPercent = findViewById(R.id.tvPercent);
        tvPercentNotEnough = findViewById(R.id.tvPercentNotEnough);
        tvStreak = findViewById(R.id.tvStreak);
        tvFirstDay = findViewById(R.id.tvFirstDay);
        tvLastDay = findViewById(R.id.tvLastDay);
        tvLastTen = findViewById(R.id.tvLastTen);
        clLastTen = findViewById(R.id.clLastTen);
        ivDayOne = findViewById(R.id.ivDayOne);
        ivDayTwo = findViewById(R.id.ivDayTwo);
        ivDayThree = findViewById(R.id.ivDayThree);
        ivDayFour = findViewById(R.id.ivDayFour);
        ivDayFive = findViewById(R.id.ivDayFive);
        ivDaySix = findViewById(R.id.ivDaySix);
        ivDaySeven = findViewById(R.id.ivDaySeven);
        ivDayEight = findViewById(R.id.ivDayEight);
        ivDayNine = findViewById(R.id.ivDayNine);
        ivDayTen = findViewById(R.id.ivDayTen);
    }
}