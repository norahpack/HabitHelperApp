package com.example.habithelper.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.activities.MainActivity;
import com.example.habithelper.R;
import com.example.habithelper.models.TrackDay;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;


public class TrackFragment extends Fragment {

    public static final String GET_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";

    public TextView tvDate;
    public CheckBox cbOne;
    public CheckBox cbTwo;
    public CheckBox cbThree;
    public CheckBox cbFour;
    public CheckBox cbFive;
    public CheckBox cbSix;
    public CheckBox cbSeven;
    public CheckBox cbEight;
    public CheckBox cbNine;
    public CheckBox cbTen;
    public Button btnTrack;
    public RadioGroup radioGroup;
    public RadioButton rbOne;
    public RadioButton rbTwo;
    public RadioButton rbThree;
    public RadioButton rbFour;
    public RadioButton rbFive;

    public List<Object> habitsList;
    public int numHabits;
    public ParseUser currentUser;
    public List<Integer> todayHabits = new ArrayList<>();
    public String[] dateParts;

    public TrackFragment() {
        // Required empty public constructor to initialize a fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // links the TrackFragment instance variables with the ContentView elements
        initViews(view);
        initializeUserVariables(view);
        populateCheckboxes(numHabits);
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = GET_WEATHER_URL + currentUser.getString("zipCode");
        client.get(api_request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                getCurrentDate(jsonObject);
                setOldHabits();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                return;
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idChecked = radioGroup.getCheckedRadioButtonId();
                RadioButton finalChoice = view.findViewById(idChecked);
                int todayMood = (int) (finalChoice.getTag());

                populateTodayHabits(numHabits);
                saveTrack(todayMood, todayHabits);
            }
        });
    }

    /**
     * determines and displays the current date in the user's location
     *
     * @param jsonObject the response from the API call to weatherAPI
     */
    private void getCurrentDate(JSONObject jsonObject) {
        try {
            JSONObject location = jsonObject.getJSONObject("location");
            String locationTime = location.getString("localtime");
            String locationDate = locationTime.split(" ")[0];
            String locationName = location.getString("name");
            dateParts = locationDate.split("-", 3);
            String month = getMonth(dateParts[1]);
            String day = getDay(dateParts[2]);
            String year = dateParts[0];
            tvDate.setText("Today is " + month + " " + day + ", " + year + " in " + locationName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the String representation of an integer month
     *
     * @param datePart the integer value of the month
     * @return the String representation of the month
     */
    private String getMonth(String datePart) {
        int monthNum = Integer.valueOf(datePart);
        List<String> monthsList = new ArrayList<>(Arrays.asList("None", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
        return monthsList.get(monthNum);
    }

    /**
     * gets the String representation (e.g. 21st) of an integer day
     *
     * @param datePart the integer value of the day
     * @return the String representation of the day
     */
    private String getDay(String datePart) {
        int date = Integer.valueOf(datePart);
        if (date == 1 || date == 21 || date == 31) {
            return (datePart + "st");
        } else if (date == 2 || date == 22) {
            return (datePart + "nd");
        } else if (date == 3 || date == 23) {
            return (datePart + "rd");
        } else {
            return (datePart + "th");
        }
    }

    /**
     * Initializes user variables using information from the currentUser Parse Object
     *
     * @param view
     */
    private void initializeUserVariables(View view) {
        rbOne.setTag(1);
        rbTwo.setTag(2);
        rbThree.setTag(3);
        rbFour.setTag(4);
        rbFive.setTag(5);
        currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");
        numHabits = habitsList.size();
    }

    /**
     * links the TrackFragment instance variables with the ContentView elements
     *
     * @param view the current view
     */
    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tvDate);
        cbOne = view.findViewById(R.id.cbOne);
        cbTwo = view.findViewById(R.id.cbTwo);
        cbThree = view.findViewById(R.id.cbThree);
        cbFour = view.findViewById(R.id.cbFour);
        cbFive = view.findViewById(R.id.cbFive);
        cbSix = view.findViewById(R.id.cbSix);
        cbSeven = view.findViewById(R.id.cbSeven);
        cbEight = view.findViewById(R.id.cbEight);
        cbNine = view.findViewById(R.id.cbNine);
        cbTen = view.findViewById(R.id.cbTen);
        btnTrack = view.findViewById(R.id.btnTrack);
        radioGroup = view.findViewById(R.id.radioGroup);
        rbOne = view.findViewById(R.id.rbOne);
        rbTwo = view.findViewById(R.id.rbTwo);
        rbThree = view.findViewById(R.id.rbThree);
        rbFour = view.findViewById(R.id.rbFour);
        rbFive = view.findViewById(R.id.rbFive);
    }

    /**
     * Determines whether the user has previously tracked their mood and habits that day and, if so,
     * populates the screen with the information the user last tracked.
     */
    private void setOldHabits() {
        String dateStringInt = dateParts[0] + dateParts[1] + dateParts[2];
        try {
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
                    if (daysTracked.size() >= 1) {
                        displayOldHabits(daysTracked);
                    }
                }
            });
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks the radioButton of the mood the user last tracked (if they already tracked earlier that day)
     * Checks the checkboxes of any habits the user previously marked as completed earlier that day
     *
     * @param daysTracked the list of TrackDay Parse Objects corresponding to currentUser
     */
    private void displayOldHabits(List<TrackDay> daysTracked) {
        TrackDay firstTracked = daysTracked.get(0);
        int moodFirstDay = firstTracked.getMood();
        List<Integer> firstHabitsTracked = firstTracked.getTrackArray();
        RadioButton rbToCheck;
        if (moodFirstDay == 1) {
            rbToCheck = rbOne;
        } else if (moodFirstDay == 2) {
            rbToCheck = rbTwo;
        } else if (moodFirstDay == 3) {
            rbToCheck = rbThree;
        } else if (moodFirstDay == 4) {
            rbToCheck = rbFour;
        } else {
            rbToCheck = rbFive;
        }
        rbThree.setChecked(false);
        rbToCheck.setChecked(true);

        for (int i = 0; i < firstHabitsTracked.size(); i++) {
            if (firstHabitsTracked.get(i) == 1) {
                setChecked(i);
            }
        }
    }

    /**
     * Populates a checkbox to be checked if the user tracked that habit earlier
     *
     * @param i the integer index of the checkbox to handle
     */
    private void setChecked(int i) {
        CheckBox cbGoTo;
        //figures out which checkBox object we should be populating
        if (i == 0) {
            cbGoTo = cbOne;
        } else if (i == 1) {
            cbGoTo = cbTwo;
        } else if (i == 2) {
            cbGoTo = cbThree;
        } else if (i == 3) {
            cbGoTo = cbFour;
        } else if (i == 4) {
            cbGoTo = cbFive;
        } else if (i == 5) {
            cbGoTo = cbSix;
        } else if (i == 6) {
            cbGoTo = cbSeven;
        } else if (i == 7) {
            cbGoTo = cbEight;
        } else if (i == 8) {
            cbGoTo = cbNine;
        } else {
            cbGoTo = cbTen;
        }
        cbGoTo.setChecked(true);
    }

    /**
     * Saves the information the user tracked to the Parse database
     *
     * @param todayMood   the user's inputted mood
     * @param todayHabits an array of 1's and 0's representing whether the user completed each of their habits that day
     */
    private void saveTrack(int todayMood, List<Integer> todayHabits) {
        TrackDay trackDay = new TrackDay();
        trackDay.setMood(todayMood);
        trackDay.setTrackArray(todayHabits);
        trackDay.setParentUser(currentUser);
        String dateStringInt = dateParts[0] + dateParts[1] + dateParts[2];
        try {
            int dateInt = Integer.parseInt(dateStringInt);
            trackDay.setDateNumber(dateInt);
            removeDuplicates(currentUser, dateInt);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        trackDay.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                // navigates to the home fragment on successful submission of trackDay
                MainActivity.self.setTab(new HomeFragment(), R.id.itemHome);
            }
        });
    }

    /**
     * Clears any habit data the user tracked earlier that day so that the user can only track once per day.
     *
     * @param parentTracker the user
     * @param dateInt       the current date in the user's timezone
     */
    private void removeDuplicates(ParseUser parentTracker, int dateInt) {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, parentTracker);
        query.whereEqualTo(TrackDay.KEY_DATE_NUMBER, dateInt);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                for (int i = 0; i < daysTracked.size(); i++) {
                    TrackDay toRemove = daysTracked.get(i);
                    toRemove.deleteInBackground();
                }
            }
        });
    }

    /**
     * populates an array of binary values that indicate whether or not a certain habit was completed by a user
     *
     * @param numHabits the number of habits the user is tracking
     */
    private void populateTodayHabits(int numHabits) {
        for (int i = 0; i < numHabits; i++) {
            CheckBox cbGoTo;
            //figures out which checkBox object we should be populating
            if (i == 0) {
                cbGoTo = cbOne;
            } else if (i == 1) {
                cbGoTo = cbTwo;
            } else if (i == 2) {
                cbGoTo = cbThree;
            } else if (i == 3) {
                cbGoTo = cbFour;
            } else if (i == 4) {
                cbGoTo = cbFive;
            } else if (i == 5) {
                cbGoTo = cbSix;
            } else if (i == 6) {
                cbGoTo = cbSeven;
            } else if (i == 7) {
                cbGoTo = cbEight;
            } else if (i == 8) {
                cbGoTo = cbNine;
            } else {
                cbGoTo = cbTen;
            }
            if (cbGoTo.isChecked()) {
                todayHabits.add(1);
            } else {
                todayHabits.add(0);
            }
        }
    }

    /**
     * Populates the screen with the appropriate number of checkboxes and labels
     *
     * @param numHabits the number of habits the user is tracking
     */
    private void populateCheckboxes(int numHabits) {
        for (int i = 0; i < numHabits; i++) {
            CheckBox cbGoTo;
            //figures out which checkBox object we should be populating
            if (i == 0) {
                cbGoTo = cbOne;
            } else if (i == 1) {
                cbGoTo = cbTwo;
            } else if (i == 2) {
                cbGoTo = cbThree;
            } else if (i == 3) {
                cbGoTo = cbFour;
            } else if (i == 4) {
                cbGoTo = cbFive;
            } else if (i == 5) {
                cbGoTo = cbSix;
            } else if (i == 6) {
                cbGoTo = cbSeven;
            } else if (i == 7) {
                cbGoTo = cbEight;
            } else if (i == 8) {
                cbGoTo = cbNine;
            } else {
                cbGoTo = cbTen;
            }
            cbGoTo.setText(String.valueOf(habitsList.get(i)));
            cbGoTo.setVisibility(View.VISIBLE);
        }
    }
}