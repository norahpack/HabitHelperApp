package com.example.habithelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import okhttp3.Headers;

public class SetupAccount extends AppCompatActivity {

    public static final String GET_WEATHER_URL="https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";

    EditText etZip;
    EditText etName;
    CheckBox checkBoxWater;
    CheckBox checkBoxHealthy;
    CheckBox checkBoxScreens;
    CheckBox checkBoxMeditate;
    CheckBox checkBoxSleep;
    CheckBox checkBoxRead;
    CheckBox checkBoxOutside;
    CheckBox checkBoxWorkout;
    CheckBox checkBoxSkincare;
    CheckBox checkBoxTalk;
    CheckBox checkBoxCustomOne;
    CheckBox checkBoxCustomTwo;
    EditText etCustomOne;
    EditText etCustomTwo;
    ImageButton ibUpdate;
    ArrayList<String> habitsList = new ArrayList<String>();
    Boolean validZip;
    String name;
    String zipString;
    String customHabitOne;
    String customHabitTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        etZip = findViewById(R.id.etZip);
        etName = findViewById(R.id.etName);
        checkBoxWater = findViewById(R.id.checkBoxWater);
        checkBoxHealthy = findViewById(R.id.checkBoxHealthy);
        checkBoxScreens = findViewById(R.id.checkBoxScreens);
        checkBoxMeditate = findViewById(R.id.checkBoxMeditate);
        checkBoxSleep = findViewById(R.id.checkBoxSleep);
        checkBoxRead = findViewById(R.id.checkBoxRead);
        checkBoxOutside = findViewById(R.id.checkBoxOutside);
        checkBoxWorkout = findViewById(R.id.checkBoxWorkout);
        checkBoxSkincare = findViewById(R.id.checkBoxSkincare);
        checkBoxTalk = findViewById(R.id.checkBoxTalk);
        checkBoxCustomOne = findViewById(R.id.checkBoxCustomOne);
        checkBoxCustomTwo = findViewById(R.id.checkBoxCustomTwo);
        ibUpdate = findViewById(R.id.ibUpdate);
        etCustomOne = findViewById(R.id.etCustomOne);
        etCustomTwo = findViewById(R.id.etCustomTwo);

        ibUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                zipString = etZip.getText().toString();
                customHabitOne=etCustomOne.getText().toString();
                customHabitTwo=etCustomTwo.getText().toString();

                if(checkBoxCustomOne.isChecked()){
                    if (customHabitOne.isEmpty()) {
                        Toast.makeText(SetupAccount.this, "habit cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(checkBoxCustomTwo.isChecked()){
                    if (customHabitTwo.isEmpty()) {
                        Toast.makeText(SetupAccount.this, "habit cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (name.isEmpty()) {
                    Toast.makeText(SetupAccount.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zipString.isEmpty()) {
                    Toast.makeText(SetupAccount.this, "zipcode cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // initializing the list of habits the user wishes to track
                initializeHabitList();
                if (habitsList.size()<4) {
                    Toast.makeText(SetupAccount.this, "Add more habits", Toast.LENGTH_SHORT).show();
                    habitsList.clear();
                    return;
                }

                if (habitsList.size()>10) {
                    Toast.makeText(SetupAccount.this, "Select fewer habits", Toast.LENGTH_SHORT).show();
                    habitsList.clear();
                    return;
                }

                validZip=false;
                // checking whether or not the zipcode inputted is valid
                AsyncHttpClient client = new AsyncHttpClient();
                String api_request=GET_WEATHER_URL+zipString;
                client.get(api_request, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        validZip=true;
                        tryToInitialize(validZip);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        validZip=false;
                        tryToInitialize(validZip);
                        return;
                    }
                });




            }
        });
    }


    public void initializeHabitList () {
        if (checkBoxWater.isChecked()) {
            habitsList.add("Drink 8 cups of water");
        }
        if (checkBoxHealthy.isChecked()) {
            habitsList.add("Eat healthy");
        }
        if (checkBoxScreens.isChecked()) {
            habitsList.add("Limit screen time");
        }
        if (checkBoxMeditate.isChecked()) {
            habitsList.add("Meditate");
        }
        if (checkBoxSleep.isChecked()) {
            habitsList.add("Sleep 7 hours");
        }
        if (checkBoxRead.isChecked()) {
            habitsList.add("Read a book");
        }
        if (checkBoxOutside.isChecked()) {
            habitsList.add("Spend time outside");
        }
        if (checkBoxWorkout.isChecked()) {
            habitsList.add("Workout");
        }
        if (checkBoxSkincare.isChecked()) {
            habitsList.add("Skincare routine");
        }
        if (checkBoxTalk.isChecked()) {
            habitsList.add("Talk with loved ones");
        }
        if (checkBoxCustomOne.isChecked()){
            habitsList.add(customHabitOne);
        }
        if (checkBoxCustomTwo.isChecked()){
            habitsList.add(customHabitTwo);
        }
    }

    public void tryToInitialize(boolean zipValid){
        if(zipValid==true){
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("zipCode", zipString);
            currentUser.put("name", name);
            currentUser.put("habitsList", habitsList);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        return;
                    }
                    //goes back to the MainActivity class with the user logged in
                    Intent i = new Intent(SetupAccount.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        } else {
            Toast.makeText(SetupAccount.this, "Not a valid zipcode!", Toast.LENGTH_SHORT).show();
        }
    }
}