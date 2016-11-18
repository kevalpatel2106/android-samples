package com.androidsample.watchface;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class ComService extends WearableListenerService {
    public static final String ACTION_BG_COLOR_CHANGE = "action.bg.color.change";
    public static final String ARG_NEW_COLOR = "new_color";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        Log.d("data","receoved");

        for (DataEvent dataEvent : dataEventBuffer) {

            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {

                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/bg_change")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    String newColor = dataMap.getString("new_color", "#000000");

                    getSharedPreferences("settings", Context.MODE_PRIVATE)
                            .edit()
                            .putString("select_color", newColor)
                            .apply();

                    //Broadcast to service
                    Intent intent = new Intent(ACTION_BG_COLOR_CHANGE);
                    intent.putExtra(ARG_NEW_COLOR, newColor);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
    }
}
