package com.androidsample.wearablenotification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.androidsample.wearablenotification.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WearableNotification.notify(MainActivity.this,
                        "This notification is on your android wear.",
                        "You can test this notification on the android wear device and also you " +
                                "can replay to the notification using voice input. " +
                                "But if you are using android wear emulator, you have " +
                                "to type the replay manually.", false);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Assign multiple notifications to see groupings of the notification
                //on wearable
                for (int i = 0; i < 4; i++) {
                    WearableNotification.notify(MainActivity.this,
                            "This notification(" + i + ") is on your android wear.",
                            "You can test this notification on the android wear device and also you " +
                                    "can replay to the notification using voice input. " +
                                    "But if you are using android wear emulator, you have " +
                                    "to type the replay manually.", i == 3);
                }
            }
        });
    }
}
