package sample.kevalpatel2106.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
                                "to type the replay manually.");
            }
        });
    }
}
