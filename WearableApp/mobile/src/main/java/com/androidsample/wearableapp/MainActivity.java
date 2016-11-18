package com.androidsample.wearableapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import wearableapp.androidsample.wearableapp.wearableapp.R;

public class MainActivity extends AppCompatActivity {
    private long mLastStatusUpdateTime = 0;

    private TextView trackingStatusTv;
    private TextView stepCountTv;

    private BroadcastReceiver mTrackingStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long statusTimeStamp = intent.getLongExtra("status-time", 0);

            if (statusTimeStamp >= mLastStatusUpdateTime) {
                trackingStatusTv.setText(intent.getBooleanExtra("status", false) ?
                        R.string.tracking_running : R.string.tracking_stopped);
                mLastStatusUpdateTime = statusTimeStamp;
            }
        }
    };

    private BroadcastReceiver mStepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stepCountTv.setText(intent.getStringExtra("step-count"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackingStatusTv = (TextView) findViewById(R.id.textView);
        trackingStatusTv.setText(R.string.tracking_stopped);

        stepCountTv = (TextView) findViewById(R.id.step_count_tv);
        stepCountTv.setText("0");

        LocalBroadcastManager.getInstance(this).registerReceiver(mTrackingStatusReceiver,
                new IntentFilter(WearService.TRACKING_STATUS_ACTION));

        LocalBroadcastManager.getInstance(this).registerReceiver(mStepCountReceiver,
                new IntentFilter(WearService.TRACKING_COUNT_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTrackingStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStepCountReceiver);
    }
}
