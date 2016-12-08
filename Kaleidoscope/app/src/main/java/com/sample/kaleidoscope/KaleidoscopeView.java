package com.sample.kaleidoscope;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
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
    private Context mContext;

    private Paint mLinePaint;

    private int mViewHeight;
    private int mViewWidth;

    private int mNoOfRows;
    private int mNoOfColumns;

    private float mSingleSlotWidth;
    private float mSingleSlotHeight;

    private Path mDrawPath;
    private Paint mDrawPaint;

    public KaleidoscopeView(Context context, int rows, int columns) {
        super(context);
        init(context, rows, columns);
    }

    public KaleidoscopeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KaleidoscopeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KaleidoscopeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, int rows, int columns) {
        mContext = context;
        mNoOfRows = rows;
        mNoOfColumns = columns;

        mBoxes = new ArrayList<>();

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.parseColor("#000000"));

        setupPath();

        invalidate();
    }

    private void setupPath() {

        //set paint
        mDrawPaint = new Paint();
        mDrawPaint.setColor(Color.parseColor("#FF0000"));
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(5);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mNoOfColumns; i++) {
            canvas.drawLine(mSingleSlotWidth * i, 0, mSingleSlotWidth * i, mViewHeight, mLinePaint);
        }

        for (int j = 0; j < mNoOfRows; j++) {
            canvas.drawLine(0, mSingleSlotHeight * j, mViewWidth, mSingleSlotHeight * j, mLinePaint);
        }

        //draw the path drawn by user
        for (Box box : mBoxes) canvas.drawPath(box.path, mDrawPaint);
    }

    private void setBoxes(float viewWidth, float viewHeight) {
        mSingleSlotHeight = viewHeight / mNoOfRows;
        mSingleSlotWidth = viewWidth / mNoOfColumns;

        mBoxes = new ArrayList<>(mNoOfColumns * mNoOfRows);

        float x = 0, y = 0;
        for (int i = 0; i < mNoOfColumns; i++) {
            for (int j = 0; j < mNoOfRows; j++) {
                Box box = new Box();
                box.i = i;
                box.j = j;

                x = x + mSingleSlotWidth * i;
                y = y + mSingleSlotHeight * j;

                box.startX = x;
                box.startY = y;

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
                mSelectedCol = (int)(touchX / mSingleSlotWidth);
                mSelectedRow = (int)(touchY / mSingleSlotHeight);

                for (int i = 0; i < mBoxes.size(); i++) {
                    Box box = mBoxes.get(i);
                    //erase old path
                    box.path.reset();

                    box.path.moveTo(touchX + mSingleSlotWidth * (box.i - mSelectedCol), touchY + (mSingleSlotHeight * (box.j - mSelectedRow)));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < mBoxes.size(); i++) {
                    Box box = mBoxes.get(i);
                    box.path.lineTo(touchX + mSingleSlotWidth * (box.i - mSelectedCol), touchY + (mSingleSlotHeight * (box.j - mSelectedRow)));
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
        private float startX;
        private float startY;

        private int i;
        private int j;

        private Path path;
    }
}
