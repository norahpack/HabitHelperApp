package com.example.habithelper.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.habithelper.R;
import com.example.habithelper.models.TrackDay;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.text.DecimalFormat;
import java.util.List;


public class MoodFragment extends Fragment {

    private static final DecimalFormat dfZero = new DecimalFormat("0.00");
    public static final int MIN_Y = 1;
    public static final int MAX_Y = 5;
    public static final int MIN_DAY = 1;
    public static final int MAX_DAYS_ONE_SCREEN = 20;
    public static final int LABEL_PADDING = 29;
    public static final int LABEL_ANGLE = 30;

    public double moodSum = 0;
    public double averageMood = 0;
    public double numMood = 0;
    private ImageView ivAverageMood;
    private TextView tvNumDaysTracked;
    private TextView tvAverageMood;
    private TextView tvPercentChange;
    private GraphView graphViewMood;
    private ConstraintLayout clNotEnoughDays;
    private ConstraintLayout clMoodBarGraph;
    private ProgressBar pbLoadingAverageMood;
    private ProgressBar pbLoadingMoodGraph;
    ParseUser currentUser;

    public MoodFragment() {
        // Required empty public constructor to be able to initialize a fragment from mainActivity
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        currentUser = ParseUser.getCurrentUser();

        // attempts to determine the user's average mood and display a graph of the user's mood over time
        queryUserData(view);
    }

