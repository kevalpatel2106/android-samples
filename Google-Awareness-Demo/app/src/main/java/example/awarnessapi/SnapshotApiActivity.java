package example.awarnessapi;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This activity will demonstrate how to use snapshot apis.
 *
 * @see 'https://developers.google.com/awareness/android-api/snapshot-get-data'
 */
public class SnapshotApiActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
    private static final int GET_LOCATION_PERMISSION_REQUEST_CODE = 12345;
    private static final int GET_PLACE_PERMISSION_REQUEST_CODE = 123456;
    private static final int GET_WEATHER_PERMISSION_REQUEST_CODE = 1234567;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);

        buildApiClient();
    }

    /**
     * Build the google api client to use awareness apis.
     */
    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(SnapshotApiActivity.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        //Google API client connected.
        //ready to use awareness api
        callSnapShotGroupApis();
    }

    /**
     * This method will call all the snap shot group apis.
     */
    private void callSnapShotGroupApis() {
        //get info about user's current activity
        getCurrentActivity();

        //get the current state of the headphones.
        getHeadphoneStatus();

        //get current location. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }

        //get current place. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_PLACE_PERMISSION_REQUEST_CODE);
        } else {
            getPlace();
        }

        //get current weather conditions. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_WEATHER_PERMISSION_REQUEST_CODE);
        } else {
            getWeather();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case GET_LOCATION_PERMISSION_REQUEST_CODE://location permission granted
                    //noinspection MissingPermission
                    getLocation();
                    break;
                case GET_PLACE_PERMISSION_REQUEST_CODE://location permission granted
                    //noinspection MissingPermission
                    getPlace();
                    break;
                case GET_WEATHER_PERMISSION_REQUEST_CODE://location permission granted
                    //noinspection MissingPermission
                    getWeather();
                    break;
            }
        }
    }

    /**
     * Get the current weather condition at current location.
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getWeather() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get weather.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //parse and display current weather status
                        Weather weather = weatherResult.getWeather();
                        String weatherReport = "Temperature: " + weather.getTemperature(Weather.CELSIUS)
                                + "\nHumidity: " + weather.getHumidity();
                        ((TextView) findViewById(R.id.weather_status)).setText(weatherReport);
                    }
                });
    }

    /**
     * Get the nearby places using Snapshot apis. We are going to display only first 5 places to the user in the list.
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getPlace() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull final PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get places.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get the list of all like hood places
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();

                        // Show the top 5 possible location results.
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.current_place_container);
                        linearLayout.removeAllViews();
                        if (placeLikelihoodList != null) {
                            for (int i = 0; i < 5 && i < placeLikelihoodList.size(); i++) {
                                PlaceLikelihood p = placeLikelihoodList.get(i);

                                //add place row
                                View v = LayoutInflater.from(SnapshotApiActivity.this).inflate(R.layout.row_nearby_place, linearLayout, false);
                                ((TextView) v.findViewById(R.id.place_name)).setText(p.getPlace().getName());
                                ((TextView) v.findViewById(R.id.place_address)).setText(p.getPlace().getAddress());
                                linearLayout.addView(v);
                            }
                        } else {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get nearby places.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Get user's current location. We are also displaying Google Static map.
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getLocation() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get location.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get location
                        Location location = locationResult.getLocation();
                        ((TextView) findViewById(R.id.current_latlng)).setText(location.getLatitude() + ", " + location.getLongitude());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.latlng_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("as on: " + sdf.format(new Date(location.getTime())));

                        //Load the current map image from Google map
                        String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                                + location.getLatitude() + "," + location.getLongitude()
                                + "&zoom=20&size=400x250&key=" + getString(R.string.api_key);
                        Picasso.with(SnapshotApiActivity.this).load(url).into((ImageView) findViewById(R.id.current_map));
                    }
                });
    }

    /**
     * Check weather the headphones are plugged in or not? This is under snapshot api category.
     */
    private void getHeadphoneStatus() {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get headphone state.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();

                        //display the status
                        TextView headphoneStatusTv = (TextView) findViewById(R.id.headphone_status);
                        headphoneStatusTv.setText(headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in." : "Unplugged.");
                    }
                });
    }

    /**
     * Get current activity of the user. This is under snapshot api category.
     * Current activity and confidence level will be displayed on the screen.
     */
    private void getCurrentActivity() {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get the current activity.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();

                        //set the activity name
                        TextView activityName = (TextView) findViewById(R.id.probable_activity_name);
                        switch (probableActivity.getType()) {
                            case DetectedActivity.IN_VEHICLE:
                                activityName.setText("In vehicle");
                                break;
                            case DetectedActivity.ON_BICYCLE:
                                activityName.setText("On bicycle");
                                break;
                            case DetectedActivity.ON_FOOT:
                                activityName.setText("On foot");
                                break;
                            case DetectedActivity.RUNNING:
                                activityName.setText("Running");
                                break;
                            case DetectedActivity.STILL:
                                activityName.setText("Still");
                                break;
                            case DetectedActivity.TILTING:
                                activityName.setText("Tilting");
                                break;
                            case DetectedActivity.UNKNOWN:
                                activityName.setText("Unknown");
                                break;
                            case DetectedActivity.WALKING:
                                activityName.setText("Walking");
                                break;
                        }

                        //set the confidante level
                        ProgressBar confidenceLevel = (ProgressBar) findViewById(R.id.probable_activity_confidence);
                        confidenceLevel.setProgress(probableActivity.getConfidence());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("as on: " + sdf.format(new Date(ar.getTime())));
                    }
                });
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
}
