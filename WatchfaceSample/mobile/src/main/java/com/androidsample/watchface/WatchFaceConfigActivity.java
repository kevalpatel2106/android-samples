package com.androidsample.watchface;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class WatchFaceConfigActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String[] mSupportedBg = new String[]{
            "#000000",
            "#FF0000",
            "#00FF00",
            "#0000FF",
    };
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectGoogleApiClient();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.color_picker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ColorListAdapter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    private void connectGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.MyViewHolder> {
        private String mSelectedColor;

        public ColorListAdapter() {
            mSelectedColor = getSharedPreferences("settings", Context.MODE_PRIVATE).getString("select_color", "#000000");
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(WatchFaceConfigActivity.this)
                    .inflate(R.layout.row_color, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.colorPreviewIv.setBackgroundColor(Color.parseColor(mSupportedBg[position]));
            holder.colorCodeTv.setText(mSupportedBg[position]);

            if (mSelectedColor.equals(mSupportedBg[position])) {
                holder.rowRoot.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.rowRoot.setBackgroundColor(Color.WHITE);
            }

            holder.rowRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedColor = mSupportedBg[position];

                    getSharedPreferences("settings", Context.MODE_PRIVATE)
                            .edit()
                            .putString("select_color", mSupportedBg[position])
                            .apply();

                    notifyWear(mSupportedBg[position]);

                    notifyDataSetChanged();
                }
            });
        }

        private void notifyWear(String newBgColor) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/bg_change");

            dataMapRequest.getDataMap().putString("new_color", newBgColor);

            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                            //check if the message is delivered?
                            //If the status is failed, that means that the currently device is
                            //not connected. The data will get deliver when phone gets connected to the watch.
                            Log.d("Data saving", dataItemResult.getStatus().isSuccess() ? "Success" : "Failed");
                        }
                    });
        }


        @Override
        public int getItemCount() {
            return mSupportedBg.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView colorPreviewIv;
            private TextView colorCodeTv;
            private View rowRoot;

            MyViewHolder(View itemView) {
                super(itemView);
                rowRoot = itemView.findViewById(R.id.row_root);
                colorCodeTv = (TextView) itemView.findViewById(R.id.color_code);
                colorPreviewIv = (ImageView) itemView.findViewById(R.id.color_preview);
            }
        }
    }
}
