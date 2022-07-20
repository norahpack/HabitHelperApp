package com.example.habithelper.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Habit")
public class Habit extends ParseObject {
    public static final String KEY_HABIT_NAME = "habitName";
    public static final String KEY_HABIT_IMAGE_NAME = "habitImageName";
    public static final String KEY_HABIT_CREATOR = "habitCreator";
    public static final String KEY_HABIT_CUSTOM_ICON = "habitCustomIcon";

    public String getHabitName(){return getString(KEY_HABIT_NAME);}

    public void setHabitName(String habitName){put(KEY_HABIT_NAME, habitName);}

    public String getHabitImageKey(){return getString(KEY_HABIT_IMAGE_NAME);}

    public void setHabitImageKey(String imageKey){put(KEY_HABIT_IMAGE_NAME, imageKey);}

    public void setCreator(ParseUser creator){put(KEY_HABIT_CREATOR, creator);}

    public ParseFile getHabitCustomIcon(){
        return getParseFile(KEY_HABIT_CUSTOM_ICON);
    }

    public void setHabitCustomIcon(ParseFile parseFile){
        put(KEY_HABIT_CUSTOM_ICON, parseFile);
    }

}
