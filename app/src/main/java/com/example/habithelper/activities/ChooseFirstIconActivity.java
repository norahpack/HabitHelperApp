package com.example.habithelper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;
import com.example.habithelper.R;
import com.example.habithelper.adapters.IconAdapter;
import com.parse.ParseUser;
import java.util.Arrays;
import java.util.List;

public class ChooseFirstIconActivity extends AppCompatActivity {

    private RecyclerView rvIconList;
    private TextView tvCustomHabit;
    private ParseUser currentUser;
    private List<String> iconList;
    private IconAdapter adapter;
    private String firstIconName = "First Custom Habit";
    private String secondIconName = "Second Custom Habit";
    private boolean hasSecondCustom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_first_icon);
        rvIconList = findViewById(R.id.rvIconList);
        tvCustomHabit = findViewById(R.id.tvCustomHabit);

        getIntentExtras();

        currentUser = ParseUser.getCurrentUser();
        setupIconList();
        final GridLayoutManager layout = new GridLayoutManager(this, 3);
        adapter = new IconAdapter(this, iconList, hasSecondCustom, firstIconName, secondIconName);
        rvIconList.setAdapter(adapter);
        rvIconList.setLayoutManager(layout);
    }

    /**
     * Unwraps the intent and puts the extras passed in into variables
     */
    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("firstIconName") != null) {
            firstIconName = (String) bundle.getString("firstIconName");
            tvCustomHabit.setText(firstIconName);
        }
        if (bundle.getBoolean("hasSecondCustom")) {
            hasSecondCustom = (boolean) bundle.getBoolean("hasSecondCustom");
        }
        if (bundle.getString("secondIconName") != null) {
            secondIconName = (String) bundle.getString("secondIconName");
        }
    }

    /**
     * Initializes the list of drawable icon names
     */
    private void setupIconList() {
        iconList = Arrays.asList("choice_icon_one", "choice_icon_two", "choice_icon_three", "choice_icon_four", "choice_icon_five",
                "choice_icon_six", "choice_icon_seven", "choice_icon_eight", "choice_icon_nine", "choice_icon_ten", "choice_icon_eleven",
                "choice_icon_twelve", "choice_icon_thirteen", "choice_icon_fourteen", "choice_icon_fifteen", "choice_icon_sixteen",
                "choice_icon_seventeen", "choice_icon_eighteen", "choice_icon_nineteen", "choice_icon_twenty", "choice_icon_twentyone",
                "choice_icon_twentytwo", "choice_icon_twentythree", "choice_icon_twentyfour", "choice_icon_twentyfive", "choice_icon_twentysix",
                "choice_icon_twentyseven", "choice_icon_twentyeight", "choice_icon_twentynine", "choice_icon_thirty", "choice_icon_thirtyone", "starslarge",
                "healthylarge", "meditatelarge", "phonelarge", "readlarge", "skincarelarge", "sleeplarge", "sunlarge", "talklarge", "waterlarge", "workoutlarge");
    }
}