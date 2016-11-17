package sample.kevalpatel2106.com.myapplication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class WearableNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "NewMessage";

    public static final String REMOTE_INPUT_LABEL = "remote_input";

    public static void notify(final Context context,
                              final String exampleString) {
        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);


        final String title = res.getString(
                R.string.new_message_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.new_message_notification_placeholder_text_template, exampleString);

        RemoteInput remoteInput = new RemoteInput.Builder(REMOTE_INPUT_LABEL)
                .setLabel("Replay by voice")
                .setChoices(new String[]{"Yes", "No", "I'm feeling lucky."})
                .build();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_new_message)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(picture)
                .setTicker(exampleString)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText("Dummy summary text"))
                .addAction(                         //this is device actions. This action won't appear in wearable
                        R.drawable.ic_action_stat_reply,
                        res.getString(R.string.action_reply),
                        null)
                .extend(new NotificationCompat.WearableExtender()   //Wearable specific
                        .addAction(new NotificationCompat.Action.Builder(     //Add wearable specific actions
                                R.drawable.ic_action_stat_reply,
                                "Replay",
                                PendingIntent.getActivity(
                                        context,
                                        0,
                                        new Intent(context, WearableReplay.class),
                                        PendingIntent.FLAG_UPDATE_CURRENT))
                                .addRemoteInput(remoteInput)    //add remote voice input
                                .build())
                        .setHintHideIcon(true)     //Do not display phone's notification large icon
                        .setBackground(BitmapFactory.decodeResource(res, R.drawable.backgorund_wearable)))   //set the custom icon background
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
