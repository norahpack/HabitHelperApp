package com.example.habithelper.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habithelper.R;
import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {

    private Context context;
    private List<String> badgeList;

    public BadgesAdapter(Context context, List<String> badgeList) {
        this.context = context;
        this.badgeList = badgeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new BadgesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String badgeImage = badgeList.get(position);
        holder.bind(badgeImage);
    }

    @Override
    public int getItemCount() {
        return badgeList.size();
    }

    public void clear(){
        badgeList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<String> stringList) {
        for (String s : stringList){
            badgeList.add(s);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBadge;
        private TextView tvBadgeDescription;
        private ImageView ivBadge;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvBadge = view.findViewById(R.id.tvBadge);
            tvBadgeDescription = view.findViewById(R.id.tvBadgeDescription);
            ivBadge = view.findViewById(R.id.ivBadge);
        }

        public void bind(String badgeImage) {
            setText(badgeImage);
            Resources resources = context.getResources();
            int resId = resources.getIdentifier(badgeImage, "drawable", "com.example.habithelper");
            ivBadge.setBackgroundResource(resId);
        }

        private void setText(String badgeImage) {
            if (badgeImage.equals("badge_magnificent_mood")) {
                tvBadge.setText("magnificent mood");
                tvBadgeDescription.setText("track a week where your average mood is greater than 3");
            } else if (badgeImage.equals("badge_month_long_master")){
                tvBadge.setText("month long master");
                tvBadgeDescription.setText("earn a streak of 30 days on a habit");
            } else if (badgeImage.equals("badge_no_red_days")){
                tvBadge.setText("no red days");
                tvBadgeDescription.setText("track a mood of 3 or higher every day for a week straight");
            } else if (badgeImage.equals("badge_perfect_day")){
                tvBadge.setText("perfect day");
                tvBadgeDescription.setText("complete every one of your habits on the same day");
            } else if (badgeImage.equals("badge_seven_days_of_smiles")){
                tvBadge.setText("seven days of smiles");
                tvBadgeDescription.setText("track a week where your average mood is greater than 4");
            } else if (badgeImage.equals("badge_two_week_triumph")){
                tvBadge.setText("two week triumph");
                tvBadgeDescription.setText("earn a streak of 14 days on a habit");
            } else if (badgeImage.equals("badge_warmest_welcomes")){
                tvBadge.setText("warmest welcomes");
                tvBadgeDescription.setText("create an account and setup your profile");
            } else if (badgeImage.equals("badge_weeklong_warrior")){
                tvBadge.setText("weeklong warrior");
                tvBadgeDescription.setText("earn a streak of 7 days on a habit");
            } else if (badgeImage.equals("badge_world_traveler")){
                tvBadge.setText("world traveler");
                tvBadgeDescription.setText("update your zipcode to a new location");
            } else if (badgeImage.equals("badge_you_did_it")){
                tvBadge.setText("you did it");
                tvBadgeDescription.setText("track your habits for the first time");
            } else {
                tvBadge.setText("hmm, there's been an issue");
            }
        }
    }
}
