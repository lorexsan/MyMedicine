package com.example.mymedicine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DoctorFamilyHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_family_homepage);

        Button mAssignPatientButton = (Button) findViewById(R.id.assign_patient);
        mAssignPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the "assign" button is clicked, go to a screen for assigning patient
                Intent intent = new Intent(DoctorFamilyHomepageActivity.this, AssignPatientActivity.class);
                startActivity(intent);
            }
        });
    }
}
