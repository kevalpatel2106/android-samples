package com.example.ubersachin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button mDriver,mCustomer;
    public void driverbut(View v) {
        Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }



    public void riderbut(View v) {
        Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDriver=findViewById(R.id.driver);
        mCustomer=findViewById(R.id.customer);





    }
}

