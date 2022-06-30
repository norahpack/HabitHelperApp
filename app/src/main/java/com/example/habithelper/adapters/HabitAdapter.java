package com.example.habithelper.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habithelper.R;
import com.example.habithelper.activities.HabitDetailsActivity;
import com.example.habithelper.models.Habit;
import org.parceler.Parcels;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private Activity mActivity;
    private Context context;
    private List<Habit> habitList;

    public HabitAdapter(Activity mActivity, Context context, List<Habit> habitList) {
        this.mActivity = mActivity;
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.bind(habit);
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public void addAll(List<Habit> list) {
        habitList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvHabit;
        private ImageView ivHabit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHabit = itemView.findViewById(R.id.ivHabit);
            tvHabit = itemView.findViewById(R.id.tvHabit);
            itemView.setOnClickListener(this);
        }

        public void bind(Habit habit) {
            tvHabit.setText(habit.getHabitName());
            if (habit.get("habitImageName") != null) {
                Resources resources = context.getResources();
                int resId = resources.getIdentifier(habit.getHabitImageKey(), "drawable", "com.example.habithelper");
                ivHabit.setBackground(AppCompatResources.getDrawable(context, resId));
            } else {
                ivHabit.setBackground(AppCompatResources.getDrawable(context, R.drawable.starslarge));
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Habit habit = habitList.get(position);
                Intent intent = new Intent(context, HabitDetailsActivity.class);

                Pair<View, String> p1 = Pair.create((View) ivHabit, "habitImage");
                Pair<View, String> p2 = Pair.create((View) tvHabit, "habitName");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, p1, p2);
                intent.putExtra(Habit.class.getSimpleName(), Parcels.wrap(habit));
                context.startActivity(intent, options.toBundle());
            }
        }
    }
}
