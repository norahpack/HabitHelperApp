package com.example.habithelper.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habithelper.R;
import com.example.habithelper.activities.ChooseIconActivity;
import com.example.habithelper.activities.MainActivity;
import com.example.habithelper.models.Habit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private ParseUser currentUser = ParseUser.getCurrentUser();
    private Context context;
    private List<String> iconList;
    private boolean hasSecondCustom;
    private String currentCustomIconName;
    private String secondIconName;

    public IconAdapter(Context context, List<String> iconList, boolean hasSecondCustom, String currentCustomIconName, String secondIconName) {
        this.context = context;
        this.iconList = iconList;
        this.hasSecondCustom = hasSecondCustom;
        this.currentCustomIconName = currentCustomIconName;
        this.secondIconName = secondIconName;
    }

    @NonNull
    @Override
    public IconAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false);
        return new IconAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconAdapter.ViewHolder holder, int position) {
        String icon = iconList.get(position);
        holder.bind(icon);
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String icon = iconList.get(position);
                putIconToParse(icon);
            }
        }

        public void bind(String icon) {
            Resources resources = context.getResources();
            int resId = resources.getIdentifier(icon, "drawable", "com.example.habithelper");
            ivIcon.setBackground(AppCompatResources.getDrawable(context, resId));
        }
    }

    /**
     * either goes to the secondIconActivity or to MainActivity if the user doesn't have a second activity
     */
    private void startNextActivity() {
        if (hasSecondCustom){
            Intent i = new Intent (context, ChooseIconActivity.class);
            i.putExtra("currentCustomIconName", secondIconName);
            i.putExtra("hasSecondCustom", false);
            context.startActivity(i);
        } else {
            // goes back to the MainActivity class with the user logged in
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

    /**
     * Pushes the User's selected custom icon to parse
     * @param icon the String name of the icon to push to parse
     */
    private void putIconToParse(String icon) {
        ParseQuery<Habit> query = ParseQuery.getQuery(Habit.class);
        query.include(Habit.KEY_HABIT_NAME);
        query.include(Habit.KEY_HABIT_CREATOR);
        query.whereEqualTo(Habit.KEY_HABIT_CREATOR, currentUser);
        query.whereEqualTo(Habit.KEY_HABIT_NAME, currentCustomIconName);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Habit>() {
            @Override
            public void done(List<Habit> habits, ParseException e) {
                if (e != null) {
                    return;
                }
                if(habits.size() == 1){
                    Habit currentHabit = habits.get(0);
                    currentHabit.setHabitImageKey(icon);
                    currentHabit.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                return;
                            }
                            startNextActivity();
                        }
                    });
                }
            }
        });
    }
}
