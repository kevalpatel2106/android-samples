package com.sample.estimote;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    private TextView mMainTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainTv = (TextView) findViewById(R.id.main_tv);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                Log.d("beacons", beacons.size() + "");
                if (beacons.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Iterator<Beacon> beaconIterator = beacons.iterator();
                            double distance = 1000f;
                            while (beaconIterator.hasNext()) {
                                Beacon beacon = beaconIterator.next();
                                Log.i("beacons", "The first beacon I see is about " + beacon.getBluetoothAddress() + " meters away.");
                                if (beacon.getBluetoothAddress().equals("C9:FE:F5:0D:BB:3A"))
                                    distance = beacon.getDistance();
                            }

                            mMainTv.setText("Min distance: " + distance);
                        }
                    });

                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
}
