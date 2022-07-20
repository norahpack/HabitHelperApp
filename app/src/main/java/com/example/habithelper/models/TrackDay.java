package com.example.habithelper.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.List;

@ParseClassName("TrackDay")
public class TrackDay extends ParseObject {
    public static final String KEY_TRACK_ARRAY = "trackArray";
    public static final String KEY_PARENT_USER = "parentUser";
    public static final String KEY_MOOD = "mood";
    public static final String KEY_DATE_NUMBER = "dateNumber";
    public static final String KEY_EARNED_WEEKLONG_WARRIOR = "earnedWeeklongWarrior";
    public static final String KEY_EARNED_TWO_WEEK_TRIUMPH = "earnedTwoWeekTriumph";
    public static final String KEY_EARNED_MONTH_LONG_MASTER = "earnedMonthLongMaster";
    public static final String KEY_EARNED_PERFECT_DAY = "earnedPerfectDay";
    public static final String KEY_EARNED_MAGNIFICENT_MOOD = "earnedMagnificentMood";
    public static final String KEY_EARNED_SEVEN_DAYS_OF_SMILES = "earnedSevenDaysOfSmiles";
    public static final String KEY_EARNED_NO_RED_DAYS = "earnedNoRedDays";

    public int getMood(){return getInt(KEY_MOOD);}

    public void setMood(Integer mood){put(KEY_MOOD, mood);}

    public void setDateNumber(int date){put(KEY_DATE_NUMBER, date);}

    public void setParentUser(ParseUser parent){put(KEY_PARENT_USER, parent);}

    public List<Integer> getTrackArray(){return getList(KEY_TRACK_ARRAY);}

    public void setTrackArray(List<Integer> array){put(KEY_TRACK_ARRAY, array);}

    public boolean getEarnedWeeklongWarrior(){return getBoolean(KEY_EARNED_WEEKLONG_WARRIOR);}

    public void setEarnedWeeklongWarrior(boolean earned){put(KEY_EARNED_WEEKLONG_WARRIOR, earned);}

    public boolean getEarnedTwoWeekTriumph(){return getBoolean(KEY_EARNED_TWO_WEEK_TRIUMPH);}

    public void setEarnedTwoWeekTriumph(boolean earned){put(KEY_EARNED_TWO_WEEK_TRIUMPH, earned);}

    public boolean getEarnedMonthLongMaster(){return getBoolean(KEY_EARNED_MONTH_LONG_MASTER);}

    public void setEarnedMonthLongMaster(boolean earned){put(KEY_EARNED_MONTH_LONG_MASTER, earned);}

    public boolean getEarnedPerfectDay(){return getBoolean(KEY_EARNED_PERFECT_DAY);}

    public void setEarnedPerfectDay(boolean earned){put(KEY_EARNED_PERFECT_DAY, earned);}

    public boolean getEarnedMagnificentMood(){return getBoolean(KEY_EARNED_MAGNIFICENT_MOOD);}

    public void setEarnedMagnificentMood(boolean earned){put(KEY_EARNED_MAGNIFICENT_MOOD, earned);}

    public boolean getEarnedSevenDaysOfSmiles(){return getBoolean(KEY_EARNED_SEVEN_DAYS_OF_SMILES);}

    public void setEarnedSevenDaysOfSmiles(boolean earned){put(KEY_EARNED_SEVEN_DAYS_OF_SMILES, earned);}

    public boolean getEarnedNoRedDays(){return getBoolean(KEY_EARNED_NO_RED_DAYS);}

    public void setEarnedNoRedDays(boolean earned){put(KEY_EARNED_NO_RED_DAYS, earned);}

}
