package com.example.habithelper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.example.habithelper.R;
import com.parse.ParseUser;

public class CurvedText extends View {
    private Path circle;
    private Path circle2;
    private Paint tPaint;
    public String myString;
    public String levelString;
    public int nameOffset;

    private static final int UPPER_CIRCLE_RADIUS = 325;
    private static final int LOWER_CIRCLE_RADIUS = 415;
    public static final int UPPER_OFFSET = 1270;
    public static final int LOWER_OFFSET = 1860;
    public static final int VERTICAL_OFFSET = 0;


    public CurvedText(Context context, AttributeSet attrs) {
        super(context, attrs);
        myString = ParseUser.getCurrentUser().getString("name");
        nameOffset = (myString.length()-5)*(-10);
        int numDaysTracked = ParseUser.getCurrentUser().getInt("numDaysTracked");

        circle = new Path();
        circle2 = new Path();
        circle.addCircle(400, 380, UPPER_CIRCLE_RADIUS, Path.Direction.CW);
        circle2.addCircle(400, 380, LOWER_CIRCLE_RADIUS, Path.Direction.CCW);

        setBackground(numDaysTracked);

        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setColor(Color.parseColor("#EBE2D9"));
        tPaint.setTypeface(getResources().getFont(R.font.bright));
        tPaint.setTextSize(50);
    }

    /**
     * Determines the level the user is at and sets the profile badge and level accordingly
     * @param numDaysTracked the number of days the user has tracked for
     */
    private void setBackground(int numDaysTracked) {
        if(numDaysTracked > 100){
            setBackgroundResource(R.drawable.level_five);
            levelString = "level five - habit hero";
        } else if (numDaysTracked > 50){
            setBackgroundResource(R.drawable.level_four);
            levelString = "level four - productivity pro";
        } else if (numDaysTracked > 25){
            setBackgroundResource(R.drawable.level_three);
            levelString = "level three - mood master";
        } else if (numDaysTracked > 10){
            setBackgroundResource(R.drawable.level_two);
            levelString = "level two - terrific tracker";
        } else {
            setBackgroundResource(R.drawable.level_one);
            levelString = "level one - routine rookie";
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawTextOnPath(myString, circle, UPPER_OFFSET+nameOffset, VERTICAL_OFFSET, tPaint);
        canvas.drawTextOnPath(levelString, circle2, LOWER_OFFSET, VERTICAL_OFFSET, tPaint);
    }
}
