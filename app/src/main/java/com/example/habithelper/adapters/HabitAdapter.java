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
import com.bumptech.glide.Glide;
import com.example.habithelper.R;
import com.example.habithelper.activities.HabitDetailsActivity;
import com.example.habithelper.models.Habit;
import com.parse.ParseFile;
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
            if(habit.getHabitName().length() >= 30){
                tvHabit.setTextSize(23);
            } else if (habit.getHabitName().length() >= 20){
                tvHabit.setTextSize(26);
            } else {
                tvHabit.setTextSize(30);
            }
            tvHabit.setText(habit.getHabitName());
            ParseFile image = habit.getHabitCustomIcon();
            if (image != null){
                Glide.with(context).load(image.getUrl()).into(ivHabit);
            } else {
                Resources resources = context.getResources();
                if (habit.get("habitImageName") != null) {
                    int resId = resources.getIdentifier(habit.getHabitImageKey(), "drawable", "com.example.habithelper");
                    Glide.with(context).load(AppCompatResources.getDrawable(context, resId)).into(ivHabit);
                } else {
                    Glide.with(context).load(R.drawable.starslarge).into(ivHabit);
                }
                ivHabit.setColorFilter(resources.getColor(R.color.sienna)); // Add tint color
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Habit habit = habitList.get(position);
                Intent intent = new Intent(context, HabitDetailsActivity.class);

                Pair<View, String> p1 = Pair.create(ivHabit, "habitImage");
                Pair<View, String> p2 = Pair.create(tvHabit, "habitName");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, p1, p2);
                intent.putExtra(Habit.class.getSimpleName(), Parcels.wrap(habit));
                context.startActivity(intent, options.toBundle());
            }
        }
    }
}
