package com.androidsample.wearableapp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * This service is responsible for receiving messages and data maps from android wear.
 * Starting and stopping this service will done automatically by Google Play Services.
 * <p>
 * IF there is any change in DataMap than {@link #onDataChanged(DataEventBuffer)} will be called.
 * IF there is any new message than {@link #onMessageReceived(MessageEvent)} will be called.
 */
public class WearService extends WearableListenerService {
    public static final String TRACKING_STATUS_ACTION = "step.tracking.status";
    public static final String TRACKING_COUNT_ACTION = "step.tracking.count";


    private static final String STEP_COUNT_MESSAGES_PATH = "/StepCount";
    private static final String STEP_TRACKING_STATUS_PATH = "/TrackingStatus";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        //This method will call while any changes in data map occurs from watch side
        //This is data map. So, message delivery is guaranteed.
        for (DataEvent dataEvent : dataEventBuffer) {

            //Check for only those data who changed
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                //Check if the data map path matches with the step tracking status path
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals(STEP_TRACKING_STATUS_PATH)) {

                    //Read the values
                    boolean isTracking = dataMap.getBoolean("status");
                    long timeStamp = dataMap.getLong("status-time");

                    //send broadcast to update the UI in MainActivity based on the tracking status
                    Intent intent = new Intent(TRACKING_STATUS_ACTION);
                    intent.putExtra("status", isTracking);
                    intent.putExtra("status-time", timeStamp);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                    Log.d("Tracking status: ", isTracking + " Time: " + timeStamp);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //This method will call while any message is posted by the watch to the phone.
        //This is message api, so if the phone is not connected message will be lost.
        //No guarantee of the message delivery

        //check path of the message
        if (messageEvent.getPath().equalsIgnoreCase(STEP_COUNT_MESSAGES_PATH)) {

            //Extract the values
            String stepCount = new String(messageEvent.getData());
            Log.d("Step count: ", stepCount + " ");

            //send broadcast to update the UI in MainActivity based on the tracking status
            Intent intent = new Intent(TRACKING_COUNT_ACTION);
            intent.putExtra("step-count", stepCount);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
