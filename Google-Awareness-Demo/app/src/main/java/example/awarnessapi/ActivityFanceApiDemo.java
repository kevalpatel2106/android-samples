package example.awarnessapi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

public class ActivityFanceApiDemo extends AppCompatActivity implements  View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String ACTIVITY_STILL_FENCE_KEY = "stillActivityFence";
    private static final String ACTIVITY_MOVING_FENCE_KEY = "movingActivityFence";
    private static final String FENCE_RECEIVER_ACTION = "action.activity.fence";

    private GoogleApiClient mGoogleApiClient;
    private TextView mActivityFenceStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_api_demo);
        mActivityFenceStatusTv = (TextView) findViewById(R.id.activity_fence_status);

        buildApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register the receiver to get notify
        registerReceiver(mActivityFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    /**
     * Build the google api client to use awareness apis.
     */
    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.register_activity_fence:
                registerActivityFence();
                break;
            case R.id.unregister_activity_fence:
                unregisterActivityFence();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister the receiver.
        unregisterReceiver(mActivityFenceReceiver);

        //unregister fence
        unregisterActivityFence();
    }

    /**
     * Register the user activity fence. This will register two fences.
     * 1. Fence to activate when user is still
     * 2. When user is walking.
     */
    private void registerActivityFence() {
        //generate fence
        AwarenessFence activityStillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        AwarenessFence activityMovingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);

        //generate pending intent
        PendingIntent fencePendingIntent = PendingIntent.getBroadcast(this,
                10001,
                new Intent(FENCE_RECEIVER_ACTION),
                0);

        //fence to activate when headphone is plugged in
        Awareness.FenceApi.updateFences(mGoogleApiClient, new FenceUpdateRequest.Builder()
                .addFence(ACTIVITY_STILL_FENCE_KEY, activityStillFence, fencePendingIntent).build())
                .setResultCallback(new ResultCallbacks<Status>() {
                    @Override
                    public void onSuccess(@NonNull final Status status) {
                        Toast.makeText(ActivityFanceApiDemo.this,
                                "Fence registered successfully. Move your device to see magic.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull final Status status) {
                        Toast.makeText(ActivityFanceApiDemo.this,
                                "Cannot register activity fence.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        //fence to activate when headphone is unplugged in
        Awareness.FenceApi.updateFences(mGoogleApiClient, new FenceUpdateRequest.Builder()
                .addFence(ACTIVITY_MOVING_FENCE_KEY, activityMovingFence, fencePendingIntent).build());
    }

    private void unregisterActivityFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(ACTIVITY_STILL_FENCE_KEY)
                        .removeFence(ACTIVITY_MOVING_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Toast.makeText(ActivityFanceApiDemo.this,
                        "Fence unregistered successfully.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Toast.makeText(ActivityFanceApiDemo.this,
                        "Cannot unregister fence.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        //Google API client connected.
        //ready to use awareness api
        findViewById(R.id.register_activity_fence).setOnClickListener(this);
        findViewById(R.id.unregister_activity_fence).setOnClickListener(this);
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

    /**
     * A {@link BroadcastReceiver} to be called when any of the awareness fence is activated.
     */
    private BroadcastReceiver mActivityFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), ACTIVITY_STILL_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:   //User is still
                        mActivityFenceStatusTv.setText("You are still.");
                        break;
                    case FenceState.FALSE:
                        mActivityFenceStatusTv.setText("You are moving. Keep moving.:-)");
                        break;
                    case FenceState.UNKNOWN:
                        mActivityFenceStatusTv.setText("Confused.:-(");
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), ACTIVITY_MOVING_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.FALSE:
                        mActivityFenceStatusTv.setText("You are still.");
                        break;
                    case FenceState.TRUE: //User is moving
                        mActivityFenceStatusTv.setText("You are moving. Keep moving.:-)");
                        break;
                    case FenceState.UNKNOWN:
                        mActivityFenceStatusTv.setText("Confused.:-(");
                        break;
                }
            }
        }
    };
}
