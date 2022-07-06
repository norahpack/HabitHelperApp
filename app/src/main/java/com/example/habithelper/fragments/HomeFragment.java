package com.example.habithelper.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
import com.example.habithelper.activities.LoginActivity;
import com.example.habithelper.models.TrackDay;
import com.example.habithelper.utilities.LinearRegressionCalculator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import okhttp3.Headers;

public class HomeFragment extends Fragment {

    public static final String GET_QUOTE_URL = "https://zenquotes.io/api";
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
    TextView tvQuote;
    TextView tvQuoteAuthor;
    ProgressBar pbCompletedPortion;

    public int numDaysTracked;
    private int numChecked = 0;
    private int numHabits;
    public List<Object> habitsList;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    public LinearRegressionCalculator lrc;
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

        initViews(view);
        loadAnimations(view);
        initializeUserVariables();
        countNumCompleted();
        setQuote();

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
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }

    private void setQuote() {
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = GET_QUOTE_URL + "/today";
        client.get(api_request, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray quoteArray = json.jsonArray;
                try {
                    JSONObject quoteArrayJSONObject = (quoteArray.getJSONObject(0));
                    tvQuote.setText("\"" + quoteArrayJSONObject.getString("q") + "\"");
                    tvQuoteAuthor.setText("-" + quoteArrayJSONObject.getString("a")+" ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                tvQuote.setText("\"For there is always light, if only we're brave enough to see it. If only we're brave enough to be it.\"");
                tvQuoteAuthor.setText("-Amanda Gorman");
                return;
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
                currentUser.put("numDaysTracked", numDaysTracked);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    }
                });

                // ensures the number of days the user has tracked is greater than the number of habits they are tracking
                // required for LeastSquaresRegression
                if (numDaysTracked > numHabits) {
                    lrc = new LinearRegressionCalculator();
                    // calculates the array of correlation coefficients of each habit on the user's mood
                    lrc.performLeastSquares(daysTracked, numDaysTracked, numHabits);
                    double[] resultsArray = lrc.leastSquaresResult;
                    clNotEnoughHabits.setVisibility(view.GONE);
                    clThree.setVisibility(view.VISIBLE);
                    // getting and displaying the three most impactful habits on a user's mood
                    setMostImpactfulText(resultsArray, lrc);
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
    private void setMostImpactfulText(double[] resultsArray, LinearRegressionCalculator lrc) {
        try {
            Object firstObject = currentUser.getJSONArray("habitsList").get(lrc.getIndexOfLargest(resultsArray));
            String firstString = String.valueOf(firstObject);
            tvThreeOne.setText("1. " + firstString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            Object secondObject = currentUser.getJSONArray("habitsList").get(lrc.getIndexOfSecondLargest(resultsArray));
            String secondString = String.valueOf(secondObject);
            tvThreeTwo.setText("2. " + secondString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            Object thirdObject = currentUser.getJSONArray("habitsList").get(lrc.getIndexOfThirdLargest(resultsArray));
            String thirdString = String.valueOf(thirdObject);
            tvThreeThree.setText("3. " + thirdString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
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
        tvQuote = view.findViewById(R.id.tvQuote);
        tvQuoteAuthor = view.findViewById(R.id.tvQuoteAuthor);
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