package com.example.habithelper.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyDrawView extends View {
    public Bitmap mBitmap;
    public Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;


    /**
     * initializes the path and paint objects and supplies relevant parameters
     */
    public MyDrawView(Context c, AttributeSet attrs) {
        super(c, attrs);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#A44F30"));
        //lets the user draw in a paintbrush-type style
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);
    }

    /**
     * initializes the canvas and creates a bitmap
     * @param w the new width
     * @param h the new height
     * @param oldw the old width
     * @param oldh the old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    /**
     * Allows the canvas to be drawn on
     * @param canvas the canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#EBE2D9"));
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Handles the user first pressing down on the screen to draw
     * @param x the x coordinate of the press
     * @param y the y coordinate of the press
     */
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * Handles the user dragging their finger to draw on the canvas
     * @param x the x coordinate of the new location
     * @param y the y coordinate of the new location
     */
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    /**
     * Handles the user taking their finger/mouse off the screen;
     * Uses paint to draw the Path on the canvas.
     */
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    /**
     * handles all user touch events on the canvas.
     * @param event the user's touch event on the canvas
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Gets a bitmap of the canvas
     * @return the bitmap
     */
    public Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
