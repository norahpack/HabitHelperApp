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

    private static final int CIRCLE_POSITION = 412;
    private static final int UPPER_CIRCLE_RADIUS = 355;
    private static final int LOWER_CIRCLE_RADIUS = 385;
    public static final int UPPER_OFFSET = 1590;
    public static final int LOWER_OFFSET = 1700;
    public static final int VERTICAL_OFFSET = 0;


    public CurvedText(Context context, AttributeSet attrs) {
        super(context, attrs);

        circle = new Path();
        circle2 = new Path();
        circle.addCircle(CIRCLE_POSITION, CIRCLE_POSITION, UPPER_CIRCLE_RADIUS, Path.Direction.CW);
        circle2.addCircle(CIRCLE_POSITION, CIRCLE_POSITION, LOWER_CIRCLE_RADIUS, Path.Direction.CCW);

        setBackgroundResource(R.drawable.circle_logo_border);

        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setColor(Color.parseColor("#EBE2D9"));
        tPaint.setTypeface(getResources().getFont(R.font.bright));
        tPaint.setTextSize(50);

        myString = ParseUser.getCurrentUser().getString("name");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawTextOnPath(myString, circle, UPPER_OFFSET, VERTICAL_OFFSET, tPaint);
        canvas.drawTextOnPath("habit hero", circle2, LOWER_OFFSET, VERTICAL_OFFSET, tPaint);
    }
}
