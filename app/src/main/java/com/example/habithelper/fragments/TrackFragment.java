package com.example.habithelper.fragments;

import static com.example.habithelper.activities.MainActivity.self;

import android.content.Intent;
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
import com.example.habithelper.R;
import com.example.habithelper.activities.AccountSetupActivity;
import com.example.habithelper.activities.MainActivity;
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
    public static final int MONTH_LONG_STREAK = 30;
    public static final int TWO_WEEK_STREAK = 14;
    public static final int ONE_WEEK_STREAK = 7;
    public static final double MAGNIFICENT_MOOD_MIN_MOOD = 3.0;
    public static final double SEVEN_DAYS_OF_SMILES_MIN_MOOD = 4.0;
    public static final int NUM_DAYS_ONE_WEEK = 7;
    public static final double NUM_DAYS_ONE_WEEK_DOUBLE = 7.0;

    public View view;
    private TextView tvDate;
    private TextView tvCurrentLocation;
    private CheckBox cbOne;
    private CheckBox cbTwo;
    private CheckBox cbThree;
    private CheckBox cbFour;
    private CheckBox cbFive;
    private CheckBox cbSix;
    private CheckBox cbSeven;
    private CheckBox cbEight;
    private CheckBox cbNine;
    private CheckBox cbTen;
    private Button btnTrack;
    private RadioGroup radioGroup;
    private RadioButton rbOne;
    private RadioButton rbTwo;
    private RadioButton rbThree;
    private RadioButton rbFour;
    private RadioButton rbFive;
    public List<Object> habitsList;
    public int numHabits;
    public int longestStreak;
    public ParseUser currentUser;
    public List<Integer> todayHabits = new ArrayList<>();
    public String[] dateParts;
    public List<CheckBox> checkBoxList;
    public boolean perfectDayEarlier;
    public boolean magnificentMoodEarlier;
    public boolean sevenDaysOfSmilesEarlier;
    public boolean noRedDaysEarlier;
    public boolean weeklongWarriorEarlier;
    public boolean twoWeekTriumphEarlier;
    public boolean monthLongMasterEarlier;
    public boolean earnedPerfectDayToday = false;
    public boolean earnedMagnificentMoodToday = false;
    public boolean earnedSevenDaysOfSmilesToday = false;
    public boolean earnedNoRedDaysToday = false;
    public boolean earnedWeeklongWarriorToday = false;
    public boolean earnedTwoWeekTriumphToday = false;
    public boolean earnedMonthLongMasterToday = false;
    public TrackDay trackDay;

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
    public void onViewCreated(@NonNull View myView, @Nullable Bundle savedInstanceState) {
        this.view = myView;
        super.onViewCreated(view, savedInstanceState);

        // links the TrackFragment instance variables with the ContentView elements
        initViews(view);
        initializeUserVariables();
        checkBoxList = Arrays.asList(cbOne, cbTwo, cbThree, cbFour, cbFive, cbSix, cbSeven, cbEight, cbNine, cbTen);
        populateCheckboxes(numHabits);
        zipCodeToDate();
        trackOnClick(view);
    }

    /**
     * Sets an onClickListener on the track button
     */
    private void trackOnClick(View view) {
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTrack.setClickable(false);
                int idChecked = radioGroup.getCheckedRadioButtonId();

                // ensures the user has selected their mood
                if(idChecked != -1){
                    RadioButton finalChoice = view.findViewById(idChecked);
                    int todayMood = (int) (finalChoice.getTag());
                    trackBadge();
                    populateTodayHabits(numHabits, todayMood, todayHabits);
                } else {
                    btnTrack.setClickable(true);
                    Toast.makeText(getContext(), "Select a mood", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Checks that the user has set a zip code and if so, uses that zipcode to make an api request
     */
    private void zipCodeToDate() {
        AsyncHttpClient client = new AsyncHttpClient();
        if (currentUser.getString("zipCode") != null){
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
                    btnTrack.setClickable(true);
                    return;
                }
            });
        } else {
            goToSetup();
        }
    }

    /**
     * Checks if the user has earned the "weeklong warrior" badge (longest streak for a habit is >= 7 days);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a weeklong warrior track from earlier that day.
     *
     * @param longestStreak the user's longest current habit streak
     */
    private void weeklongWarriorBadge(int longestStreak) {
        if(self.checkForBadge("badge_weeklong_warrior")){
            if (weeklongWarriorEarlier){
                // the user earned the weeklong warrior badge earlier, but is rewriting that data
                // remove their weeklong warrior badge
                if(!(longestStreak >= ONE_WEEK_STREAK)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_weeklong_warrior")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedWeeklongWarriorToday = true;
                }
            }
        } else {
            // the user has earned the weeklong warrior badge
            if(longestStreak >= ONE_WEEK_STREAK){
                currentUser.add("badgesEarned", "badge_weeklong_warrior");
                currentUser.saveInBackground();
                earnedWeeklongWarriorToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "month long master" badge (longest streak for a habit is >= 30 days);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a month long master track from earlier that day.
     *
     * @param longestStreak the user's longest current habit streak
     */
    private void monthLongMasterBadge(int longestStreak) {
        if(self.checkForBadge("badge_month_long_master")){
            if (monthLongMasterEarlier){
                // the user earned the month long master badge earlier, but is rewriting that data
                // remove their month long master badge
                if(!(longestStreak >= MONTH_LONG_STREAK)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_month_long_master")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedMonthLongMasterToday = true;
                }
            }
        } else {
            // the user has earned the month long master badge
            if(longestStreak >= MONTH_LONG_STREAK){
                currentUser.add("badgesEarned", "badge_month_long_master");
                currentUser.saveInBackground();
                earnedMonthLongMasterToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "two week triumph" badge (longest streak for a habit is >= 14 days);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a two week triumph track from earlier that day.
     *
     * @param longestStreak the user's longest current habit streak
     */
    private void twoWeekTriumphBadge(int longestStreak) {
        if(self.checkForBadge("badge_two_week_triumph")){
            if (twoWeekTriumphEarlier){
                // the user earned the two week triumph badge earlier, but is rewriting that data
                // remove their two week triumph badge
                if(!(longestStreak >= TWO_WEEK_STREAK)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_two_week_triumph")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedTwoWeekTriumphToday = true;
                }
            }
        } else {
            // the user has earned the two week triumph badge
            if(longestStreak >= TWO_WEEK_STREAK){
                currentUser.add("badgesEarned", "badge_two_week_triumph");
                currentUser.saveInBackground();
                earnedTwoWeekTriumphToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "magnificent mood" badge (average mood >=3 in the last seven days);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a magnificent mood track from earlier that day.
     *
     * @param weekAverage the user's average mood over the last week
     */
    private void magnificentMoodBadge(double weekAverage) {
        if(self.checkForBadge("badge_magnificent_mood")){
            if (magnificentMoodEarlier){
                // the user earned the magnificent mood badge earlier, but is rewriting that data
                // remove their magnificent mood badge
                if(!(weekAverage >= MAGNIFICENT_MOOD_MIN_MOOD)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_magnificent_mood")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedMagnificentMoodToday = true;
                }
            }
        } else {
            // the user has earned the magnificent mood badge
            if(weekAverage >= MAGNIFICENT_MOOD_MIN_MOOD){
                currentUser.add("badgesEarned", "badge_magnificent_mood");
                currentUser.saveInBackground();
                earnedMagnificentMoodToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "seven days of smiles" badge (average mood >=4 in the last seven days);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a seven days of smiles track from earlier that day.
     *
     * @param weekAverage the user's average mood over the last week
     */
    private void sevenDaysOfSmilesBadge(double weekAverage){
        if(self.checkForBadge("badge_seven_days_of_smiles")){
            if (sevenDaysOfSmilesEarlier){
                // the user earned the seven days of smiles badge earlier, but is rewriting that data
                // remove their seven days of smiles badge
                if(!(weekAverage >= SEVEN_DAYS_OF_SMILES_MIN_MOOD)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_seven_days_of_smiles")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedSevenDaysOfSmilesToday = true;
                }
            }
        } else {
            // the user has earned the seven days of smiles badge
            if(weekAverage >= SEVEN_DAYS_OF_SMILES_MIN_MOOD){
                currentUser.add("badgesEarned", "badge_seven_days_of_smiles");
                currentUser.saveInBackground();
                earnedSevenDaysOfSmilesToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "no red days" badge (no "1" or "2" moods for a week);
     * if not, gives them the badge if they completed the criteria to earn that badge.
     * Removes the badge if they are overwriting a no red days track from earlier that day.
     *
     * @param noRed whether or not the user has achieved a no-red-day week
     */
    private void noRedDaysBadge(boolean noRed){
        if(self.checkForBadge("badge_no_red_days")){
            if (noRedDaysEarlier){
                // the user earned the no red days badge earlier, but is rewriting that data
                // remove their no red days badge
                if(!noRed){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_no_red_days")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedNoRedDaysToday = true;
                }
            }
        } else {
            // the user has earned the no red days badge
            if(noRed){
                currentUser.add("badgesEarned", "badge_no_red_days");
                currentUser.saveInBackground();
                earnedNoRedDaysToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "perfect day" badge (completing all habits one day);
     * if not, gives them the badge if they tracked all their habits that day.
     * Removes the badge if they are overwriting a perfect day track from earlier that day.
     */
    private void perfectDayBadge() {
        if(self.checkForBadge("badge_perfect_day")){
            if (perfectDayEarlier){
                // the user tracked their first perfect day earlier, but is rewriting that data with non-perfect data
                // remove their perfectDay badge
                if(todayHabits.contains(0)){
                    List<String> badgesEarned = currentUser.getList("badgesEarned");
                    currentUser.remove("badgesEarned");
                    currentUser.saveInBackground();
                    for (String badge : badgesEarned){
                        if(! badge.equals("badge_perfect_day")){
                            currentUser.add("badgesEarned", badge);
                            currentUser.saveInBackground();
                        }
                    }
                } else {
                    earnedPerfectDayToday = true;
                }
            }
        } else {
            // the user had a perfect day
            if(!todayHabits.contains(0)){
                currentUser.add("badgesEarned", "badge_perfect_day");
                currentUser.saveInBackground();
                earnedPerfectDayToday = true;
            }
        }
    }

    /**
     * Checks if the user has earned the "You did it" badge; if not, gives them the badge
     * once they track their habits for the first time.
     */
    private void trackBadge() {
        if(!self.checkForBadge("badge_you_did_it")){
            currentUser.add("badgesEarned", "badge_you_did_it");
            currentUser.saveInBackground();
        }
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
            tvDate.setText("Today is " + month + " " + day + ", " + year);
            tvCurrentLocation.setText("in " + locationName);
        } catch (JSONException e) {
            btnTrack.setClickable(true);
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
        if (date == 01 || date == 21 || date == 31) {
            return (datePart + "st");
        } else if (date == 02 || date == 22) {
            return (datePart + "nd");
        } else if (date == 03 || date == 23) {
            return (datePart + "rd");
        } else {
            return (datePart + "th");
        }
    }

    /**
     * Initializes user variables using information from the currentUser Parse Object
     */
    private void initializeUserVariables() {
        rbOne.setTag(1);
        rbTwo.setTag(2);
        rbThree.setTag(3);
        rbFour.setTag(4);
        rbFive.setTag(5);
        currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");
        if (habitsList == null || habitsList.size() < 4 || habitsList.size() > 10){
            goToSetup();
        } else {
            numHabits = habitsList.size();
        }
    }

    /**
     * Takes the user back to the account setup page because their account was not set up properly
     */
    private void goToSetup() {
        startActivity(new Intent(getContext(), AccountSetupActivity.class));
        getActivity().finish();
    }

    /**
     * links the TrackFragment instance variables with the ContentView elements
     *
     * @param view the current view
     */
    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tvDate);
        tvCurrentLocation = view.findViewById(R.id.tvCurrentLocation);
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
        rbToCheck.setChecked(true);

        perfectDayEarlier = firstTracked.getEarnedPerfectDay();
        magnificentMoodEarlier = firstTracked.getEarnedMagnificentMood();
        sevenDaysOfSmilesEarlier = firstTracked.getEarnedSevenDaysOfSmiles();
        noRedDaysEarlier = firstTracked.getEarnedNoRedDays();
        weeklongWarriorEarlier = firstTracked.getEarnedWeeklongWarrior();
        twoWeekTriumphEarlier = firstTracked.getEarnedTwoWeekTriumph();
        monthLongMasterEarlier = firstTracked.getEarnedMonthLongMaster();

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
        // figures out which checkBox object we should be populating
        if(i >= 0 && i < checkBoxList.size()){
            CheckBox cbGoTo = checkBoxList.get(i);
            cbGoTo.setChecked(true);
        }
    }

    /**
     * Saves the information the user tracked to the Parse database
     *
     * @param todayMood   the user's inputted mood
     * @param todayHabits an array of 1's and 0's representing whether the user completed each of their habits that day
     */
    private void saveTrack(int todayMood, List<Integer> todayHabits) {
        trackDay = new TrackDay();
        trackDay.setMood(todayMood);
        trackDay.setTrackArray(todayHabits);
        trackDay.setParentUser(currentUser);
        String dateStringInt = dateParts[0] + dateParts[1] + dateParts[2];
        try {
            int dateInt = Integer.parseInt(dateStringInt);
            trackDay.setDateNumber(dateInt);
        } catch (NumberFormatException ex) {
            btnTrack.setClickable(true);
            ex.printStackTrace();
        }
        trackDay.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    btnTrack.setClickable(true);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                try {
                    int dateInt = Integer.parseInt(dateStringInt);
                    trackDay.setDateNumber(dateInt);
                    removeDuplicates(currentUser, dateInt, todayMood);
                } catch (NumberFormatException ex) {
                    btnTrack.setClickable(true);
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Clears any habit data the user tracked earlier that day so that the user can only track once per day.
     *
     * @param parentTracker the user
     * @param dateInt       the current date in the user's timezone
     */
    private void removeDuplicates(ParseUser parentTracker, int dateInt, int todayMood) {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_UPDATED_AT);
        query.orderByDescending(TrackDay.KEY_UPDATED_AT);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, parentTracker);
        query.whereEqualTo(TrackDay.KEY_DATE_NUMBER, dateInt);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                if(daysTracked.size() > 1){
                    for (int i = 1; i < daysTracked.size(); i++) {
                        TrackDay toRemove = daysTracked.get(i);
                        toRemove.deleteInBackground();
                    }
                }
                queryUserData(todayMood, todayHabits);
            }
        });
    }

    /**
     * populates an array of binary values that indicate whether or not a certain habit was completed by a user
     *
     * @param numHabits the number of habits the user is tracking
     */
    private void populateTodayHabits(int numHabits, int todayMood, List<Integer> todayHabits) {
        for (int i = 0; i < numHabits; i++) {
            // figures out which checkBox object we should be populating
            CheckBox cbGoTo = checkBoxList.get(i);
            if (cbGoTo.isChecked()) {
                todayHabits.add(1);
            } else {
                todayHabits.add(0);
            }
        }
        perfectDayBadge();
        saveTrack(todayMood, todayHabits);
    }

    /**
     * Populates the screen with the appropriate number of checkboxes and labels
     *
     * @param numHabits the number of habits the user is tracking
     */
    private void populateCheckboxes(int numHabits) {
        for (int i = 0; i < numHabits; i++) {
            // figures out which checkBox object we should be populating
            CheckBox cbGoTo = checkBoxList.get(i);
            cbGoTo.setText(String.valueOf(habitsList.get(i)));
            cbGoTo.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets the last seven days the user has tracked
     */
    private void queryUserData(int todayMood, List<Integer> todayHabits) {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.orderByDescending("dateNumber");
        query.setLimit(NUM_DAYS_ONE_WEEK);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                if (daysTracked.size() == NUM_DAYS_ONE_WEEK) {
                    int moodSum = 0;
                    boolean noRed = true;
                    for (TrackDay day : daysTracked){
                        if (day.getMood() == 1 || day.getMood() == 2){
                            noRed = false;
                        }
                        moodSum += day.getMood();
                    }
                    getLongestStreak(moodSum, noRed);
                }
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }

    /**
     * Determines the User's current longest habit completion streak
     * @param moodSum the sum of the user's mood for the last seven days
     * @param noRed whether the user has had "no red days" the last seven days
     * @return the length, in days, of the user's longest streak
     */
    private void getLongestStreak(int moodSum, boolean noRed) {
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
                for (int i = 0; i < daysTracked.get(i).getTrackArray().size(); i++)
                {
                    int singleHabitStreak = getSingleHabitStreak(i, daysTracked);
                    if (singleHabitStreak > longestStreak){
                        longestStreak = singleHabitStreak;
                    }
                }
                setBadges(longestStreak, moodSum, noRed);
                return;
            }
        });
        return;
    }

    /**
     * Runs the methods to determine if the user has earned each of the badges
     * @param longestStreak the length, in days, of the user's current longest habit streak
     * @param moodSum the sum of the user's mood for the last seven days
     * @param noRed whether the user has had "no red days" the last seven days
     */
    private void setBadges(int longestStreak, int moodSum, boolean noRed) {
        magnificentMoodBadge(moodSum/NUM_DAYS_ONE_WEEK_DOUBLE);
        sevenDaysOfSmilesBadge(moodSum/NUM_DAYS_ONE_WEEK_DOUBLE);
        noRedDaysBadge(noRed);
        weeklongWarriorBadge(longestStreak);
        twoWeekTriumphBadge(longestStreak);
        monthLongMasterBadge(longestStreak);
        trackDay.setEarnedPerfectDay(earnedPerfectDayToday);
        trackDay.setEarnedMagnificentMood(earnedMagnificentMoodToday);
        trackDay.setEarnedSevenDaysOfSmiles(earnedSevenDaysOfSmilesToday);
        trackDay.setEarnedNoRedDays(earnedNoRedDaysToday);
        trackDay.setEarnedWeeklongWarrior(earnedWeeklongWarriorToday);
        trackDay.setEarnedTwoWeekTriumph(earnedTwoWeekTriumphToday);
        trackDay.setEarnedMonthLongMaster(earnedMonthLongMasterToday);
        trackDay.saveInBackground();
    }

    /**
     * Gets the User's current habit streak for the habit at index i
     * @param habitIndex the index of the habit we are analyzing
     * @return the user's current streak (in days) for completing that habit
     */
    private int getSingleHabitStreak(int habitIndex, List<TrackDay> daysTracked) {
        boolean streakLost = false;
        int habitStreak = 0;
        for (int i = 0; i < daysTracked.size() && !streakLost; i++) {
            if (daysTracked.get(i).getTrackArray().get(habitIndex) == 1) {
                // the user completed the habit on the specified day
                habitStreak += 1;
            } else {
                streakLost = true;
            }
        }
        return habitStreak;
    }
}