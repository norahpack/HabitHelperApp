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
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.activities.LoginActivity;
import com.example.habithelper.utilities.OLSMultipleLinearRegression;
import com.example.habithelper.R;
import com.example.habithelper.models.TrackDay;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.math3.linear.RealVector;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class HomeFragment extends Fragment {

    public static final String GET_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";
    public static final int CAMERA_DISTANCE = 8000;

    Button btnLogout;
    ConstraintLayout clThree;
    ConstraintLayout clNotEnoughHabits;
    ConstraintLayout clFront;
    ConstraintLayout clBack;
    TextView tvNumDaysTracked;
    TextView tvHello;
    TextView tvThreeOne;
    TextView tvThreeTwo;
    TextView tvThreeThree;
    TextView tvCompletedPortion;
    ProgressBar pbCompletedPortion;

    double[] moodList;
    double[][] habitHistoryList;
    public int numDaysTracked;
    private int numChecked = 0;
    private int numHabits;
    public List<Object> habitsList;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    String firstName;
    ParseUser currentUser;

    public HomeFragment() {
        // Required empty public constructor in order to initialize fragment from MainActivity
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // links the HomeFragment instance variables with the ContentView elements
        initViews(view);

        // loads the cardFlip animations and adjusts the view camera
        loadAnimations(view);

        // grabs data about the user from the parse database
        initializeUserVariables();

        // counts and displays the number of habits the user has tracked today
        countNumCompleted();

        // uses linear regression to determine and display which of the user's habits have the biggest impact on their mood
        getMostImpactfulHabits(view);

        // implementing the double click to flip the card gesture
        clThree.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
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
                currentUser = ParseUser.getCurrentUser();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }

    /**
     * Determines whether the user has been tracking habits long enough to analyze their past data
     * If so, calls a method to determine and display their most impactful habits
     * If not, displays a card to tell the user to continue tracking.
     *
     * @param view the current view
     */
    private void getMostImpactfulHabits(View view) {
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
                    // calculates the array of correlation coefficients of each habit on the user's mood
                    double[] resultsArray = performLeastSquares(daysTracked);
                    clNotEnoughHabits.setVisibility(view.GONE);
                    clThree.setVisibility(view.VISIBLE);
                    // getting and displaying the three most impactful habits on a user's mood
                    setMostImpactfulText(resultsArray);
                } else {
                    notEnoughDays(view);
                }
            }
        });
    }

    /**
     * Determines and displays the three most impactful habits on a user's mood
     *
     * @param resultsArray an array of coefficients corresponding to the correlation of each habit to the user's mood
     */
    private void setMostImpactfulText(double[] resultsArray) {
        try {
            Object firstObject = currentUser.getJSONArray("habitsList").get(getIndexOfLargest(resultsArray));
            String firstString = String.valueOf(firstObject);
            tvThreeOne.setText("1. " + firstString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            Object secondObject = currentUser.getJSONArray("habitsList").get(getIndexOfSecondLargest(resultsArray));
            String secondString = String.valueOf(secondObject);
            tvThreeTwo.setText("2. " + secondString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            Object thirdObject = currentUser.getJSONArray("habitsList").get(getIndexOfThirdLargest(resultsArray));
            String thirdString = String.valueOf(thirdObject);
            tvThreeThree.setText("3. " + thirdString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Performs least squares linear regression on the user's list of moods and habits
     *
     * @param daysTracked the list holding every TrackDay Parse object corresponding to the currentUser
     * @return an array of coefficients corresponding to the correlation of each habit to the user's mood
     */
    private double[] performLeastSquares(List<TrackDay> daysTracked) {
        moodList = new double[numDaysTracked];
        habitHistoryList = new double[numDaysTracked][numHabits]; //first is data, second is predictors

        // putting the data from the Parse database into lists that we can perform OLSMultipleLinearRegression on
        for (int i = 0; i < numDaysTracked; i++) {
            List<Integer> trackDayArray = daysTracked.get(i).getTrackArray();
            moodList[i] = daysTracked.get(i).getMood();
            double[] coefficientList = new double[trackDayArray.size()];
            for (int k = 0; k < trackDayArray.size(); k++) {
                coefficientList[k] = (double) trackDayArray.get(k);
            }
            habitHistoryList[i] = coefficientList;
        }

        OLSMultipleLinearRegression myMLR = new OLSMultipleLinearRegression();
        myMLR.newSampleData(moodList, habitHistoryList);
        RealVector resultsVector = myMLR.calculateBeta();
        double[] tempArray = resultsVector.toArray();

        // removes the y-intercept value from the array of predictor coefficients
        double[] resultsArray = new double[tempArray.length - 1];
        for (int i = 1; i < tempArray.length; i++) {
            resultsArray[i - 1] = tempArray[i];
        }
        return resultsArray;
    }

    /**
     * Displays a card stating that the user has not tracked enough days to unlock analysis features
     *
     * @param view the current view
     */
    private void notEnoughDays(View view) {
        clThree.setVisibility(view.GONE);
        if (numDaysTracked != 1) {
            tvNumDaysTracked.setText("So far, you've been tracking for " + String.valueOf(numDaysTracked) + " days!");
        } else {
            tvNumDaysTracked.setText("So far, you've been tracking for 1 day!");
        }
        clNotEnoughHabits.setVisibility(view.VISIBLE);
    }

    /**
     * Gets information about the current user from the Parse database
     */
    private void initializeUserVariables() {
        currentUser = ParseUser.getCurrentUser();
        firstName = currentUser.getString("name");
        tvHello.setText("Nice to see you again, " + firstName);
        habitsList = currentUser.getList("habitsList");
        numHabits = habitsList.size();
    }

    /**
     * Inflates the animations and adjusts the view camera
     */
    private void loadAnimations(View view) {
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.cardflip_exit_animation);
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.cardflip_animation);
        float scale = getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(CAMERA_DISTANCE * scale);
    }

    /**
     * links the HomeFragment instance variables with the ContentView elements
     */
    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        tvHello = view.findViewById(R.id.tvHello);
        tvThreeOne = view.findViewById(R.id.tvThreeOne);
        tvThreeTwo = view.findViewById(R.id.tvThreeTwo);
        tvThreeThree = view.findViewById(R.id.tvThreeThree);
        clThree = view.findViewById(R.id.clThree);
        clNotEnoughHabits = view.findViewById(R.id.clNotEnoughHabits);
        tvNumDaysTracked = view.findViewById(R.id.tvNumDaysTracked);
        clBack = view.findViewById(R.id.clBack);
        clFront = view.findViewById(R.id.clFront);
        tvCompletedPortion = view.findViewById(R.id.tvCompletedPortion);
        pbCompletedPortion = view.findViewById(R.id.pbCompletedPortion);
    }

    /**
     * Counts the number of habits the user has tracked so far today
     */
    private void countNumCompleted() {
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = GET_WEATHER_URL + ParseUser.getCurrentUser().getString("zipCode");
        client.get(api_request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                String dateStringInt = getDateString(jsonObject);
                try {
                    int dateInt = Integer.parseInt(dateStringInt);
                    findPortionCompleted(dateInt);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                return;
            }
        });
    }

    /**
     * determines and displays the portion of the user's habits they have completed that day.
     *
     * @param dateInt
     */
    private void findPortionCompleted(int dateInt) {
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
                if (daysTracked.size() >= 1) {
                    TrackDay firstTracked = daysTracked.get(0);
                    List<Integer> firstHabitsTracked = firstTracked.getTrackArray();
                    for (int i = 0; i < firstHabitsTracked.size(); i++) {
                        numChecked += firstHabitsTracked.get(i);
                    }
                }
                tvCompletedPortion.setText("Today, you have completed " + String.valueOf(numChecked) + " of " + numHabits + " habits");
                pbCompletedPortion.setProgress((int) Math.round(100 * ((double) numChecked / numHabits)));
            }
        });
    }

    /**
     * uses an API call to determine the current date in the user's timezone
     *
     * @param jsonObject the JSONObject representing the result of the API call to weatherAPI
     * @return a string representing the current date in the user's timezone (e.g. 20220619 for June 19, 2022)
     */
    private String getDateString(JSONObject jsonObject) {
        try {
            JSONObject location = jsonObject.getJSONObject("location");
            String locationTime = location.getString("localtime");
            String locationDate = locationTime.split(" ")[0];
            String[] dateParts = locationDate.split("-", 3);
            String dateString = dateParts[0] + dateParts[1] + dateParts[2];
            return dateString;
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * returns the index of the largest element in an array
     *
     * @param array the array to find the largest element in
     * @return the index of the largest element in the array
     */
    public int getIndexOfLargest(double[] array) {
        if (array == null || array.length == 0) {
            return -1; // null or empty
        }
        int largest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[largest]) largest = i;
        }
        return largest; // index of the first largest element found
    }

    /**
     * returns the index of the second largest element in an array
     *
     * @param array the array to find the second largest element in
     * @return the index of the second largest element in the array
     */
    public int getIndexOfSecondLargest(double[] array) {
        int secondLargest;
        if (array == null || array.length == 0) {
            return -1; // null or empty
        }
        int largest = getIndexOfLargest(array);
        if (largest == 0) {
            secondLargest = 1;
        } else {
            secondLargest = 0;
        }
        for (int i = secondLargest + 1; i < array.length; i++) {
            if (array[i] > array[secondLargest] && array[i] < array[largest]) {
                secondLargest = i;
            }
        }
        return secondLargest; // index of the second largest element found
    }

    /**
     * returns the index of the third largest element in an array
     *
     * @param array the array to find the third largest element in
     * @return the index of the third largest element in the array
     */
    public int getIndexOfThirdLargest(double[] array) {
        int thirdLargest;
        if (array == null || array.length == 0) {
            return -1; // null or empty
        }
        int largest = getIndexOfLargest(array);
        int secondLargest = getIndexOfSecondLargest(array);
        if (largest == 0 || secondLargest == 0) {
            if (largest == 1 || secondLargest == 1) {
                thirdLargest = 2;
            } else {
                thirdLargest = 1;
            }
        } else {
            thirdLargest = 0;
        }
        for (int i = thirdLargest + 1; i < array.length; i++) {
            if (array[i] > array[thirdLargest] && array[i] < array[secondLargest]) {
                thirdLargest = i;
            }
        }
        return thirdLargest; // index of the third largest element found
    }

    /**
     * Performs the animation to flip the card from a view of the user's top most impactful habits
     * To a view telling them what portion of their habits they have completed today (and vice versa)
     */
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