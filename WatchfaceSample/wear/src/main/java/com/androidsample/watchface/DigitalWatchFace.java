package com.androidsample.watchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextPaint;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DigitalWatchFace extends CanvasWatchFaceService {

    /**
     * Create the {@link android.support.wearable.watchface.CanvasWatchFaceService.Engine}
     */
    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    /**
     * A {@link android.support.wearable.watchface.CanvasWatchFaceService.Engine} class that contains
     * core logic of the watch face.
     */
    private class Engine extends CanvasWatchFaceService.Engine {
        private static final long TICK_PERIOD_MILLIS = 1000;    //1 sec
        private int mNormalBgColor;

        private SimpleDateFormat mDateFormatWithSec;
        private SimpleDateFormat mDateFormatWithoutSec;

        private TextPaint mTextPaint;

        private Handler mTimeTick;
        private final Runnable mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    mTimeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFace.this)
                    .setHideStatusBar(true)
                    .setShowSystemUiTime(false) // we set the UI time to false because we will already show the time on the watch by drawing it onto the canvas
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)    //when the watch enters in ambient mode, no peek card will be visible
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)    //this will specify that the first card peeked and shown on the watch will have a single line tail (i.e. it will have small height)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE) //the background of the peek card should only be shown briefly, and only if the peek card represents an interruptive notification
                    .build());

            //Initialize every thing here, not in onDraw().
            mDateFormatWithoutSec = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            mDateFormatWithSec = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());


            mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(ContextCompat.getColor(DigitalWatchFace.this, android.R.color.white));
            mTextPaint.setTextSize(getResources().getDimension(R.dimen.clock_text_size));
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            mTextPaint.setTypeface(Typeface.createFromAsset(getAssets(), "Montserrat-Bold.ttf"));

            mNormalBgColor = ContextCompat.getColor(DigitalWatchFace.this, android.R.color.holo_green_dark);

            mTimeTick = new Handler(Looper.myLooper());
            mTimeTick.postDelayed(mTimeRunnable,1000);
        }

        /**
         * This will start timer to tick every second, if the device is not in the ambient mode and
         * watch face is visible. In ambient mode we don't need timer, we will update watch face every
         * minute when {@link #onTimeTick()} callback is received.
         */
        private void startTimerIfNecessary() {
            mTimeTick.removeCallbacks(mTimeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                mTimeTick.post(mTimeRunnable);
            }
        }

        /**
         * probably the most important callback, this is called every time the watch face is invalidated. Here we will define the draw logic of the watch face
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            //Set the background color
            canvas.drawColor(isInAmbientMode() ? Color.BLACK : mNormalBgColor);

            //Display the hour:minute:second
            canvas.drawText(getClockTimeText(), bounds.centerX(), bounds.centerY(), mTextPaint);
        }

        @SuppressWarnings("WrongConstant")
        private String getClockTimeText() {
           if (isInAmbientMode()){
               return mDateFormatWithoutSec.format(System.currentTimeMillis());
           }else {
               return mDateFormatWithSec.format(System.currentTimeMillis());
           }
        }

        /**
         * This callback is invoked every minute when the watch is in ambient mode. It is very important
         * to consider that this callback is only invoked while on ambient mode, as it's name is rather
         * confusing suggesting that this callbacks every time. This being said, usually, here we will
         * have only to invalidate() the watch in order to trigger onDraw(). In order to keep track of
         * time outside ambient mode, we will have to provide our own mechanism.
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        /**
         * This is called when the device enters or exits ambient mode. While on ambient mode, one should
         * be considerate to preserve battery consumption by providing a black and white display and not
         * provide any animation such as displaying seconds.
         *
         * @param inAmbientMode true if the device is in ambient mode
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            startTimerIfNecessary();
        }


        @Override
        public void onDestroy() {
            mTimeTick.removeCallbacks(mTimeRunnable);
            super.onDestroy();
        }
    }
}
