package com.example.habithelper.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
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

    CurvedText cText;
    ConstraintLayout clMain;
    TextView tvLocation;
    Button btnChangeZip;
    TextView tvDaysTracked;

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

        cText = view.findViewById(R.id.cText);
        clMain = view.findViewById(R.id.clMain);
        tvLocation = view.findViewById(R.id.tvLocation);
        btnChangeZip = view.findViewById(R.id.btnChangeZip);
        tvDaysTracked = view.findViewById(R.id.tvDaysTracked);

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

        btnChangeZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = MainActivity.self.getSupportFragmentManager();
                ZipcodeDialogFragment zipcodeDialogFragment = ZipcodeDialogFragment.newInstance("Change Zipcode");
                zipcodeDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                zipcodeDialogFragment.show(fm, "fragment_dialog_zipcode");
            }
        });
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
                tvDaysTracked.setText("You have been tracking for " + numDaysTracked + " days");
                return;
            }
        });
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
            tvLocation.setText("Your current location is " + locationName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}