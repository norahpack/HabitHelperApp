package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
import com.example.habithelper.models.Habit;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import okhttp3.Headers;

public class AccountCreationActivity extends AppCompatActivity {

    public static final String GET_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";
    public static final int MIN_REQUIRED_HABITS_NUM = 4;
    public static final int MAX_ALLOWED_HABITS_NUM = 10;

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
    EditText etZip;
    EditText etName;
    EditText etCustomOne;
    EditText etCustomTwo;
    ImageButton ibUpdate;

    String name;
    String zipString;
    String customHabitOne;
    String customHabitTwo;
    ArrayList<String> habitsList = new ArrayList<String>();
    Boolean existsCustomOne = false;
    Boolean existsCustomTwo = false;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        // links the AccountCreationActivity instance variables with the ContentView elements
        initViews();

        ibUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserInput();
                checkZipCode();
            }
        });
    }

    /**
     * Checks whether or not the zipcode the user inputted is a valid zipcode
     * If it is not valid, tryToInitialize will not be called
     */
    private void checkZipCode() {
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = GET_WEATHER_URL + zipString;
        client.get(api_request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                tryToInitialize();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(AccountCreationActivity.this, "Not a valid zipcode!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    /**
     * Checks whether the name, custom habits, zipcode, and number of habits the user selected are valid
     */
    private void checkUserInput() {
        name = etName.getText().toString();
        zipString = etZip.getText().toString();
        customHabitOne = etCustomOne.getText().toString();
        customHabitTwo = etCustomTwo.getText().toString();

        if (checkBoxCustomOne.isChecked()) {
            if (customHabitOne.isEmpty()) {
                Toast.makeText(AccountCreationActivity.this, "habit cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (checkBoxCustomTwo.isChecked()) {
            if (customHabitTwo.isEmpty()) {
                Toast.makeText(AccountCreationActivity.this, "habit cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (name.isEmpty()) {
            Toast.makeText(AccountCreationActivity.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (zipString.isEmpty()) {
            Toast.makeText(AccountCreationActivity.this, "zipcode cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // initializing the list of habits the user wishes to track
        initializeHabitList();

        if (habitsList.size() < MIN_REQUIRED_HABITS_NUM) {
            Toast.makeText(AccountCreationActivity.this, "Add more habits", Toast.LENGTH_SHORT).show();
            habitsList.clear();
            return;
        }

        if (habitsList.size() > MAX_ALLOWED_HABITS_NUM) {
            Toast.makeText(AccountCreationActivity.this, "Select fewer habits", Toast.LENGTH_SHORT).show();
            habitsList.clear();
            return;
        }
    }

    /**
     * links the AccountCreationActivity instance variables with the ContentView elements
     */
    private void initViews() {
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
        etZip = findViewById(R.id.etZip);
        etName = findViewById(R.id.etName);
        etCustomOne = findViewById(R.id.etCustomOne);
        etCustomTwo = findViewById(R.id.etCustomTwo);
    }

    /**
     * Adds the habits the user has selected to the AccountCreationActivity object's habitsList
     */
    public void initializeHabitList() {
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
        if (checkBoxCustomOne.isChecked()) {
            habitsList.add(customHabitOne);
            existsCustomOne = true;
        }
        if (checkBoxCustomTwo.isChecked()) {
            habitsList.add(customHabitTwo);
            existsCustomTwo = true;
        }
    }

    /**
     * Attempts to save the user-inputted data to the ParseUser object representing the user
     *
     */
    public void tryToInitialize() {
        currentUser = ParseUser.getCurrentUser();
        currentUser.put("zipCode", zipString);
        currentUser.put("name", name);
        currentUser.put("habitsList", habitsList);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    return;
                }
                if (existsCustomOne) {
                    initializeParseHabits(customHabitOne);
                }
                if (existsCustomTwo) {
                    initializeParseHabits(customHabitTwo);
                }
                //goes back to the MainActivity class with the user logged in
                startActivity(new Intent(AccountCreationActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * Attempts to save the user-inputted habits (if they exist) to a new Parse Habit object
     */
    private void initializeParseHabits(String habitName) {
        Habit customHabit = new Habit();
        customHabit.setHabitName(habitName);
        customHabit.setCreator(currentUser);
        customHabit.setHabitImageKey("starslarge");
        customHabit.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(AccountCreationActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}