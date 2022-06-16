package com.example.habithelper;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("TrackDay")
public class TrackDay extends ParseObject {
    public static final String KEY_TRACK_ARRAY="trackArray";
    public static final String KEY_PARENT_USER="parentUser";
    public static final String KEY_MOOD="mood";
    public static final String KEY_DATE_TRACKED="dateTracked";
    public static final String KEY_DATE_NUMBER="dateNumber";

    public double getMood(){return getDouble(KEY_MOOD);}

    public void setMood(double mood){put(KEY_MOOD, mood);}

    public Date getDateTracked(){return getDate(KEY_DATE_TRACKED);}

    public void setDateTracked(Date date){put(KEY_DATE_TRACKED, date);}

    public int getDateNumber(){return getInt(KEY_DATE_NUMBER);}

    public void setDateNumber(int date){put(KEY_DATE_NUMBER, date);}

    public ParseUser getParentUser(){return getParseUser(KEY_PARENT_USER);}

    public void setParentUser(ParseUser parent){put(KEY_PARENT_USER, parent);}

    public JSONArray getTrackArray(){return getJSONArray(KEY_TRACK_ARRAY);}

    public void setTrackArray(JSONArray array){put(KEY_TRACK_ARRAY, array);}


}
