package com.androidsample.wearableapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import wearableapp.androidsample.wearableapp.wearableapp.R;

public class TrackActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private SensorManager mSensorManager;
    private Sensor mStepSensor;

    private GoogleApiClient mGoogleApiClient;

    private WatchViewStub mWatchViewStub;
    private ImageView mHeaderIv;
    private TextView mStepCountTv;

    private static final String STEP_COUNT_MESSAGES_PATH = "/StepCount";
    private static final String STEP_TRACKING_STATUS_PATH = "/TrackingStatus";

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        private float mStepOffset;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mStepOffset == 0) mStepOffset = event.values[0];

            if (mStepCountTv != null) {
                String stepCount = Float.toString(event.values[0] - mStepOffset);
                mStepCountTv.setText(stepCount);
                sendStepCountMessage(stepCount);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //Set this to allow activity to enable the ambient mode
        setAmbientEnabled();

        //Get the watch view stub
        mWatchViewStub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        mWatchViewStub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                init(watchViewStub);
            }
        });

        connectGoogleApiClient();

        registerStepSensor();
    }

    private void connectGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private void registerStepSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mSensorManager.unregisterListener(mSensorEventListener);
            sendTrackingStatusDataMap(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private void init(WatchViewStub watchViewStub) {
        mHeaderIv = (ImageView) watchViewStub.findViewById(R.id.header_icon);
        mHeaderIv.setColorFilter(ContextCompat.getColor(this, R.color.icon_red));

        mStepCountTv = (TextView) watchViewStub.findViewById(R.id.step_count_tv);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        //This method will called whenever the application is entering the
        //ambient mode. Try to simplify the view and display less number of colors
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        //This method will called whenever the ambient mode updates.
        //This will allow you to update the existing view and the
        //duration period is almost every 1 min.
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        //This method will be called whenever device exists from the ambient mode and entered into
        //interactive mode.
        updateDisplay();

        super.onExitAmbient();
    }

    /**
     * Update the display based on if the device is in ambiant mode or not. If the device is in
     * ambient mode this will try to reduce number of colors on the screen and try to simplify the view.
     */
    private void updateDisplay() {
        if (isAmbient()) {
            mWatchViewStub.setBackgroundColor(getResources().getColor(android.R.color.black));
            mHeaderIv.setColorFilter(ContextCompat.getColor(this, R.color.white));
        } else {
            mWatchViewStub.setBackgroundColor(getResources().getColor(R.color.green));
            mHeaderIv.setColorFilter(ContextCompat.getColor(this, R.color.icon_red));
        }
    }

    /**
     * Send the current step count to the device with the time stamp to the phone. Phone will just display
     * the data on the screen.  We are using DataMap here to ensure the delivery of the messages.
     *
     * @param isTracking true if the tracking is running
     */
    private void sendTrackingStatusDataMap(boolean isTracking) {
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(STEP_TRACKING_STATUS_PATH);

        dataMapRequest.getDataMap().putBoolean("status", isTracking);
        dataMapRequest.getDataMap().putLong("status-time", System.currentTimeMillis());

        PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        Log.d("Data saving", dataItemResult.getStatus().isSuccess() ? "Success" : "Failed");
                    }
                });

    }

    private void sendStepCountMessage(final String stepCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), STEP_COUNT_MESSAGES_PATH, stepCount.getBytes()).await();

                    Log.d("Messages Api", result.getStatus().isSuccess() ? "Sent successfully" : "Sent failed.");
                }
            }
        }).start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mSensorManager.registerListener(mSensorEventListener, mStepSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sendTrackingStatusDataMap(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Cannot init GoogleApiClient.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot init GoogleApiClient.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
