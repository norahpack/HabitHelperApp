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
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.habithelper.MainActivity;
import com.example.habithelper.R;
import com.example.habithelper.TrackDay;
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

    public TextView tvAverageMood;
    public ImageView ivAverageMood;
    public double moodSum = 0;
    public double averageMood=0;
    public ConstraintLayout clNotEnoughDays;
    TextView tvNumDaysTracked;
    GraphView gvMood;
    ConstraintLayout clMoodBarGraph;


    public MoodFragment() {
        // Required empty public constructor
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        tvAverageMood = view.findViewById(R.id.tvAverageMood);
        ivAverageMood = view.findViewById(R.id.ivAverageMood);
        clNotEnoughDays = view.findViewById(R.id.clNotEnoughDays);
        tvNumDaysTracked = view.findViewById(R.id.tvNumDaysTracked);
        clMoodBarGraph = view.findViewById(R.id.clMoodBarGraph);
        gvMood = view.findViewById(R.id.gvMood);


        ParseUser currentUser = ParseUser.getCurrentUser();
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
                if (daysTracked.size()>0){
                    for (int i = 0; i<daysTracked.size(); i++){
                        moodSum+=daysTracked.get(i).getMood();
                    }
                    averageMood = moodSum / (daysTracked.size());
                    tvAverageMood.setText(dfZero.format(averageMood)+"/5.00");
                    setIvAverageMood();
                }

                // sets the graph of moods for the user
                if (daysTracked.size()>=5){
                    clMoodBarGraph.setVisibility(view.VISIBLE);
                    clNotEnoughDays.setVisibility(view.GONE);
                    DataPoint[] moodDataPoints = new DataPoint[daysTracked.size()];
                    for (int i = 0; i<daysTracked.size(); i++) {
                        int dayMood = daysTracked.get(i).getMood();
                        moodDataPoints[i] = new DataPoint(i, dayMood);
                        System.out.println(moodDataPoints[i]);
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(moodDataPoints);
                    series.setColor(Color.parseColor("#A44F30"));
                    gvMood.addSeries(series);

                    // makes sure the bounds of the graphs are accurate
                    gvMood.getViewport().setMinY(1.0);
                    gvMood.getViewport().setMaxY(5.0);
                    gvMood.getViewport().setYAxisBoundsManual(true);
                } else {
                    clMoodBarGraph.setVisibility(view.GONE);
                    clNotEnoughDays.setVisibility(view.VISIBLE);
                    if(daysTracked.size()!=1){
                        tvNumDaysTracked.setText("So far, you've been tracking for "+String.valueOf(daysTracked.size())+" days!");

                    } else {
                        tvNumDaysTracked.setText("So far, you've been tracking for 1 day!");
                    }
                }
            }
        });
    }

    private void setIvAverageMood() {
        Drawable imageToLoad;
        if (averageMood >= 4.5){
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood5);
        } else if (averageMood >= 3.5){
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood4);
        } else if (averageMood >= 2.5){
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood3);
        } else if (averageMood >= 1.5){
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood2);
        } else {
            imageToLoad = AppCompatResources.getDrawable(getContext(), R.drawable.mood1);
        }
        ivAverageMood.setBackground(imageToLoad);
    }
}