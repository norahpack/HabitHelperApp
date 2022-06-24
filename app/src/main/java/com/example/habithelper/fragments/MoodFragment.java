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

    public TextView tvAverageMood;
    public ImageView ivAverageMood;
    public double moodSum = 0;
    public double averageMood = 0;
    public ConstraintLayout clNotEnoughDays;
    TextView tvNumDaysTracked;
    GraphView gvMood;
    ConstraintLayout clMoodBarGraph;
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
        // links the MoodFragment instance variables with the ContentView elements
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
                // tells the user their average mood
                setAverageMood(daysTracked);

                // sets the graph of moods for the user
                if (daysTracked.size() >= 5) {
                    setMoodGraph(daysTracked, view);
                } else {
                    setNotEnoughDays(daysTracked, view);
                }
            }
        });
    }

    /**
     * Displays an element that tells the user they have not tracked long enough to enjoy analysis/graph features
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     * @param view        the current view
     */
    private void setNotEnoughDays(List<TrackDay> daysTracked, View view) {
        clMoodBarGraph.setVisibility(view.GONE);
        clNotEnoughDays.setVisibility(view.VISIBLE);
        if (daysTracked.size() != 1) {
            tvNumDaysTracked.setText("So far, you've been tracking for " + String.valueOf(daysTracked.size()) + " days!");
        } else {
            tvNumDaysTracked.setText("So far, you've been tracking for 1 day!");
        }
    }

    /**
     * Displays a line graph corresponding to the user's mood over time
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     * @param view        the current view
     */
    private void setMoodGraph(List<TrackDay> daysTracked, View view) {
        clMoodBarGraph.setVisibility(view.VISIBLE);
        clNotEnoughDays.setVisibility(view.GONE);
        DataPoint[] moodDataPoints = new DataPoint[daysTracked.size()];
        for (int i = 0; i < daysTracked.size(); i++) {
            int dayMood = daysTracked.get(i).getMood();
            moodDataPoints[i] = new DataPoint(i + 1, dayMood);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(moodDataPoints);
        series.setColor(Color.parseColor("#A44F30"));
        gvMood.addSeries(series);
        instantiateGraphSettings(daysTracked);
    }

    /**
     * Adjusts the view and parameters of the mood graph (e.g. makes it scrollable)
     *
     * @param daysTracked the list of TrackDay objects corresponding to the current user
     */
    private void instantiateGraphSettings(List<TrackDay> daysTracked) {
        // makes sure the bounds of the graphs are accurate
        gvMood.getViewport().setScrollable(true);
        gvMood.getViewport().isScrollable();
        gvMood.getViewport().scrollToEnd();
        gvMood.getViewport().setMinY(MIN_Y);
        gvMood.getViewport().setMaxY(MAX_Y);
        gvMood.getViewport().setMinX(MIN_DAY);
        gvMood.getViewport().setMaxX(daysTracked.size());
        if (daysTracked.size() >= MAX_DAYS_ONE_SCREEN) {
            gvMood.getViewport().setMinX(daysTracked.size() - MAX_DAYS_ONE_SCREEN + 1);
        }
        gvMood.getViewport().setYAxisBoundsManual(true);
        gvMood.getViewport().setXAxisBoundsManual(true);
        gvMood.getGridLabelRenderer().setPadding(LABEL_PADDING);
        gvMood.getGridLabelRenderer().setHorizontalLabelsAngle(LABEL_ANGLE);
        gvMood.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
            }
            averageMood = moodSum / (daysTracked.size());
            tvAverageMood.setText(dfZero.format(averageMood) + "/5.00");
            setIvAverageMood();
        }
    }

    /**
     * links the MoodFragment instance variables with the ContentView elements
     *
     * @param view the current view
     */
    private void initViews(View view) {
        tvAverageMood = view.findViewById(R.id.tvAverageMood);
        ivAverageMood = view.findViewById(R.id.ivAverageMood);
        clNotEnoughDays = view.findViewById(R.id.clNotEnoughDays);
        tvNumDaysTracked = view.findViewById(R.id.tvNumDaysTracked);
        clMoodBarGraph = view.findViewById(R.id.clMoodBarGraph);
        gvMood = view.findViewById(R.id.gvMood);
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