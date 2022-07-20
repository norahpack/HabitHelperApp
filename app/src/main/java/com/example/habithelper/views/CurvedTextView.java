package com.example.habithelper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.example.habithelper.R;
import com.parse.ParseUser;

public class CurvedTextView extends View {
    private Path circle;
    private Path circle2;
    private Paint tPaint;
    public String myString;
    public String levelString;
    public int nameOffset;
    public int levelOffset;

    private static final int UPPER_CIRCLE_RADIUS = 240;
    private static final int LOWER_CIRCLE_RADIUS = 250;
    public static final int UPPER_OFFSET = 1067;
    public static final int LOWER_OFFSET = 890;
    public static final int VERTICAL_OFFSET = 0;
    public static final int CENTER_COORDINATES = 300;
    public static final int UPPER_VERTICAL_COORDINATES = 320;
    public static final int DEFAULT_NAME_LENGTH = 5;
    public static final int OFFSET_PER_NAME_LETTER = -14;
    public static final int PAINT_TEXT_SIZE = 50;
    public static final int MIN_DAYS_LEVEL_FIVE = 100;
    public static final int MIN_DAYS_LEVEL_FOUR = 50;
    public static final int MIN_DAYS_LEVEL_THREE = 25;
    public static final int MIN_DAYS_LEVEL_TWO = 10;
    public static final int OFFSET_LEVEL_ONE = -3;
    public static final int OFFSET_LEVEL_TWO = -8;
    public static final int OFFSET_LEVEL_THREE = -3;
    public static final int OFFSET_LEVEL_FOUR = -38;
    public static final int OFFSET_LEVEL_FIVE = 48;

    public CurvedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myString = ParseUser.getCurrentUser().getString("name");
        nameOffset = (myString.length() - DEFAULT_NAME_LENGTH) * (OFFSET_PER_NAME_LETTER);
        int numDaysTracked = ParseUser.getCurrentUser().getInt("numDaysTracked");

        circle = new Path();
        circle2 = new Path();
        circle.addCircle(CENTER_COORDINATES, UPPER_VERTICAL_COORDINATES, UPPER_CIRCLE_RADIUS, Path.Direction.CW);
        circle2.addCircle(CENTER_COORDINATES, CENTER_COORDINATES, LOWER_CIRCLE_RADIUS, Path.Direction.CCW);

        setLevelText(numDaysTracked);

        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setColor((getResources().getColor(R.color.dusty_rose)));
        tPaint.setTypeface(getResources().getFont(R.font.bright));
        tPaint.setTextSize(PAINT_TEXT_SIZE);
    }

    /**
     * Determines the level the user is at and sets the profile level accordingly
     * @param numDaysTracked the number of days the user has tracked for
     */
    private void setLevelText(int numDaysTracked) {
        if(numDaysTracked > MIN_DAYS_LEVEL_FIVE){
            levelString = "level five - habit hero";
            levelOffset = OFFSET_LEVEL_FIVE;
        } else if (numDaysTracked > MIN_DAYS_LEVEL_FOUR){
            levelString = "level four - productivity pro";
            levelOffset = OFFSET_LEVEL_FOUR;
        } else if (numDaysTracked > MIN_DAYS_LEVEL_THREE){
            levelString = "level three - mood master";
            levelOffset = OFFSET_LEVEL_THREE;
        } else if (numDaysTracked > MIN_DAYS_LEVEL_TWO){
            levelString = "level two - terrific tracker";
            levelOffset = OFFSET_LEVEL_TWO;
        } else {
            levelString = "level one - routine rookie";
            levelOffset = OFFSET_LEVEL_ONE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawTextOnPath(myString, circle, UPPER_OFFSET+nameOffset, VERTICAL_OFFSET, tPaint);
        canvas.drawTextOnPath(levelString, circle2, LOWER_OFFSET+levelOffset, VERTICAL_OFFSET, tPaint);
    }
}
