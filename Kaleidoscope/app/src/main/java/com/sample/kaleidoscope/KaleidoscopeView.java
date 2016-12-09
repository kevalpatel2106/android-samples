package com.sample.kaleidoscope;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Keval on 08-Dec-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class KaleidoscopeView extends View {
    private ArrayList<Box> mBoxes;

    private Paint mLinePaint;
    private Paint mDrawPaint;

    private int mViewHeight;
    private int mViewWidth;

    private int mNoOfRows;
    private int mNoOfColumns;

    private float mSingleSlotWidth;
    private float mSingleSlotHeight;

    public KaleidoscopeView(Context context, int rows, int columns, @ColorInt int pathColor, @ColorInt int boxColor) {
        super(context);
        init(rows, columns, pathColor, boxColor);
    }

    public KaleidoscopeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KaleidoScope, 0, 0);
        init(ta.getInt(R.styleable.KaleidoScope_noRows, 2),
                ta.getInt(R.styleable.KaleidoScope_noColumns, 2),
                ta.getColor(R.styleable.KaleidoScope_pathColor, Color.parseColor("#FF0000")),
                ta.getColor(R.styleable.KaleidoScope_boxColor, Color.parseColor("#000000")));
    }

    public KaleidoscopeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KaleidoScope, 0, 0);
        init(ta.getInt(R.styleable.KaleidoScope_noRows, 2),
                ta.getInt(R.styleable.KaleidoScope_noColumns, 2),
                ta.getColor(R.styleable.KaleidoScope_pathColor, Color.parseColor("#FF0000")),
                ta.getColor(R.styleable.KaleidoScope_boxColor, Color.parseColor("#000000")));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KaleidoscopeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KaleidoScope, 0, 0);
        init(ta.getInt(R.styleable.KaleidoScope_noRows, 2),
                ta.getInt(R.styleable.KaleidoScope_noColumns, 2),
                ta.getColor(R.styleable.KaleidoScope_pathColor, Color.parseColor("#FF0000")),
                ta.getColor(R.styleable.KaleidoScope_boxColor, Color.parseColor("#000000")));
    }

    private void init(int rows, int columns, @ColorInt int pathColor, @ColorInt int boxColor) {
        mNoOfRows = rows;
        mNoOfColumns = columns;

        mBoxes = new ArrayList<>();

        //set point for the separator line
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(boxColor);

        //set paint for the path
        mDrawPaint = new Paint();
        mDrawPaint.setColor(pathColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(5);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mNoOfColumns; i++)
            canvas.drawLine(mSingleSlotWidth * i, 0, mSingleSlotWidth * i, mViewHeight, mLinePaint);
        for (int j = 0; j < mNoOfRows; j++)
            canvas.drawLine(0, mSingleSlotHeight * j, mViewWidth, mSingleSlotHeight * j, mLinePaint);

        //draw the path drawn by user
        for (Box box : mBoxes) {
            canvas.drawPath(box.path, mDrawPaint);
            if (box.path2 != null) canvas.drawPath(box.path2, mDrawPaint);
        }
    }

    private void setBoxes(float viewWidth, float viewHeight) {
        mSingleSlotHeight = viewHeight / mNoOfRows;
        mSingleSlotWidth = viewWidth / mNoOfColumns;

        mBoxes = new ArrayList<>(mNoOfColumns * mNoOfRows);

        for (int i = 0; i < mNoOfColumns; i++) {
            for (int j = 0; j < mNoOfRows; j++) {
                Box box = new Box();
                box.i = i;
                box.j = j;

                //initialize path
                box.path = new Path();
                mBoxes.add(box);
            }
        }
    }

    private int mSelectedCol = 0, mSelectedRow = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //get the location of the box
                mSelectedCol = (int) (touchX / mSingleSlotWidth);
                mSelectedRow = (int) (touchY / mSingleSlotHeight);

                for (Box box : mBoxes) {
                    //erase old path
                    box.reset();

                    //draw the start point
                    box.path.moveTo(touchX + mSingleSlotWidth * (box.i - mSelectedCol), touchY + (mSingleSlotHeight * (box.j - mSelectedRow)));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float pathX;
                float pathY;

                for (Box box : mBoxes) {
                    pathX = touchX + mSingleSlotWidth * (box.i - mSelectedCol);
                    pathY = touchY + (mSingleSlotHeight * (box.j - mSelectedRow));

                    box.path.lineTo(pathX, pathY);

                    boolean isOutOfScreen = false;
                    if (pathX > mViewWidth) {
                        pathX = pathX - mViewWidth;
                        isOutOfScreen = true;
                    }
                    if (pathY > mViewHeight) {
                        pathY = pathY - mViewHeight;
                        isOutOfScreen = true;
                    }

                    if (isOutOfScreen) {
                        if (box.path2 == null) {
                            box.path2 = new Path();
                            box.path2.moveTo(pathX, pathY);
                        } else {
                            box.path2.lineTo(pathX, pathY);
                        }
                    }
                    Log.d("path", pathX + " " + pathY);
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(mViewWidth, mViewHeight);

        setBoxes(mViewWidth, mViewHeight);

        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class Box {
        private int i;
        private int j;

        private Path path;
        private Path path2;

        private void reset() {
            path2 = null;
            path.reset();
        }
    }
}
