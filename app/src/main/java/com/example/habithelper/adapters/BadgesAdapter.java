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
import com.example.habithelper.models.BadgeDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {

    private Context context;
    private List<String> badgeList;
    Map<String, BadgeDetails> badgeDetailsMap = new HashMap<String, BadgeDetails>();

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
            initializeHashMap();
            tvBadge.setText(badgeDetailsMap.get(badgeImage).text);
            tvBadgeDescription.setText(badgeDetailsMap.get(badgeImage).description);
            Resources resources = context.getResources();
            int resId = resources.getIdentifier(badgeImage, "drawable", "com.example.habithelper");
            ivBadge.setBackgroundResource(resId);
        }

        private void initializeHashMap() {
            badgeDetailsMap.put("badge_magnificent_mood", new BadgeDetails("magnificent mood", "track a week where your average mood is greater than 3"));
            badgeDetailsMap.put("badge_month_long_master", new BadgeDetails("month long master", "earn a streak of 30 days on a habit"));
            badgeDetailsMap.put("badge_no_red_days", new BadgeDetails("no red days", "track a mood of 3 or higher every day for a week straight"));
            badgeDetailsMap.put("badge_perfect_day", new BadgeDetails("perfect day", "complete every one of your habits on the same day"));
            badgeDetailsMap.put("badge_seven_days_of_smiles", new BadgeDetails("seven days of smiles", "track a week where your average mood is greater than 4"));
            badgeDetailsMap.put("badge_two_week_triumph", new BadgeDetails("two week triumph", "earn a streak of 14 days on a habit"));
            badgeDetailsMap.put("badge_warmest_welcomes", new BadgeDetails("warmest welcomes", "create an account and setup your profile"));
            badgeDetailsMap.put("badge_weeklong_warrior", new BadgeDetails("weeklong warrior", "earn a streak of 7 days on a habit"));
            badgeDetailsMap.put("badge_world_traveler", new BadgeDetails("world traveler", "update your zipcode to a new location"));
            badgeDetailsMap.put("badge_you_did_it", new BadgeDetails("you did it", "track your habits for the first time"));
        }
    }
}
