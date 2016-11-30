package example.awarnessapi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * This activity will demonstrate use awareness api and combine multiple conditions.
 *
 * In this we are going to use awareness apis to change your phone profile to silent when you satisfy below 3 conditions.
 * 1. particular location. (e.g. your work place)
 * 2. particular interval of time. (e.g. your work hours)
 * 3. particular day of week. (e.g. your work days)
 * see {@link #registerFence()} to more detail.
 */
public class CombineFenceApiActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String COMBINE_FENCE_ENTERING_KEY = "entringCombineFence";
    private static final String FENCE_RECEIVER_ACTION = "action.combine.fence";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12345678;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_fence_api);

        mStatusTv = (TextView) findViewById(R.id.fence_status);

        buildApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register the receiver to get notify
        registerReceiver(mFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    /**
     * Build the google api client to use awareness apis.
     */
    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(CombineFenceApiActivity.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.register_fence:
                //Check for the location permission. We need them to generate location fence.
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    registerFence();
                }
                break;
            case R.id.unregister_fence:
                unregisterFence();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST_CODE://location permission granted
                    //noinspection MissingPermission
                    registerFence();
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister the receiver.
        unregisterReceiver(mFenceReceiver);

        //unregister fence
        unregisterFence();
    }

    /**
     * Register fences to get notified at particular condition.
     * Here we are using multiple fences and also combining them.
     */
    private void registerFence() {
        /**
         * This is location fence will trigger while entering the into the given location.
         */
        //noinspection MissingPermission
        AwarenessFence locationFence = LocationFence.in(
                23.0756607,    //Latitude of place
                72.5253209,    //Longitude of place
                50.00,         //Radius in meters
                5 * 1000);     //Wait for the five seconds

        /**
         * This time fence will trigger between 10:30AM to 7:30PM on Monday to Friday.
         * So, generate 5 time fences for each day and apply or to them.
         */
        ArrayList<AwarenessFence> timeFences = new ArrayList<>(6);
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0://Register for Monday
                    timeFences.add(TimeFence
                            .inMondayInterval(TimeZone.getDefault(), 10L * 30L * 60L * 1000L, 20L * 60L * 60L * 1000L)); //10:30AM - 7:30PM
                    break;
                case 1: //Register for Tuesday
                    timeFences.add(TimeFence
                            .inTuesdayInterval(TimeZone.getDefault(), 10L * 30L * 60L * 1000L, 20L * 60L * 60L * 1000L)); //10:30AM - 7:30PM
                    break;
                case 2://Register for Wednesday
                    timeFences.add(TimeFence
                            .inWednesdayInterval(TimeZone.getDefault(), 10L * 30L * 60L * 1000L, 20L * 60L * 60L * 1000L)); //10:30AM - 7:30PM
                    break;
                case 3://Register for Thursday
                    timeFences.add(TimeFence
                            .inThursdayInterval(TimeZone.getDefault(), 10L * 30L * 60L * 1000L, 20L * 60L * 60L * 1000L)); //10:30AM - 7:30PM
                    break;
                case 4://Register for Friday
                    timeFences.add(TimeFence
                            .inFridayInterval(TimeZone.getDefault(), 10L * 30L * 60L * 1000L, 20L * 60L * 60L * 1000L)); //10:30AM - 7:30PM
                    break;
            }
        }
        AwarenessFence oredTimeFences = AwarenessFence.or(timeFences);

        /**
         * Now apply and fence to location fence and OR-ED time fences.
         */
        AwarenessFence andFence = AwarenessFence.and(locationFence, oredTimeFences);

        //generate pending intent to call when condition appears
        PendingIntent fencePendingIntent = PendingIntent.getBroadcast(this,
                10001,
                new Intent(FENCE_RECEIVER_ACTION),
                0);

        //fence to activate when headphone is plugged in
        Awareness.FenceApi.updateFences(mGoogleApiClient, new FenceUpdateRequest.Builder()
                .addFence(COMBINE_FENCE_ENTERING_KEY, andFence, fencePendingIntent).build())
                .setResultCallback(new ResultCallbacks<Status>() {
                    @Override
                    public void onSuccess(@NonNull final Status status) {
                        Toast.makeText(CombineFenceApiActivity.this,
                                "Fence registered successfully.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull final Status status) {
                        Toast.makeText(CombineFenceApiActivity.this,
                                "Cannot register fence.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * unregister fence.
     */
    private void unregisterFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(COMBINE_FENCE_ENTERING_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Toast.makeText(CombineFenceApiActivity.this,
                        "Fence unregistered successfully.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Toast.makeText(CombineFenceApiActivity.this,
                        "Cannot unregister fence.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        //Google API client connected.
        //ready to use awareness api
        findViewById(R.id.register_fence).setOnClickListener(this);
        findViewById(R.id.unregister_fence).setOnClickListener(this);
    }

    @Override
    public void onConnectionSuspended(final int i) {
        new AlertDialog.Builder(this)
                .setMessage("Cannot connect to google api services.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        finish();
                    }
                }).show();
    }

    private BroadcastReceiver mFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), COMBINE_FENCE_ENTERING_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        mStatusTv.setText("You are at work.");
                        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        break;
                    case FenceState.FALSE:
                        mStatusTv.setText("You are not at work.");
                        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                    case FenceState.UNKNOWN:
                        mStatusTv.setText("Confused.:-(");
                        break;
                }
            }
        }
    };
}
