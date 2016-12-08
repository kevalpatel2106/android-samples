package com.sample.kaleidoscope;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.activity_main);

        KaleidoscopeView kaleidoscopeView = new KaleidoscopeView(this, 6, 6);
        rootLayout.addView(kaleidoscopeView);
    }
}
