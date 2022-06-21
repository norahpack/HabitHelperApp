package com.example.habithelper.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.example.habithelper.MainActivity;
import com.example.habithelper.R;
import com.example.habithelper.TrackDay;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;


public class TrackFragment extends Fragment {

    public static final String GET_WEATHER_URL="https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";
    public static final String TAG = "TrackFragment";

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
    public List<Double> todayHabits = new ArrayList<>();
    public String[] dateParts;


    public TrackFragment() {
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
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        tvDate=view.findViewById(R.id.tvDate);
        cbOne=view.findViewById(R.id.cbOne);
        cbTwo=view.findViewById(R.id.cbTwo);
        cbThree=view.findViewById(R.id.cbThree);
        cbFour=view.findViewById(R.id.cbFour);
        cbFive=view.findViewById(R.id.cbFive);
        cbSix=view.findViewById(R.id.cbSix);
        cbSeven=view.findViewById(R.id.cbSeven);
        cbEight=view.findViewById(R.id.cbEight);
        cbNine=view.findViewById(R.id.cbNine);
        cbTen=view.findViewById(R.id.cbTen);
        btnTrack=view.findViewById(R.id.btnTrack);
        radioGroup=view.findViewById(R.id.radioGroup);
        rbOne=view.findViewById(R.id.rbOne);
        rbTwo=view.findViewById(R.id.rbTwo);
        rbThree=view.findViewById(R.id.rbThree);
        rbFour=view.findViewById(R.id.rbFour);
        rbFive=view.findViewById(R.id.rbFive);

        rbOne.setTag(1);
        rbTwo.setTag(2);
        rbThree.setTag(3);
        rbFour.setTag(4);
        rbFive.setTag(5);


        super.onViewCreated(view, savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        habitsList = currentUser.getList("habitsList");
        int numHabits=habitsList.size();

        populateCheckboxes(numHabits);

        AsyncHttpClient client = new AsyncHttpClient();
        String api_request=GET_WEATHER_URL+currentUser.getString("zipCode");
        client.get(api_request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONObject location = jsonObject.getJSONObject("location");
                    String locationTime = location.getString("localtime");
                    String locationDate=locationTime.split(" ")[0];
                    String locationName = location.getString("name");
                    dateParts = locationDate.split("-", 3);
                    String month = getMonth(dateParts[1]);
                    String day = getDay(dateParts[2]);
                    String year = dateParts[0];
                    tvDate.setText("Today is "+month+" "+day+", "+year+" in "+locationName);
                    System.out.println(locationTime);
                    System.out.println(locationDate);
                    System.out.println(locationName);
                } catch (JSONException e){
                    Log.e(TAG, "hit json exception", e);
                    e.printStackTrace();
                }
            }

            private String getMonth(String datePart) {
                int monthNum = Integer.valueOf(datePart);
                List<String> monthsList = new ArrayList<>(Arrays.asList("None", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
                return monthsList.get(monthNum);
            }

            private String getDay(String datePart) {
                int date = Integer.valueOf(datePart);
                if(date==1 || date==21 || date ==31){
                    return (datePart+"st");
                } else if (date==2 || date == 22){
                    return (datePart+"nd");
                } else if (date==3 || date ==23){
                    return (datePart+"rd");
                } else {
                    return (datePart+"th");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                return;
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int idChecked = radioGroup.getCheckedRadioButtonId();
                RadioButton finalChoice = view.findViewById(idChecked);
                int todayMood=(int)(finalChoice.getTag());

                populateTodayHabits(numHabits);
                System.out.println(todayHabits);
                System.out.println(todayMood);
                saveTrack(todayMood, todayHabits);
            }

        });

    }

    private void saveTrack(int todayMood, List<Double> todayHabits) {
        TrackDay trackDay = new TrackDay();
        trackDay.setMood(todayMood);
        trackDay.setTrackArray(todayHabits);
        trackDay.setParentUser(ParseUser.getCurrentUser());
        String dateStringInt = dateParts[0]+dateParts[1]+dateParts[2];
        try{
            int dateInt = Integer.parseInt(dateStringInt);
            trackDay.setDateNumber(dateInt);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }

        trackDay.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    System.out.println(e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }

                //allows us to navigate in between fragments
                MainActivity.self.setTab(new HomeFragment(), R.id.itemHome);
            }
        });

    }

    private void populateTodayHabits(int numHabits){
        for(int i = 0; i<numHabits; i++){
            CheckBox cbGoTo;
            //figures out which checkBox object we should be populating
            if(i==0){
                cbGoTo=cbOne;
            } else if (i==1){
                cbGoTo=cbTwo;
            } else if (i==2){
                cbGoTo=cbThree;
            } else if (i==3){
                cbGoTo=cbFour;
            } else if (i==4){
                cbGoTo=cbFive;
            } else if (i==5){
                cbGoTo=cbSix;
            } else if (i==6){
                cbGoTo=cbSeven;
            } else if (i==7){
                cbGoTo=cbEight;
            } else if (i==8){
                cbGoTo=cbNine;
            } else {
                cbGoTo=cbTen;
            }
            if(cbGoTo.isChecked()){
                todayHabits.add(1.0);
            } else {
                todayHabits.add(0.0);
            }
        }

    }

    private void populateCheckboxes(int numHabits) {
        for(int i = 0; i<numHabits; i++){
            CheckBox cbGoTo;
            //figures out which checkBox object we should be populating
            if(i==0){
                cbGoTo=cbOne;
            } else if (i==1){
                cbGoTo=cbTwo;
            } else if (i==2){
                cbGoTo=cbThree;
            } else if (i==3){
                cbGoTo=cbFour;
            } else if (i==4){
                cbGoTo=cbFive;
            } else if (i==5){
                cbGoTo=cbSix;
            } else if (i==6){
                cbGoTo=cbSeven;
            } else if (i==7){
                cbGoTo=cbEight;
            } else if (i==8){
                cbGoTo=cbNine;
            } else {
                cbGoTo=cbTen;
            }
            cbGoTo.setText(String.valueOf(habitsList.get(i)));
            cbGoTo.setVisibility(View.VISIBLE);
        }
    }
}