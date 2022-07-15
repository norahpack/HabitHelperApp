package com.example.habithelper.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
import com.example.habithelper.activities.BadgesActivity;
import com.example.habithelper.activities.LoginActivity;
import com.example.habithelper.activities.MainActivity;
import com.example.habithelper.models.TrackDay;
import com.example.habithelper.views.CurvedText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import okhttp3.Headers;


public class ProfileFragment extends Fragment {

    private Button btnChangeZip;
    private Button btnViewBadges;
    private Button btnLogout;
    private CurvedText curvedTextBadge;
    private ConstraintLayout clMain;
    private ImageView ivProfileLevel;
    private TextView tvLocation;
    private TextView tvDaysTracked;
    private TextView tvLevel;
    private ProgressBar pbLoadingNumDays;
    private ProgressBar pbLoadingLocation;

    private ParseUser currentUser;
    private int numDaysTracked = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        currentUser = ParseUser.getCurrentUser();
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = TrackFragment.GET_WEATHER_URL + currentUser.getString("zipCode");
        client.get(api_request, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                getCurrentDate(jsonObject);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                return;
            }
        });

        getDaysTracked();

        setButtonOnClickListeners();
    }

    /**
     * Sets new OnClickListeners on the changeZip, logout, and viewBadges buttons.
     */
    private void setButtonOnClickListeners() {
        btnChangeZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = MainActivity.self.getSupportFragmentManager();
                ZipcodeDialogFragment zipcodeDialogFragment = ZipcodeDialogFragment.newInstance("Change Zipcode");
                zipcodeDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                zipcodeDialogFragment.show(fm, "fragment_dialog_zipcode");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        btnViewBadges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BadgesActivity.class));
            }
        });
    }

    /**
     * links the ProfileFragment instance variables with the ContentView elements
     *
     * @param view the current view
     */
    private void initViews(View view) {
        btnChangeZip = view.findViewById(R.id.btnChangeZip);
        btnViewBadges = view.findViewById(R.id.btnViewBadges);
        btnLogout = view.findViewById(R.id.btnLogout);
        curvedTextBadge = view.findViewById(R.id.curvedTextBadge);
        clMain = view.findViewById(R.id.clMain);
        ivProfileLevel = view.findViewById(R.id.ivProfileLevel);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDaysTracked = view.findViewById(R.id.tvDaysTracked);
        tvLevel = view.findViewById(R.id.tvLevel);
        pbLoadingLocation = view.findViewById(R.id.pbLoadingLocation);
        pbLoadingNumDays = view.findViewById(R.id.pbLoadingNumDays);
    }

    /**
     * Determines the number of days the user has been tracking their habits for
     */
    private void getDaysTracked() {
        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.addDescendingOrder(TrackDay.KEY_DATE_NUMBER);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                numDaysTracked = daysTracked.size();
                tvDaysTracked.setVisibility(View.INVISIBLE);
                tvDaysTracked.setText("You have been tracking for " + numDaysTracked + " days");
                setLevel();
                return;
            }
        });
    }

    /**
     * Tells the user how many days more they have to track to reach the next level
     */
    private void setLevel() {
        String nextLevel;
        int daysLeft;
        if(numDaysTracked > 100){
            tvLevel.setText("You have reached the maximum level");
            return;
        } else if (numDaysTracked > 50){
            daysLeft = 100 - numDaysTracked;
            nextLevel = "5";
        } else if (numDaysTracked > 25){
            daysLeft = 50 - numDaysTracked;
            nextLevel = "4";
        } else if (numDaysTracked > 10){
            daysLeft = 25 - numDaysTracked;
            nextLevel = "3";
        } else {
            daysLeft = 10 - numDaysTracked;
            nextLevel = "2";
        }
        setBackground(numDaysTracked);
        pbLoadingNumDays.setVisibility(View.GONE);
        tvDaysTracked.setVisibility(View.VISIBLE);
        tvLevel.setText(String.valueOf(daysLeft)+" more days until you reach level "+nextLevel);
    }

    /**
     * determines the user's current location
     *
     * @param jsonObject the response from the API call to weatherAPI
     */
    private void getCurrentDate(JSONObject jsonObject) {
        try {
            JSONObject location = jsonObject.getJSONObject("location");
            String locationName = location.getString("name");
            pbLoadingLocation.setVisibility(View.GONE);
            tvLocation.setText("Your current location is " + locationName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the level the user is at and sets the profile badge and level accordingly
     * @param numDaysTracked the number of days the user has tracked for
     */
    private void setBackground(int numDaysTracked) {
        if (getContext() != null){
            if(numDaysTracked > 100){
                ivProfileLevel.setBackground(AppCompatResources.getDrawable(getContext(),R.drawable.level_five));
            } else if (numDaysTracked > 50){
                ivProfileLevel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.level_four));
            } else if (numDaysTracked > 25){
                ivProfileLevel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.level_three));
            } else if (numDaysTracked > 10){
                ivProfileLevel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.level_two));
            } else {
                ivProfileLevel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.level_one));
            }
        }
    }
}