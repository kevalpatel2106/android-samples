package com.androidsample.wearableapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/StepCount")) {
                    int stepCount = dataMap.getInt("step-count");
                    long timeStamp = dataMap.getLong("step-stamp");

                    Toast.makeText(this, "Step count: " + stepCount + ", time stamp: " + timeStamp, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
