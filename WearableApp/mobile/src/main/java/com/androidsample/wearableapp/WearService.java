package com.androidsample.wearableapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearService extends WearableListenerService {
    public static final String TRACKING_STATUS_ACTION = "step.tracking.status";
    public static final String TRACKING_COUNT_ACTION = "step.tracking.count";


    private static final String STEP_COUNT_MESSAGES_PATH = "/StepCount";
    private static final String STEP_TRACKING_STATUS_PATH = "/TrackingStatus";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals(STEP_TRACKING_STATUS_PATH)) {
                    boolean isTracking = dataMap.getBoolean("status");
                    long timeStamp = dataMap.getLong("status-time");

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
        if (messageEvent.getPath().equalsIgnoreCase(STEP_COUNT_MESSAGES_PATH)) {
            String stepCount = new String(messageEvent.getData());
            Log.d("Step count: ", stepCount + " ");

            Intent intent = new Intent(TRACKING_COUNT_ACTION);
            intent.putExtra("step-count", stepCount);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
