package com.androidsample.contentresolversample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    public static final String ARG_NAME_ID = "ARG_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().getIntExtra(ARG_NAME_ID, -1) < 0) {
            Toast.makeText(this, "Missing arguments.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


    }
}
