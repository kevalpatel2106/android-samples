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
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

public class HeadphoneFenceApiActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks {
    private static final String HEADPHONE_PLUG_FENCE_KEY = "headphonesPlugFence";
    private static final String HEADPHONE_UNPLUG_FENCE_KEY = "headphonesUnplugFence";
    private static final String FENCE_RECEIVER_ACTION = "action.headphone.fence";

    private GoogleApiClient mGoogleApiClient;
    private TextView mHeadPhoneStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_api);

        mHeadPhoneStatusTv = (TextView) findViewById(R.id.fence_status);

        buildApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register the receiver to get notify
        registerReceiver(mHeadPhoneFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    /**
     * Build the google api client to use awareness apis.
     */
    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(HeadphoneFenceApiActivity.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.register_fence:
                registerHeadphoneFence();
                break;
            case R.id.unregister_fence:
                unregisterHeadPhoneFence();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister the receiver.
        unregisterReceiver(mHeadPhoneFenceReceiver);

        //unregister fence
        unregisterHeadPhoneFence();
    }

    /**
     * Register the headphone status fence. This will register two fences.
     * 1. Fence to activate when headphones plugged in
     * 2. When headphones unplugged.
     */
    private void registerHeadphoneFence() {
        //generate fence
        AwarenessFence headphonePlugFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        AwarenessFence headphoneUnplugFence = HeadphoneFence.during(HeadphoneState.UNPLUGGED);

        //generate pending intent
        PendingIntent fencePendingIntent = PendingIntent.getBroadcast(this,
                10001,
                new Intent(FENCE_RECEIVER_ACTION),
                0);

        //fence to activate when headphone is plugged in
        Awareness.FenceApi.updateFences(mGoogleApiClient, new FenceUpdateRequest.Builder()
                .addFence(HEADPHONE_PLUG_FENCE_KEY, headphonePlugFence, fencePendingIntent).build())
                .setResultCallback(new ResultCallbacks<Status>() {
                    @Override
                    public void onSuccess(@NonNull final Status status) {
                        Toast.makeText(HeadphoneFenceApiActivity.this,
                                "Fence registered successfully. Plug in you head phones to see magic.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull final Status status) {
                        Toast.makeText(HeadphoneFenceApiActivity.this,
                                "Cannot register headphone fence.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        //fence to activate when headphone is unplugged in
        Awareness.FenceApi.updateFences(mGoogleApiClient, new FenceUpdateRequest.Builder()
                .addFence(HEADPHONE_UNPLUG_FENCE_KEY, headphoneUnplugFence, fencePendingIntent).build());
    }

    /**
     * Unregister all fences.
     */
    private void unregisterHeadPhoneFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(HEADPHONE_PLUG_FENCE_KEY)
                        .removeFence(HEADPHONE_UNPLUG_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Toast.makeText(HeadphoneFenceApiActivity.this,
                        "Fence unregistered successfully.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Toast.makeText(HeadphoneFenceApiActivity.this,
                        "Cannot unregister headphone fence.",
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

    /**
     * A {@link BroadcastReceiver} to be called when any of the awareness fence is activated.
     */
    private BroadcastReceiver mHeadPhoneFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), HEADPHONE_PLUG_FENCE_KEY)) {//response if from headphone plug in fence
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:   //Head phones are plugged in. (Check fence register code)
                        mHeadPhoneStatusTv.setText("Headphones connected.");
                        break;
                    case FenceState.FALSE:
                        mHeadPhoneStatusTv.setText("Headphones disconnected.");
                        break;
                    case FenceState.UNKNOWN:
                        mHeadPhoneStatusTv.setText("Confused.:-(");
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), HEADPHONE_UNPLUG_FENCE_KEY)) {//response if from headphone unplug fence
                switch (fenceState.getCurrentState()) {
                    case FenceState.FALSE:
                        mHeadPhoneStatusTv.setText("Headphones connected.");
                        break;
                    case FenceState.TRUE: //Head phones are unplugged. (Check fence register code)
                        mHeadPhoneStatusTv.setText("Headphones disconnected.");
                        break;
                    case FenceState.UNKNOWN:
                        mHeadPhoneStatusTv.setText("Confused.:-(");
                        break;
                }
            }
        }
    };
}
