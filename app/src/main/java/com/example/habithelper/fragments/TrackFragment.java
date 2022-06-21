package com.example.habithelper.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
import com.parse.ParseUser;

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

        super.onViewCreated(view, savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();

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
                    String[] dateParts = locationDate.split("-", 3);
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

    }
}