package com.androidsample.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;

/**
 * Created by Keval on 21-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class CounterWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_RESET = "com.counter.widget.ACTION_RESET";
    public static final String ACTION_INCREASE = "com.counter.widget.ACTION_INCREASE";
    public static final String ARG_WIDGET_ID = "arg_widget_id";
    public static final String ARG_OPERATION = "arg_operation_id";

    private static HashMap<Integer, Integer> mCounts = new HashMap<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int currentWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.counter_widget_layout);
            Log.d("count", mCounts.get(currentWidgetId) + "");
            if (!mCounts.containsKey(currentWidgetId))
                mCounts.put(currentWidgetId, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setTextViewText(R.id.tvCount, mCounts.get(currentWidgetId) + "");

            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.putExtra(ARG_WIDGET_ID, currentWidgetId);
            intent.putExtra(ARG_OPERATION, ACTION_INCREASE);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.btn_plus_one, pending);

            intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.putExtra(ARG_WIDGET_ID, currentWidgetId);
            intent.putExtra(ARG_OPERATION, ACTION_RESET);
            pending = PendingIntent.getBroadcast(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.btn_plus_one, pending);

            appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        int widgetId = intent.getIntExtra(ARG_WIDGET_ID, -1);
        Log.d("widget", widgetId  + " " + mCounts.keySet().toString());
        if (!mCounts.containsKey(widgetId)) return;

        switch (intent.getStringExtra(ARG_OPERATION)) {
            case ACTION_RESET:
                mCounts.put(widgetId, 0);
                break;
            case ACTION_INCREASE:
                int count = mCounts.get(widgetId);
                count++;

                mCounts.put(widgetId, count);
                break;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), CounterWidgetProvider.class.getName());
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisAppWidget));
    }
}
