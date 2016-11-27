package example.awarnessapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Tutorial : 'https://inthecheesefactory.com/blog/google-awareness-api-in-action/en'
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //snap shot api demo
        findViewById(R.id.snap_shot_api_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(LaunchActivity.this, SnapshotApiActivity.class));
            }
        });

        //fence api demo
        findViewById(R.id.headphone_fence_api_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(LaunchActivity.this, HeadphoneFenceApiActivity.class));
            }
        });

        //activity recognition fence api demo
        findViewById(R.id.activity_fence_api_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(LaunchActivity.this, ActivityFanceApiDemo.class));
            }
        });

        //combine fence api demo
        findViewById(R.id.combine_fence_api_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(LaunchActivity.this, CombineFenceApiActivity.class));
            }
        });
    }
}
