package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class AccountSetupActivity extends AppCompatActivity {

    public static final String GET_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key=e8d92dcba9404609b24175200221606&q=";
    public static final int MIN_REQUIRED_HABITS_NUM = 4;
    public static final int MAX_ALLOWED_HABITS_NUM = 10;

    private CheckBox checkBoxWater;
    private CheckBox checkBoxHealthy;
    private CheckBox checkBoxScreens;
    private CheckBox checkBoxMeditate;
    private CheckBox checkBoxSleep;
    private CheckBox checkBoxRead;
    private CheckBox checkBoxOutside;
    private CheckBox checkBoxWorkout;
    private CheckBox checkBoxSkincare;
    private CheckBox checkBoxTalk;
    private CheckBox checkBoxCustomOne;
    private CheckBox checkBoxCustomTwo;
    private EditText etZip;
    private EditText etName;
    private EditText etCustomOne;
    private EditText etCustomTwo;
    private Button btnUpdate;
    private Button btnShowCollapseList;
    private Group groupShowCollapseHabits;

    String name;
    String zipString;
    String customHabitOne;
    String customHabitTwo;
    ArrayList<String> habitsList = new ArrayList<String>();
    boolean existsCustomOne = false;
    boolean existsCustomTwo = false;
    boolean isFullListShown = false;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        initViews();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate.setClickable(false);
                checkUserInput();
            }
        });

        btnShowCollapseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCollapseList();
            }
        });
    }

    /**
     * Expands or collapses the full list of habits.
     */
    private void showCollapseList() {
        if (isFullListShown){
            groupShowCollapseHabits.setVisibility(View.GONE);
            btnShowCollapseList.setText("Show full list of habits");
            isFullListShown = false;
        } else {
            groupShowCollapseHabits.setVisibility(View.VISIBLE);
            btnShowCollapseList.setText("Collapse full list of habits");
            isFullListShown = true;
        }
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
                btnUpdate.setClickable(true);
                Toast.makeText(AccountSetupActivity.this, "Not a valid zipcode", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    /**
     * Checks whether the name, custom habits, zipcode, and number of habits the user selected are valid
     */
    private void checkUserInput() {
        habitsList.clear();
        name = etName.getText().toString();
        zipString = etZip.getText().toString();
        customHabitOne = etCustomOne.getText().toString();
        customHabitTwo = etCustomTwo.getText().toString();
        initializeHabitList();

        if (checkBoxCustomOne.isChecked()) {
            if (customHabitOne.isEmpty()) {
                btnUpdate.setClickable(true);
                Toast.makeText(AccountSetupActivity.this, "Habit cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (checkBoxCustomTwo.isChecked()) {
            if (customHabitTwo.isEmpty()) {
                btnUpdate.setClickable(true);
                Toast.makeText(AccountSetupActivity.this, "Habit cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (name.isEmpty()) {
            btnUpdate.setClickable(true);
            Toast.makeText(AccountSetupActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (zipString.isEmpty()) {
            btnUpdate.setClickable(true);
            Toast.makeText(AccountSetupActivity.this, "Zipcode cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (habitsList.size() < MIN_REQUIRED_HABITS_NUM) {
            btnUpdate.setClickable(true);
            Toast.makeText(AccountSetupActivity.this, "Add more habits", Toast.LENGTH_SHORT).show();
            return;
        } else if (habitsList.size() > MAX_ALLOWED_HABITS_NUM) {
            btnUpdate.setClickable(true);
            Toast.makeText(AccountSetupActivity.this, "Select fewer habits", Toast.LENGTH_SHORT).show();
            return;
        } else {
            checkZipCode();
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
        etZip = findViewById(R.id.etZip);
        etName = findViewById(R.id.etName);
        etCustomOne = findViewById(R.id.etCustomOne);
        etCustomTwo = findViewById(R.id.etCustomTwo);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnShowCollapseList = findViewById(R.id.btnShowCollapseList);
        groupShowCollapseHabits = findViewById(R.id.groupShowCollapseHabits);
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
     */
    public void tryToInitialize() {
        currentUser = ParseUser.getCurrentUser();
        currentUser.put("zipCode", zipString);
        currentUser.put("name", name);
        if (! currentUser.getList("badgesEarned").contains("badge_warmest_welcome")){
            currentUser.add("badgesEarned", "badge_warmest_welcomes");
        }
        currentUser.put("habitsList", habitsList);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    btnUpdate.setClickable(true);
                    return;
                }
                if (existsCustomOne) {
                    initializeParseHabits(customHabitOne);
                } if (existsCustomTwo) {
                    initializeParseHabits(customHabitTwo);
                }
                goNextActivity();
            }
        });
    }

    /**
     * Either takes the user to the ChooseFirstIconActivity, ChooseSecondIconActivity, or home fragment
     * depending on whether or not they chose to create custom habits
     */
    private void goNextActivity() {
        if (existsCustomOne ^ existsCustomTwo){
            Intent i = new Intent(AccountSetupActivity.this, ChooseIconActivity.class);
            i.putExtra("hasSecondCustom", false);
            if (existsCustomOne){
                i.putExtra("currentCustomIconName", customHabitOne);
            } else {
                i.putExtra("currentCustomIconName", customHabitTwo);
            }
            startActivity(i);
            finish();
        } else if (existsCustomOne && existsCustomTwo){
            Intent i = new Intent(AccountSetupActivity.this, ChooseIconActivity.class);
            i.putExtra("currentCustomIconName", customHabitOne);
            i.putExtra("hasSecondCustom", true);
            i.putExtra("secondIconName", customHabitTwo);
            startActivity(i);
            finish();
        } else {
            // goes back to the MainActivity class with the user logged in
            startActivity(new Intent(AccountSetupActivity.this, MainActivity.class));
            finish();
        }
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
                if (e != null) {
                    btnUpdate.setClickable(true);
                    Toast.makeText(AccountSetupActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}