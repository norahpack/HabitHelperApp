package com.example.habithelper.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Habit")
public class Habit extends ParseObject {
    public static final String KEY_HABIT_NAME = "habitName";
    public static final String KEY_HABIT_IMAGE = "habitImage";
    public static final String KEY_HABIT_CREATOR = "habitCreator";

    public String getHabitName(){return getString(KEY_HABIT_NAME);}

    public void setHabitName(String habitName){put(KEY_HABIT_NAME, habitName);}

    public int getHabitImageKey(){return getInt(KEY_HABIT_IMAGE);}

    public void setHabitImageKey(int imageKey){put(KEY_HABIT_IMAGE, imageKey);}

    public ParseUser getCreator(){return getParseUser(KEY_HABIT_CREATOR);}

    public void setCreator(ParseUser creator){put(KEY_HABIT_CREATOR, creator);}

}
