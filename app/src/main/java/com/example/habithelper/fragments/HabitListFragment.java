package com.example.habithelper.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habithelper.R;
import com.example.habithelper.activities.MainActivity;
import com.example.habithelper.adapters.HabitAdapter;
import com.example.habithelper.models.Habit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;


public class HabitListFragment extends Fragment {

    public static final int NUM_HABITS_IN_ROW = 2;

    private RecyclerView rvHabits;
    ParseUser currentUser;
    List<Habit> habitsList;
    List<String> habitsNameList;
    private HabitAdapter adapter;
    private ProgressBar pbLoadingList;

    public HabitListFragment() {
        // Required empty public constructor to be able to initialize a fragment from MainActivity
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHabits = view.findViewById(R.id.rvHabits);
        pbLoadingList = view.findViewById(R.id.pbLoadingList);
        currentUser = ParseUser.getCurrentUser();
        habitsNameList = currentUser.getList("habitsList");
        final GridLayoutManager layout = new GridLayoutManager(getContext(), NUM_HABITS_IN_ROW);
        habitsList = new ArrayList<>();
        adapter = new HabitAdapter(MainActivity.self, getContext(), habitsList);
        rvHabits.setAdapter(adapter);
        rvHabits.setLayoutManager(layout);
        queryCustomHabits();
    }

    /**
     * Determines the custom Habit objects to pass to the HabitAdapter
     */
    private void queryCustomHabits() {
        ParseQuery<Habit> query = ParseQuery.getQuery(Habit.class);
        query.include(Habit.KEY_HABIT_NAME);
        query.include(Habit.KEY_HABIT_CREATOR);
        query.setLimit(2);
        query.whereEqualTo(Habit.KEY_HABIT_CREATOR, currentUser);
        query.whereContainedIn("habitName", habitsNameList);

        query.findInBackground(new FindCallback<Habit>() {
            @Override
            public void done(List<Habit> list, ParseException e) {
                if (e != null) {
                    return;
                }
                queryGenericHabits(list);
            }
        });
    }

    /**
     * Determines the generic Habit objects to pass to the HabitAdapter
     */
    private void queryGenericHabits(List<Habit> customHabits) {
        ParseQuery<Habit> query = ParseQuery.getQuery(Habit.class);
        query.include(Habit.KEY_HABIT_NAME);
        query.include(Habit.KEY_HABIT_CREATOR);
        query.whereEqualTo(Habit.KEY_HABIT_CREATOR, null);
        query.whereContainedIn("habitName", habitsNameList);

        query.findInBackground(new FindCallback<Habit>() {
            @Override
            public void done(List<Habit> list, ParseException e) {
                if (e != null) {
                    return;
                }
                // save received Habits to list and notify adapter of new data
                pbLoadingList.setVisibility(View.GONE);
                adapter.addAll(customHabits);
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }
}