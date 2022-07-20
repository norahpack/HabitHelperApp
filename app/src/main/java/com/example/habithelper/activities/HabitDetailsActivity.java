package com.example.habithelper.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.example.habithelper.R;
import com.example.habithelper.models.Habit;
import com.example.habithelper.models.TrackDay;
import com.example.habithelper.utilities.LinearRegressionCalculator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.parceler.Parcels;
import java.util.Arrays;
import java.util.List;

public class HabitDetailsActivity extends AppCompatActivity {

    public static final int NUM_DAYS_FOR_FIGURE = 10;
    public static final int DAY_NUM_TO_INDEX_OFFSET = 1;

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
    private ImageView ivLastTenHidden;
    private Habit habit;
    private ParseUser currentUser;
    private ConstraintLayout clLastTen;
    private ConstraintLayout clPercent;
    private ConstraintLayout clPercentNotEnough;
    private TextView tvLastTen;
    private TextView tvHabit;
    private TextView tvFirstDay;
    private TextView tvLastDay;
    private TextView tvPercent;
    private TextView tvStreak;
    private ProgressBar pbLoadingDetailView;
    public int numDaysTracked;
    private int numHabits = 0;
    LinearRegressionCalculator lrc;
    public List<Object> habitsList;
    public double moodSum = 0;
    public double habitCoefficient;
    public int percentChange;
    public double averageMood;
    public String increaseOrDecrease;
    public int habitStreak = 0;
    public boolean streakLost = false;
    public boolean showPercentChange;
    public List<ImageView> lastTenButtons;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);
        initViews();
        lastTenButtons = Arrays.asList(ivDayOne, ivDayTwo, ivDayThree, ivDayFour, ivDayFive, ivDaySix, ivDaySeven, ivDayEight, ivDayNine, ivDayTen);

        if (getIntent().getParcelableExtra(Habit.class.getSimpleName()) != null) {
            habit = Parcels.unwrap(getIntent().getParcelableExtra(Habit.class.getSimpleName()));
        }
        currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");

        if(habitsList != null){
            numHabits = habitsList.size();
        }

        getMostImpactfulHabits();
        tvHabit.setText(habit.getHabitName());
        setupIvHabit();
        calculateStreak();

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * OnClick of an item in the navBar, performs the action specified
     * @param item the item the user clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent(HabitDetailsActivity.this, MainActivity.class);
        i.putExtra("tab", "habits");
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
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
                if (daysTracked.size() >= NUM_DAYS_FOR_FIGURE) {
                    int minDay = daysTracked.size() - NUM_DAYS_FOR_FIGURE;
                    int maxDay = daysTracked.size() - DAY_NUM_TO_INDEX_OFFSET;
                    int habitIndex = findIndexOfHabit();
                    for (int i = 0; i < NUM_DAYS_FOR_FIGURE; i++) {
                        if (daysTracked.get(i + minDay).getTrackArray().get(habitIndex) == 1) {
                            setCompletedBubble(i);
                        }
                        pbLoadingDetailView.setVisibility(View.GONE);
                        tvFirstDay.setText(String.valueOf(minDay + DAY_NUM_TO_INDEX_OFFSET));
                        tvLastDay.setText(String.valueOf(maxDay + DAY_NUM_TO_INDEX_OFFSET));
                        tvLastTen.setVisibility(View.VISIBLE);
                        clLastTen.setVisibility(View.VISIBLE);
                    }
                } else {
                    // the user has tracked for fewer than ten days
                    pbLoadingDetailView.setVisibility(View.GONE);
                    ivLastTenHidden.setVisibility(View.VISIBLE);
                }
                if (showPercentChange){
                    clPercent.setVisibility(View.VISIBLE);
                } else {
                    clPercentNotEnough.setVisibility(View.VISIBLE);
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
        ImageView ivToFillIn = lastTenButtons.get(i);
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
                for (int i = 0; i < daysTracked.size() && !streakLost; i++) {
                    if (daysTracked.get(i).getTrackArray().get(habitIndex) == 1) {
                        habitStreak += 1;
                    } else {
                        streakLost = true;
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
        ParseFile image = habit.getHabitCustomIcon();
        if (image != null){
            Glide.with(HabitDetailsActivity.this).load(image.getUrl()).into(ivHabit);
        } else {
            Resources resources = getResources();
            if (habit.get("habitImageName") != null) {
                int resId = resources.getIdentifier(habit.getHabitImageKey(), "drawable", "com.example.habithelper");
                Glide.with(HabitDetailsActivity.this).load(AppCompatResources.getDrawable(HabitDetailsActivity.this, resId)).into(ivHabit);
            } else {
                Glide.with(HabitDetailsActivity.this).load(R.drawable.starslarge).into(ivHabit);
            }
            ivHabit.setColorFilter(resources.getColor(R.color.sienna)); // Add tint color
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
                    if (habitIndex != -1 && resultsArray != null) {
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
        showPercentChange = false;
        setupLastTenDays();
    }

    /**
     * Displays the percent change completing the displayed habit has on the user's mood
     *
     * @param resultsArray the results matrix of coefficients from the MultipleLinearRegression
     * @param habitIndex   the index corresponding to the displayed habit in the results matrix
     */
    private void displayPercentChange(double[] resultsArray, int habitIndex) {
        habitCoefficient = resultsArray[habitIndex];
        percentChange = ((int) Math.round(100 * (habitCoefficient / averageMood)));
        if (percentChange >= 0) {
            increaseOrDecrease = "increases";
        } else {
            increaseOrDecrease = "decreases";
        }
        tvPercent.setText("Completing this habit " + increaseOrDecrease + " your mood by " + Math.abs(percentChange) + " percent!");
        showPercentChange = true;
        setupLastTenDays();
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
        clPercent = findViewById(R.id.clPercent);
        clPercentNotEnough = findViewById(R.id.clPercentNotEnough);
        tvPercent = findViewById(R.id.tvPercent);
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
        ivLastTenHidden = findViewById(R.id.ivLastTenHidden);
        pbLoadingDetailView = findViewById(R.id.pbLoadingDetailView);
    }
}