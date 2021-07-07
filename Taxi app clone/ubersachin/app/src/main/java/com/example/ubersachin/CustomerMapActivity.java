package com.example.ubersachin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    private Button mRequest;    private Marker pickupMarker;
    Location mLastLocation;
    private LatLng pickupLocation;
    private GoogleMap mMap;
    LocationManager locationManager;
    private Boolean requestBol=false;


    android.location.LocationListener locationListener;

  public void Settings(View view){
      Intent intent = new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class);
      startActivity(intent);
      return;
  }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mRequest = findViewById(R.id.calluber);
    }
    private GeoQuery geoQuery;
    public void calluber(final View view) {

        if (requestBol){
            requestBol = false;



            if (driverFoundID != null){
                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                driverRef.setValue(true);
                driverFoundID = null;
                geoQuery.removeAllListeners();
                driverLocationRef.removeEventListener(driverLocationRefListener);

            }
            driverFound = false;
            radius = 1;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error removing the location from GeoFire: " + error);

                    } else {
                        System.out.println("Location removed on server successfully!");

                    }
                }
            });

            if(pickupMarker != null){
                pickupMarker.remove();
            }
            mRequest.setText("call Uber");

        }
        else {
            requestBol = true;
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    mMap.clear();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new
                            GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    //Do some stuff if you want to
                                    if (error != null) {
                                        System.err.println("There was an error saving the location to GeoFire: " + error);
                                    } else {
                                        System.out.println("Location saved on server successfully!");
                                    }
                                }
                            });

                    pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    pickupMarker=mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here"));
                    DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

                    GeoFire geoFire1 = new GeoFire(driverLocation);
                    pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    pickupMarker=mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here"));

                    geoQuery = geoFire1.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
                    geoQuery.removeAllListeners();
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            if (!driverFound) {
                                driverFound = true;
                                driverFoundID = key;

                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap map = new HashMap();
                                map.put("customerRideId", customerId);
                                driverRef.updateChildren(map);


                                mRequest.setText("Looking for Driver Location....");
                                getDriverLocation();

                            }
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            if (!driverFound) {
                                radius++;
                                System.out.println("radius is" + radius);
                                calluber(view);
                            }
                            else
                                getDriverLocation();
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mMap.clear();
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    Log.i("location", userLocation.toString());
                    pickupMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title("pickup"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                }
            }
        }
    }


    private DatabaseReference driverLocationRef;
    private Marker mDriverMarker;
    private ValueEventListener driverLocationRefListener ;
    private void getDriverLocation() {
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener =driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequest.setText("Driver Found");
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    mRequest.setText("Driver Found: " + String.valueOf(distance));
                    if(radius <100)
                        mRequest.setText("driver has arrived");

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

    }



    @Override
    protected void onStop() {
        super.onStop();

    }
}

