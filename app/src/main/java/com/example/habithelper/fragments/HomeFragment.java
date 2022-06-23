package com.example.habithelper.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.LoginActivity;
import com.example.habithelper.MainActivity;
import com.example.habithelper.OLSMultipleLinearRegression;
import com.example.habithelper.R;
import com.example.habithelper.TrackDay;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.math3.linear.RealVector;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

public class HomeFragment extends Fragment {

    public static final String GET_WEATHER_URL="https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";

    Button btnLogout;
    TextView tvHello;
    TextView tvThreeOne;
    TextView tvThreeTwo;
    TextView tvThreeThree;
    ConstraintLayout clThree;
    ConstraintLayout clNotEnoughHabits;
    TextView tvNumDaysTracked;
    ConstraintLayout clFront;
    ConstraintLayout clBack;
    TextView tvCompletedPortion;
    ProgressBar pbCompletedPortion;

    double[] y;
    double[][] x;
    public int dimension_one;
    public int dimension_two;
    public List<Object> habitsList;
    private int numChecked=0;
    private int numHabits;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        btnLogout=view.findViewById(R.id.btnLogout);
        tvHello=view.findViewById(R.id.tvHello);
        tvThreeOne=view.findViewById(R.id.tvThreeOne);
        tvThreeTwo=view.findViewById(R.id.tvThreeTwo);
        tvThreeThree=view.findViewById(R.id.tvThreeThree);
        clThree=view.findViewById(R.id.clThree);
        clNotEnoughHabits=view.findViewById(R.id.clNotEnoughHabits);
        tvNumDaysTracked=view.findViewById(R.id.tvNumDaysTracked);
        clBack = view.findViewById(R.id.clBack);
        clFront = view.findViewById(R.id.clFront);
        tvCompletedPortion = view.findViewById(R.id.tvCompletedPortion);
        pbCompletedPortion = view.findViewById(R.id.pbCompletedPortion);

        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.cardflip_exit_animation);
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.cardflip_animation);

        float scale = getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        ParseUser currentUser = ParseUser.getCurrentUser();
        String name = currentUser.getString("name");
        tvHello.setText("Nice to see you again, "+name);

        habitsList = currentUser.getList("habitsList");
        numHabits=habitsList.size();

        countNumCompleted();


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
                dimension_one=daysTracked.size();
                if(dimension_one>=5){
                    clThree.setVisibility(view.VISIBLE);
                    dimension_two=daysTracked.get(0).getTrackArray().size();
                    if(dimension_one>dimension_two){
                        y = new double[dimension_one];
                        x = new double[dimension_one][dimension_two]; //first is data, second is predictors
                        for (int i = 0; i<daysTracked.size(); i++){
                            List<Integer> trackDayArray = daysTracked.get(i).getTrackArray();
                            y[i]=daysTracked.get(i).getMood();
                            double[] myList = new double[trackDayArray.size()];
                            for (int k=0; k<trackDayArray.size(); k++) {
                                myList[k] = (double) trackDayArray.get(k);
                            }
                            x[i]=myList;
                        }
                        OLSMultipleLinearRegression myMLR = new OLSMultipleLinearRegression();
                        myMLR.newSampleData(y,x);
                        RealVector resultsVector = myMLR.calculateBeta();
                        double[] tempArray = resultsVector.toArray();
                        double[] resultsArray = new double[tempArray.length-1];
                        for (int i=1; i<tempArray.length; i++){
                            resultsArray[i-1]=tempArray[i];
                        }

                        //getting the three most impactful habits on a user's mood
                        try {
                            Object firstObject = currentUser.getJSONArray("habitsList").get(getIndexOfLargest(resultsArray));
                            String firstString = String.valueOf(firstObject);
                            tvThreeOne.setText("1. "+firstString);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Object secondObject = currentUser.getJSONArray("habitsList").get(getIndexOfSecondLargest(resultsArray));
                            String secondString = String.valueOf(secondObject);
                            tvThreeTwo.setText("2. "+secondString);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Object thirdObject = currentUser.getJSONArray("habitsList").get(getIndexOfThirdLargest(resultsArray));
                            String thirdString = String.valueOf(thirdObject);
                            tvThreeThree.setText("3. "+thirdString);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        clThree.setVisibility(view.GONE);
                        if(dimension_one!=1){
                            tvNumDaysTracked.setText("So far, you've been tracking for "+String.valueOf(dimension_one)+" days!");

                        } else {
                            tvNumDaysTracked.setText("So far, you've been tracking for 1 day!");
                        }
                        clNotEnoughHabits.setVisibility(view.VISIBLE);
                    }
                        } else {
                            clThree.setVisibility(view.GONE);
                            if(dimension_one!=1){
                            tvNumDaysTracked.setText("So far, you've been tracking for "+String.valueOf(dimension_one)+" days!");

                    } else {
                        tvNumDaysTracked.setText("So far, you've been tracking for 1 day!");
                    }
                    clNotEnoughHabits.setVisibility(view.VISIBLE);
                }
            }
        });

        // implementing the double click to flip the card gesture
        clThree.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    flipCard();
                    return false;
                }
            });
            @Override
            public boolean onTouch(View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent i = new Intent (getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    // counts the number of habits the user has done so far today
    private void countNumCompleted() {
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request=GET_WEATHER_URL+ParseUser.getCurrentUser().getString("zipCode");
        client.get(api_request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONObject location = jsonObject.getJSONObject("location");
                    String locationTime = location.getString("localtime");
                    String locationDate=locationTime.split(" ")[0];
                    String locationName = location.getString("name");
                    String[] dateParts = locationDate.split("-", 3);
                    String dateStringInt = dateParts[0]+dateParts[1]+dateParts[2];
                    try{
                        int dateInt = Integer.parseInt(dateStringInt);
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
                        query.include(TrackDay.KEY_PARENT_USER);
                        query.include(TrackDay.KEY_DATE_NUMBER);
                        query.orderByDescending(TrackDay.KEY_DATE_NUMBER);
                        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
                        query.whereEqualTo(TrackDay.KEY_DATE_NUMBER, dateInt);
                        query.findInBackground(new FindCallback<TrackDay>() {
                            @Override
                            public void done(List<TrackDay> daysTracked, ParseException e) {
                                if (e != null) {
                                    return;
                                }

                                // the user has tracked some habits today
                                if(daysTracked.size()>=1){
                                    TrackDay firstTracked=daysTracked.get(0);
                                    List<Integer> firstHabitsTracked = firstTracked.getTrackArray();
                                    for(int i = 0; i<firstHabitsTracked.size(); i++){
                                        numChecked+=firstHabitsTracked.get(i);
                                    }
                                }
                                tvCompletedPortion.setText("Today, you have completed "+String.valueOf(numChecked)+" of "+numHabits+" habits");
                                pbCompletedPortion.setProgress((int) Math.round(100*((double) numChecked/numHabits)));
                            }
                        });
                    }
                    catch (NumberFormatException ex){
                        ex.printStackTrace();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                return;
            }
        });
    }

    public int getIndexOfLargest(double[] array){
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ) largest = i;
        }
        return largest; // position of the first largest found
    }

    public int getIndexOfSecondLargest(double[] array){
        int largest;
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int max = getIndexOfLargest(array);
        if (max == 0){
            largest = 1;
        } else {
            largest = 0;
        }
        for ( int i = largest+1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] && array[i]<array[max]){
                largest = i;
            }
        }
        return largest; // position of the first largest found
    }

    public int getIndexOfThirdLargest(double[] array){
        int largest;
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int max = getIndexOfLargest(array);
        int secondMax = getIndexOfSecondLargest(array);
        if(max==0 || secondMax==0){
            if(max==1 || secondMax==1){
                largest=2;
            } else {
                largest=1;
            }
        } else {
            largest = 0;
        }

        for ( int i = largest+1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] && array[i]<array[secondMax]){
                largest = i;
            }
        }
        return largest; // position of the first largest found
    }

    public void flipCard() {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(clFront);
            mSetLeftIn.setTarget(clBack);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(clBack);
            mSetLeftIn.setTarget(clFront);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }
}