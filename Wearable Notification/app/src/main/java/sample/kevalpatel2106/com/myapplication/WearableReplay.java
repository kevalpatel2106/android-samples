package sample.kevalpatel2106.com.myapplication;

import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.Toast;

public class WearableReplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_replay);

        CharSequence replayString;

        //get the voice replays
        Bundle bundle = RemoteInput.getResultsFromIntent(getIntent());
        if (bundle != null) {
            replayString = bundle.getCharSequence(WearableNotification.REMOTE_INPUT_LABEL);
        } else {
            replayString = "No replay from the response.";
        }

        Toast.makeText(this, replayString, Toast.LENGTH_LONG).show();

        WearableNotification.cancel(this);

        finish();
    }
}
