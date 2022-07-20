package com.example.habithelper.utilities;

public enum FragmentEnum {
    PROFILE_FRAGMENT (0),
    TRACK_FRAGMENT (1),
    HOME_FRAGMENT (2),
    MOOD_FRAGMENT (3),
    HABIT_LIST_FRAGMENT (4);

    private final int index;

    FragmentEnum(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
