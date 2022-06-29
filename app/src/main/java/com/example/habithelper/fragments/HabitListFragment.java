package com.example.habithelper.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    RecyclerView rvHabits;
    ParseUser currentUser;
    List<Habit> habitsList;
    List<String> habitsNameList;
    HabitAdapter adapter;

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
        currentUser = ParseUser.getCurrentUser();
        habitsNameList = currentUser.getList("habitsList");
        final GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        habitsList = new ArrayList<>();
        adapter = new HabitAdapter(MainActivity.self, getContext(), habitsList);
        rvHabits.setAdapter(adapter);
        rvHabits.setLayoutManager(layout);
        queryHabitGrid();
    }

    /**
     * Determines the Habit objects to pass to the HabitAdapter
     */
    private void queryHabitGrid() {
        ParseQuery<Habit> query = ParseQuery.getQuery(Habit.class);
        query.include(Habit.KEY_HABIT_NAME);
        query.whereContainedIn("habitName", habitsNameList);

        // start an asynchronous call for habits
        query.findInBackground(new FindCallback<Habit>() {
            @Override
            public void done(List<Habit> list, ParseException e) {
                if (e != null) {
                    return;
                }
                // save received Habits to list and notify adapter of new data
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }
}