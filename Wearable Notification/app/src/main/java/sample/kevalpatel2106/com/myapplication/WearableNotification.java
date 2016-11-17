package sample.kevalpatel2106.com.myapplication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

/**
 * Helper class for showing and canceling new message notifications on both android phone and wear.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper class to create notifications in a backward-compatible way.
 */
public class WearableNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "NewMessage";

    /**
     * This is the unique identifier for the remote input on the android wear device.
     */
    public static final String REMOTE_INPUT_LABEL = "remote_input";

    /**
     * Generate the sample notification.
     */
    public static void notify(final Context context,
                              final String title,
                              final String bigText) {
        final Resources res = context.getResources();


        //######################################################//
        //First create generalize notification for all platforms//
        //######################################################//
        //Notification action buttons
        NotificationCompat.Action replayAction = new NotificationCompat.Action(R.drawable.ic_reply,
                "Replay",
                null);
        NotificationCompat.Action shareAction = new NotificationCompat.Action(R.drawable.ic_share,
                "Share",
                PendingIntent.getActivity(
                        context,
                        0,
                        Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .putExtra(Intent.EXTRA_STREAM, bigText),
                                "Select item to share"),
                        PendingIntent.FLAG_UPDATE_CURRENT));


        //Generate the normal big test style notification for the phone/tablet/wear
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)                                              //Sound and vibration as per default settings
                .setSmallIcon(R.drawable.ic_notification_small)                                     //Small icon to display
                .setContentTitle(title)                                                             //Title of the notification
                .setContentText(bigText)                                                            //Message text
                .setPriority(NotificationCompat.PRIORITY_HIGH)                                      //Set the priority.
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.example_picture))        //set the large icon.
                .setTicker(title)                                                                   //This ticker will be visible to lower android versions
                .setAutoCancel(true)

                //Pending intent to fire when notification is clicked.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")), //Open Google :-)
                                PendingIntent.FLAG_UPDATE_CURRENT))

                //Set the big text style
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText)
                        .setBigContentTitle(title)
                        .setSummaryText("This is summary text"))

                //This notification action won't appear in wearable as we are going to separate actions for wearables later.
                .addAction(replayAction)                    //Replay action.
                .addAction(shareAction);                    //Share action will share big text when clicked


        //#######################################################//
        //Now we are going to add some wearable specific features//
        //#######################################################//
        //Generate the remote input to get the voice input from the wear devices.
        RemoteInput remoteInput = new RemoteInput.Builder(REMOTE_INPUT_LABEL)
                .setLabel("Replay by voice")        //Define the label to display
                .setChoices(new String[]{"Yes", "No", "I'm feeling lucky."})    //Define suggested responses. (Optional)
                .build();

        //Set the wear specific notification action button
        //This will user the remoteInput to get the input from the wear device and pass that input to the
        //WearReplayActivity. Where we can parse the text being said from the caller intent.
        NotificationCompat.Action wearReplay = new NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                "Replay",
                PendingIntent.getActivity(
                        context,
                        0,
                        new Intent(context, WearableReplayActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .addRemoteInput(remoteInput)    //add remote voice input
                .build();

        //Create WearableExtender object
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                .addAction(wearReplay)
                .setHintHideIcon(true)                  //Do not display phone's notification large icon
                .setBackground(BitmapFactory.decodeResource(res, R.drawable.example_picture));  //set the custom icon background


        //Add the wearable extend to the notification
        builder.extend(wearableExtender);

        //display the notification
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
     * {@link #notify(Context, String, String)}.
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