    /**
     * Attempts to determine the user's average mood and display a graph of the user's mood over time
     * @param view the current view
     */
    private void queryUserData(View view) {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.orderByAscending("dateNumber");
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                setAverageMood(daysTracked);
                // sets the graph of moods for the user
                if (daysTracked.size() >= 5) {
                    setMoodGraph(daysTracked, view);
                } else {
                    setNotEnoughDays(daysTracked, view);
                }
                if (daysTracked.size() >= 14) {
                    setPercentChange(daysTracked);
                } else {
                    tvPercentChange.setText("You're on your way to a happier and healthier future. Keep up the good work.");
                }
            }
        });
    }

    /**
     * Displays the percent change in the user's mood in the past seven days
     * @param daysTracked the list of TrackDay objects corresponding to the user, ordered by date (ascending)
     */
    private void setPercentChange(List<TrackDay> daysTracked) {
        double currentSevenAverage = getWeekAverage(daysTracked.size() - 1, daysTracked);
        double lastSevenAverage = getWeekAverage(daysTracked.size() - 8, daysTracked);
        double percentChange = 100.0*((currentSevenAverage - lastSevenAverage) / (lastSevenAverage));
        String upOrDown = "down";
        if (percentChange >= 0){
            upOrDown = "up";
        }
        tvPercentChange.setText("Your mood this week is " + upOrDown + " "+dfZero.format(Math.abs(percentChange)) + "% from last week");
    }

    /**
     * returns the average mood over the seven trackDays in question
     * @param daysTracked the list of TrackDay objects corresponding to the user, ordered by date (ascending)
     * @param startIndex the index in daysTracked of the most recent day we are considering
     * @return the average mood score for the seven days tracked starting from startIndex.
     */
    private double getWeekAverage(int startIndex, List<TrackDay> daysTracked) {
        double totalMood = 0;
        for (int i = startIndex; i > startIndex - 7; i--){
            if(i >= 0 && i < daysTracked.size()){
                totalMood+=daysTracked.get(i).getMood();
            }
        }
        return totalMood/7.0;
    }

    /**
     * Displays an element that tells the user they have not tracked long enough to enjoy analysis/graph features
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     * @param view        the current view
     */
    private void setNotEnoughDays(List<TrackDay> daysTracked, View view) {
        if (daysTracked.size() != 1) {
            tvNumDaysTracked.setText("So far, you've been tracking for " + String.valueOf(daysTracked.size()) + " days");
        } else {
            tvNumDaysTracked.setText("So far, you've been tracking for 1 day");
        }
        pbLoadingMoodGraph.setVisibility(View.GONE);
        clNotEnoughDays.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a line graph corresponding to the user's mood over time
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     * @param view        the current view
     */
    private void setMoodGraph(List<TrackDay> daysTracked, View view) {
        DataPoint[] moodDataPoints = new DataPoint[daysTracked.size()];
        for (int i = 0; i < daysTracked.size(); i++) {
            int dayMood = daysTracked.get(i).getMood();
            moodDataPoints[i] = new DataPoint(i + 1, dayMood);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(moodDataPoints);
        series.setColor(Color.parseColor("#A44F30"));
        graphViewMood.addSeries(series);
        instantiateGraphSettings(daysTracked);
        pbLoadingMoodGraph.setVisibility(View.GONE);
        clMoodBarGraph.setVisibility(View.VISIBLE);
    }

    /**
     * Adjusts the view and parameters of the mood graph (e.g. makes it scrollable)
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     */
    private void instantiateGraphSettings(List<TrackDay> daysTracked) {
        // makes sure the bounds of the graphs are accurate
        graphViewMood.getViewport().setScrollable(true);
        graphViewMood.getViewport().isScrollable();
        graphViewMood.getViewport().scrollToEnd();
        graphViewMood.getViewport().setMinY(MIN_Y);
        graphViewMood.getViewport().setMaxY(MAX_Y);
        graphViewMood.getViewport().setMinX(MIN_DAY);
        graphViewMood.getViewport().setMaxX(daysTracked.size());
        if (daysTracked.size() >= MAX_DAYS_ONE_SCREEN) {
            graphViewMood.getViewport().setMinX(daysTracked.size() - MAX_DAYS_ONE_SCREEN + 1);
        }
        graphViewMood.getViewport().setYAxisBoundsManual(true);
        graphViewMood.getViewport().setXAxisBoundsManual(true);
        graphViewMood.getGridLabelRenderer().setPadding(LABEL_PADDING);
        graphViewMood.getGridLabelRenderer().setHorizontalLabelsAngle(LABEL_ANGLE);
        graphViewMood.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            }
        });
    }

    /**
     * Determines and displays the user's average mood
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     */
    private void setAverageMood(List<TrackDay> daysTracked) {
        if (daysTracked.size() > 0) {
            for (int i = 0; i < daysTracked.size(); i++) {
                moodSum += daysTracked.get(i).getMood();
                numMood += 1;
            }
            averageMood = moodSum / numMood;
            pbLoadingAverageMood.setVisibility(View.GONE);
            tvAverageMood.setText(dfZero.format(averageMood) + "/5.00");
            setIvAverageMood();
        } else {
            pbLoadingAverageMood.setVisibility(View.GONE);
            tvAverageMood.setText("not tracked yet");
            ivAverageMood.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.mood1));
        }
    }

    /**
     * links the MoodFragment instance variables with the ContentView elements
     *
     * @param view the current view
     */
    private void initViews(View view) {
        tvAverageMood = view.findViewById(R.id.tvAverageMood);
        tvNumDaysTracked = view.findViewById(R.id.tvNumDaysTracked);
        tvPercentChange = view.findViewById(R.id.tvPercentChange);
        ivAverageMood = view.findViewById(R.id.ivAverageMood);
        clNotEnoughDays = view.findViewById(R.id.clNotEnoughDays);
        clMoodBarGraph = view.findViewById(R.id.clMoodBarGraph);
        graphViewMood = view.findViewById(R.id.graphViewMood);
        pbLoadingAverageMood = view.findViewById(R.id.pbLoadingAverageMood);
        pbLoadingMoodGraph = view.findViewById(R.id.pbLoadingMoodGraph);
    }

    /**
     * sets the image icon corresponding to the user's average mood
     */
    private void setIvAverageMood() {
        Drawable imageToLoad;
        if (averageMood >= 4.5) {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood5);
        } else if (averageMood >= 3.5) {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood4);
        } else if (averageMood >= 2.5) {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood3);
        } else if (averageMood >= 1.5) {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood2);
        } else {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood1);
        }
        ivAverageMood.setBackground(imageToLoad);
    }
}