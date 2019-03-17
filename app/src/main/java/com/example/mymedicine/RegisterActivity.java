package com.example.mymedicine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    //*****************************************************************************************************
    //THIS CREATES AND CONNECTS THE UI
    //*****************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //This creates the button options and makes them clickable

        //Patient button
        Button mElderlyButton = (Button) findViewById(R.id.elderly_button);
        mElderlyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, RegisterElderlyActivity.class);
                startActivity(intent);
            }
        });

        //Doctor button
        Button mDoctorButton = (Button) findViewById(R.id.doctor_button);
        mDoctorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, RegisterDoctorFamilyActivity.class);
                startActivity(intent);
            }
        });

        //Relative button
        Button mFamilyButton = (Button) findViewById(R.id.family_button);
        mFamilyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, RegisterDoctorFamilyActivity.class);
                startActivity(intent);
            }
        });
    }
}

