package com.example.habithelper;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

@ParseClassName("TrackDay")
public class TrackDay extends ParseObject {
    public static final String KEY_TRACK_ARRAY="trackArray";
    public static final String KEY_PARENT_USER="parentUser";
    public static final String KEY_MOOD="mood";
    public static final String KEY_DATE_NUMBER="dateNumber";

    public int getMood(){return getInt(KEY_MOOD);}

    public void setMood(Integer mood){put(KEY_MOOD, mood);}

    public int getDateNumber(){return getInt(KEY_DATE_NUMBER);}

    public void setDateNumber(int date){put(KEY_DATE_NUMBER, date);}

    public ParseUser getParentUser(){return getParseUser(KEY_PARENT_USER);}

    public void setParentUser(ParseUser parent){put(KEY_PARENT_USER, parent);}

    public List<Integer> getTrackArray(){return getList(KEY_TRACK_ARRAY);}

    public void setTrackArray(List<Integer> array){put(KEY_TRACK_ARRAY, array);}


}
