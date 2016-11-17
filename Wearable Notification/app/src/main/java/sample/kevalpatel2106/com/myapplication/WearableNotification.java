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

    private static final String GROUP_NOTIFICATION_KEY = "group_notification";

    /**
     * This is the unique identifier for the remote input on the android wear device.
     */
    public static final String REMOTE_INPUT_LABEL = "remote_input";

    /**
     * Generate the sample notification.
     */
    public static void notify(final Context context,
                              final String title,
                              final String bigText,
                              boolean groupNotification) {
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

        //Create pages and display them into the wear device notifications. This pages will never appear on phone.
        //Page 2
        Notification secondPagNotification = new NotificationCompat
                .Builder(context)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Page 2")
                        .setBigContentTitle("A lot of text for page 2..."))
                .build();
        //Page 3
        Notification thirdPagNotification = new NotificationCompat
                .Builder(context)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Page 3")
                        .setBigContentTitle("A lot of text for page 3..."))
                .build();

        //Create WearableExtender object
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                //Add wearable actions
                .addAction(wearReplay)

                //set all the pages
                .addPage(secondPagNotification)
                .addPage(thirdPagNotification)

                //set the custom icon background
                .setHintHideIcon(true)                  //Do not display phone's notification large icon
                .setBackground(BitmapFactory.decodeResource(res, R.drawable.example_picture));


        //Add the wearable extend to the notification
        builder.extend(wearableExtender);
        builder.setGroup(GROUP_NOTIFICATION_KEY);       //Set the unique key to group notification on wear

        //display the notification
        notify(context, builder.build());

        //Create summary notification to display the summary for group of the notification.
        if (groupNotification) {
            Notification summaryNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("4 new messages")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)                                      //Set the priority.
                    .setSmallIcon(R.drawable.ic_notification_small)                                     //Small icon to display
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.example_picture))
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Title 1")
                            .addLine("Title 2")
                            .addLine("Title 3")
                            .addLine("Title 4")
                            .setSummaryText("Total 4 messages"))
                    .setGroup(GROUP_NOTIFICATION_KEY)
                    .setGroupSummary(true)  //This indicates, this is not the normal notification. This is summary notification
                    .build();

            //display the notification
            notify(context, summaryNotification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, (int) System.currentTimeMillis(), notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }
}
