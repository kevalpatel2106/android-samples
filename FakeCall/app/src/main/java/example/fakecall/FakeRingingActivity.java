package example.fakecall;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class FakeRingingActivity extends AppCompatActivity {

    private String networkCarrier;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_ringing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView fakeName = (TextView)findViewById(R.id.chosenfakename);
        TextView fakeNumber = (TextView)findViewById(R.id.chosenfakenumber);

        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        networkCarrier = tm.getNetworkOperatorName();

        TextView titleBar = (TextView)findViewById(R.id.textView1);
        if(networkCarrier != null){
            titleBar.setText("Incoming call - " + networkCarrier);
        }else{
            titleBar.setText("Incoming call");
        }

        String callNumber = getContactNumber();
        String callName = getContactName();

        fakeName.setText(callName);
        fakeNumber.setText(callNumber);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();

        Button answerCall = (Button)findViewById(R.id.answercall);
        Button rejectCall = (Button)findViewById(R.id.rejectcall);

        answerCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mp.stop();
            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mp.stop();
                Intent homeIntent= new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
            }
        });
    }

    private String getContactNumber(){
        String contact = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if(mIntent != null){
            contact  = mIntent.getString("myfakenumber");
        }
        return contact;
    }

    private String getContactName(){
        String contactName = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if(mIntent != null){
            contactName  = mIntent.getString("myfakename");
        }
        return contactName;
    }

}